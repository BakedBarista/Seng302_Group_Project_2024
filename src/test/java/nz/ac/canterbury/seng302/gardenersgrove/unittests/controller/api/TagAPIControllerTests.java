package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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

class TagAPIControllerTests {
    private GardenService gardenService;
    private TagService tagService;
    private Authentication authentication;
    private TagAPIController controller;
    private ObjectMapper jsonObjectMapper;
    private GardenUser user;
    private Garden garden;
    private GardenUserService gardenUserService;
    private StrikeService strikeService;

    @BeforeEach
    void setUp() {
        gardenService = mock(GardenService.class);
        tagService = mock(TagService.class);
        authentication = mock(Authentication.class);
        gardenUserService = mock(GardenUserService.class);
        strikeService = mock(StrikeService.class);

        controller = new TagAPIController(gardenService, tagService, strikeService, gardenUserService);

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
    void givenGardenIsOurs_whenSetGardenTags_thenUpdatesTags() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(tagService).updateGardenTags(eq(garden), assertArg(tag -> {
            assertEquals(2, tag.size());
            assertEquals("Red", tag.get(0));
            assertEquals("Green", tag.get(1));
        }));
    }

    @Test
    void givenGardenDoesNotExist_whenSetGardenTags_thenReturnsNotFound() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(404, response.getStatusCode().value());
        verify(tagService, times(0)).updateGardenTags(any(), any());
    }

    @Test
    void givenGardenIsNotOurs_whenSetGardenTags_thenReturnsForbidden() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(2L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

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

    @Test
    void givenTagsContainProfanity_whenAddTags_thenReturnErrorMessage() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        //Alternative syntax because updateGardenTags returns void
        doThrow(new ProfanityDetectedException()).when(tagService).updateGardenTags(any(), any());
        when(strikeService.addStrike(gardenUserService.getCurrentUser())).thenReturn(StrikeService.AddStrikeResult.NO_ACTION);

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Tag is inappropriate", response.getBody());
        verify(tagService, times(1)).updateGardenTags(any(), any());
    }

    @Test
    void givenTagsContainProfanity_andUserIsDueAWarning_whenAddTags_thenReturnErrorMessage() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        //Alternative syntax because updateGardenTags returns void
        doThrow(new ProfanityDetectedException()).when(tagService).updateGardenTags(any(), any());
        when(strikeService.addStrike(gardenUserService.getCurrentUser())).thenReturn(StrikeService.AddStrikeResult.WARNING);

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("You have added an inappropriate tag for the fifth time. One more strike and your account will be blocked.", response.getBody());
        verify(tagService, times(1)).updateGardenTags(any(), any());
    }

    @Test
    void givenTagsContainProfanity_andUserIsDueToBeBlocked_whenAddTags_thenReturnErrorMessage() throws ProfanityDetectedException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        //Alternative syntax because updateGardenTags returns void
        doThrow(new ProfanityDetectedException()).when(tagService).updateGardenTags(any(), any());
        when(strikeService.addStrike(gardenUserService.getCurrentUser())).thenReturn(StrikeService.AddStrikeResult.BLOCK);

        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("Red", "Green"), authentication);

        assertEquals(401, response.getStatusCode().value());
        verify(tagService, times(1)).updateGardenTags(any(), any());
    }


    @Test
    void givenTagIsBlank_whenAddTags_thenReturnErrorMessage() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        ResponseEntity<String> response = controller.setGardenTags(1L, List.of(" "), authentication);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Tag cannot be blank", response.getBody());
    }

    @Test
    void givenMultipleTagsWithBlank_whenAddTags_thenReturnErrorMessage() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        ResponseEntity<String> response = controller.setGardenTags(1L, List.of("fruit"," ", "banana"), authentication);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Tag cannot be blank", response.getBody());
    }

    @Test
    void givenMultipleBlankTags_whenAddTags_thenReturnErrorMessage() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        ResponseEntity<String> response = controller.setGardenTags(1L, List.of(" "," ", " "), authentication);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Tag cannot be blank", response.getBody());
    }

    @Test
    void givenTagWithLeadingSpaces_whenAddTags_thenReturnOK() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        ResponseEntity<String> response = controller.setGardenTags(1L, List.of(" fruit"," dwmkw mkdwkmd", "banana "), authentication);

        assertEquals(200, response.getStatusCode().value());
    }
}
