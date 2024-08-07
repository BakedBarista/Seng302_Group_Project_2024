package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;

import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceIntegrationTests {
    @Autowired
    private TagService tagService;

    @Autowired
    private GardenUserRepository userRepository;
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private TagRepository tagRepository;

    private static Tag redTag;
    private static Tag greenTag;
    private static Tag blueTag;
    private static Tag greyTag;
    private static Garden garden;
    private static boolean isSetup = false;

    // Even though this should only be run once, it needs to be a BeforeEach to be
    // able to access the tagService.
    @BeforeEach
    void setUp() {
        if (isSetup) {
            return;
        }

        redTag = new Tag("red");
        tagRepository.save(redTag);
        greenTag = new Tag("green");
        tagRepository.save(greenTag);
        blueTag = new Tag("blue");
        tagRepository.save(blueTag);
        greyTag = new Tag("GREY");
        tagRepository.save(greyTag);

        GardenUser user = new GardenUser("Test", "User", "jdo.asdf@gmail.com", "password", LocalDate.of(1970, 1, 1));
        userRepository.save(user);

        garden = new Garden("Test garden", null, null, null, "Test city", "Test country", null, null, null, null, null);
        garden.setOwner(user);
        garden.getTags().add(redTag);
        garden.getTags().add(greenTag);
        gardenRepository.save(garden);

        isSetup = true;
    }

    @Test
    void givenNoMatchingTagsExist_whenGetTagsByPrefix_thenReturnsEmptyList() {
        List<Tag> tags = tagService.getTagsByPrefix("purple");

        assertEquals(0, tags.size());
    }

    @Test
    void givenMatchingTagsExist_whenGetTagsByPrefix_thenReturnsTags() {
        List<Tag> tags = tagService.getTagsByPrefix("gre");

        assertEquals(2, tags.size());
        tags.sort(null);
        assertEquals("green", tags.get(0).getName());
        assertEquals("GREY", tags.get(1).getName());
    }

    @Test
    void givenNoTagExists_whenGetTag_thenReturnsNull() {
        Tag tag = tagService.getTag("purple");

        assertNull(tag);
    }

    @Test
    void givenTagExists_whenGetTag_thenReturnsTag() {
        Tag tag = tagService.getTag("red");

        assertEquals("red", tag.getName());
    }

    @Test
    void givenNoTagExists_whenGetOrCreateTag_thenCreatesTag() throws ProfanityDetectedException {
        Tag tag = tagService.getOrCreateTag("pink");

        assertEquals("pink", tag.getName());
        assertNotNull(tagService.getTag("pink"));
    }

    @Test
    void givenTagExists_whenGetOrCreateTag_thenReturnsTag() throws ProfanityDetectedException {
        Tag tag = tagService.getOrCreateTag("red");

        assertEquals("red", tag.getName());
    }

    @Test
    void givenGardenHasTags_whenUpdateGardenTags_thenUpdatesTags() throws ProfanityDetectedException {
        List<String> tagNames = List.of("red", "blue");

        tagService.updateGardenTags(garden, tagNames);

        assertEquals(2, garden.getTags().size());
        assertTrue(garden.getTags().contains(redTag));
        assertTrue(garden.getTags().contains(blueTag));
    }

    @Test
    void givenTagNameTooLong_whenGetOrCreateTag_thenTagNotCreated() throws ProfanityDetectedException {
        String longTag = "Amazing wonderful peaceful garden";

        assertNull(tagService.getOrCreateTag(longTag));
    }

    @Test
    void givenTagHasInvalidCharacters_whenGetOrCreateTag_thenTagNotCreated() throws ProfanityDetectedException {
        String invalidCharTag = "Cool!";

        assertNull(tagService.getOrCreateTag(invalidCharTag));
    }

    @Test
    void givenTagsHasProfanity_whenGetOrCreateTag_thenExceptionIsThrown() {
        assertThrows(ProfanityDetectedException.class, () -> tagService.getOrCreateTag("fuck"));
    }
}
