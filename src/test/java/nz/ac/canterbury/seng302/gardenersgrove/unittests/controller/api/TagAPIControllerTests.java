package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController.SearchTagsResult;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;

class TagAPIControllerTests {
    private GardenService gardenService;
    private TagService tagService;
    private Authentication authentication;

    private TagAPIController controller;

    private ObjectMapper jsonObjectMapper;
    private GardenUser user;
    private Garden garden;

    @BeforeEach
    void setUp() {
        gardenService = mock(GardenService.class);
        tagService = mock(TagService.class);
        authentication = mock(Authentication.class);

        controller = new TagAPIController(gardenService, tagService);

        jsonObjectMapper = new ObjectMapper();
        user = new GardenUser();
        user.setId(1L);
        garden = new Garden();
        garden.setId(1L);
        garden.setOwner(user);
    }

    /**
     * Helper method to create a list of tags
     */
    private List<Tag> tags(String... names) {
        return Arrays.stream(names).map(Tag::new).toList();
    }

    @Test
    void givenGardenIsOurs_whenSetGardenTags_thenUpdatesTags() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));

        ResponseEntity<Object> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(tagService).updateGardenTags(eq(garden), assertArg(tag -> {
            assertEquals(2, tag.size());
            assertEquals("Red", tag.get(0));
            assertEquals("Green", tag.get(1));
        }));
    }

    @Test
    void givenGardenDoesNotExist_whenSetGardenTags_thenReturnsNotFound() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(404, response.getStatusCode().value());
        verify(tagService, times(0)).updateGardenTags(any(), any());
    }

    @Test
    void givenGardenIsNotOurs_whenSetGardenTags_thenReturnsForbidden() {
        when(authentication.getPrincipal()).thenReturn(2L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));

        ResponseEntity<Object> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(403, response.getStatusCode().value());
        verify(tagService, times(0)).updateGardenTags(any(), any());
    }

    @Test
    void givenTagsExist_whenSearchTags_thenReturnsTags() throws JsonProcessingException {
        when(tagService.getTagsByPrefix("gre")).thenReturn(tags("green", "Green", "GREEN", "grey", "Grenwich"));

        ResponseEntity<SearchTagsResult> response = controller.searchTags("gre");
        SearchTagsResult body = response.getBody();
        String json = jsonObjectMapper.writeValueAsString(body);

        // Note that the tags are sorted, first by case-insensitive alphabetical order,
        // then by case-sensitive order
        assertEquals(
                "{\"results\":[{\"formatted\":\"GREEN\"},{\"formatted\":\"Green\"},{\"formatted\":\"green\"},{\"formatted\":\"Grenwich\"},{\"formatted\":\"grey\"}]}",
                json);
    }

    @Test
    void givenTagDoesntExist_whenSearchTags_thenReturnsEmpty() throws JsonProcessingException {
        ResponseEntity<SearchTagsResult> response = controller.searchTags("this-tag-doesnt-exist");

        SearchTagsResult body = response.getBody();
        String json = jsonObjectMapper.writeValueAsString(body);

        assertEquals("{\"results\":[]}", json);
    }
}
