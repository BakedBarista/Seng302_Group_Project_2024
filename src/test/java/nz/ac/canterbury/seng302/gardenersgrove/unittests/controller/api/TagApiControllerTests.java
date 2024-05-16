package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.TagAPIController.SearchTagsResult;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;

public class TagApiControllerTests {
    private GardenService gardenService;
    private TagService tagService;
    private TagAPIController controller;
    private ObjectMapper jsonObjectMapper;

    @BeforeEach
    void setUp() {
        gardenService = mock(GardenService.class);
        tagService = mock(TagService.class);

        controller = new TagAPIController(gardenService, tagService);

        jsonObjectMapper = new ObjectMapper();
    }

    /**
     * Helper method to create a list of tags
     */
    private List<Tag> tags(String... names) {
        return Arrays.stream(names).map(Tag::new).toList();
    }

    @Test
    void givenTagsExist_whenSearchTags_thenReturnsTags() throws JsonProcessingException {
        when(tagService.getTagsByPrefix("gre")).thenReturn(tags("green", "Green", "GREEN", "grey", "Grenwich"));

        ResponseEntity<SearchTagsResult> response = controller.searchTags("gre");
        SearchTagsResult body = response.getBody();
        String json = jsonObjectMapper.writeValueAsString(body);

        // Note that the tags are sorted, first by case-insensitive alphabetical order, then by case-sensitive order
        assertEquals("{\"results\":[{\"formatted\":\"GREEN\"},{\"formatted\":\"Green\"},{\"formatted\":\"green\"},{\"formatted\":\"Grenwich\"},{\"formatted\":\"grey\"}]}", json);
    }

    @Test
    void givenTagDoesntExist_whenSearchTags_thenReturnsEmpty() throws JsonProcessingException {
        ResponseEntity<SearchTagsResult> response = controller.searchTags("this-tag-doesnt-exist");

        SearchTagsResult body = response.getBody();
        String json = jsonObjectMapper.writeValueAsString(body);

        assertEquals("{\"results\":[]}", json);
    }
}
