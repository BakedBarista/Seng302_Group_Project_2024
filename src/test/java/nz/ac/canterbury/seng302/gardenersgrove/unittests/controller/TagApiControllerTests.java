package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController.SearchTagsResult;

public class TagApiControllerTests {
    private TagAPIController controller;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        controller = new TagAPIController();
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenTagsExist_whenSearchTags_thenReturnsTags() throws JsonProcessingException {
        ResponseEntity<SearchTagsResult> response = controller.searchTags("green");

        SearchTagsResult body = response.getBody();
        String json = objectMapper.writeValueAsString(body);
        JsonNode jsonNode = objectMapper.readTree(json);

        assertTrue(jsonNode.get("results").isArray());
        assertTrue(jsonNode.get("results").size() > 0);
        assertTrue(jsonNode.get("results").get(0).isObject());
        assertTrue(jsonNode.get("results").get(0).get("formatted").isTextual());
    }
}
