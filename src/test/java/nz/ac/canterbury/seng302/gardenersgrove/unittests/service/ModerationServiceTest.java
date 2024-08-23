package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;

public class ModerationServiceTest {

    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private ModerationService moderationService;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        restTemplate = mock(RestTemplate.class);
        moderationService = new ModerationService(null, objectMapper, restTemplate);
    }

    @Test
    public void givenEmptyBody_ReturnFalse() {
        Assertions.assertFalse(moderationService.checkIfDescriptionIsFlagged(""));
    }

    @Test
    public void givenQuotesInDescription_EscapesQuotes() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(null));

        moderationService.checkIfDescriptionIsFlagged("\"organic\"");

        verify(restTemplate).postForEntity(anyString(), assertArg((HttpEntity<String> requestEntity) -> {
            assertEquals("{\"input\":\"\\\"organic\\\"\"}", requestEntity.getBody());
        }), any());
    }
}
