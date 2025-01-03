package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.TAG_MAX_LEN;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.TAG_REGEX;

/**
 * Service to retrieve and manipulate tags.
 */
@Service
public class TagService {
    private TagRepository tagRepository;
    private GardenService gardenService;

    private ProfanityService profanityService;

    public TagService(TagRepository tagRepository, GardenService gardenService, ProfanityService profanityService) {
        this.tagRepository = tagRepository;
        this.gardenService = gardenService;
        this.profanityService = profanityService;
    }

    /**
     * Gets all tags with the given prefix.
     *
     * @param currentValue The prefix to search for.
     * @return A list of tags that have ever been used with the given prefix.
     */
    public List<Tag> getTagsByPrefix(String currentValue) {
        return tagRepository.findByNameStartingWithIgnoreCase(currentValue);
    }

    /**
     * Gets the tag with the given name.
     *
     * @param name The name of the tag.
     * @return The tag with the given name, or null if no such tag exists.
     */
    public Tag getTag(String name) {
        Optional<Tag> tag = tagRepository.findByName(name);
        if (tag.isEmpty()) {
            return null;
        }
        return tag.get();
    }

    /**
     * Gets the tag with the given name, creating it if it does not exist. If a new tag is created, then it is saved before being returned.
     *
     * @param name The name of the tag.
     * @return A tag with the given name.
     */
    public Tag getOrCreateTag(String name) throws ProfanityDetectedException {
        if (!isValidTag(name)) {
            return null;
        }

        Tag tag = getTag(name);
        if (tag == null) {
            tag = new Tag(name);
            tagRepository.save(tag);
        }
        return tag;
    }

    /**
     * Validates the tag if it contains invalid characters and checks the length
     *
     * @param name The name of the tag.
     * @return True if name is valid, otherwise false
     */
    public boolean isValidTag(String name) throws ProfanityDetectedException {
        boolean profanityExists = !(profanityService.badWordsFound(name).isEmpty());

        if (profanityExists) {
            throw new ProfanityDetectedException();
        }

        return (name.matches(TAG_REGEX) && name.length() <= TAG_MAX_LEN);
    }

    /**
     * Updates the tags on a garden to match the given list of tag names.
     *
     * @param garden The garden to update.
     * @param tagNames The names of the tags to set on the garden.
     */
    public void updateGardenTags(Garden garden, List<String> tagNames) throws ProfanityDetectedException {
        // Remove tags that are not in the new list
        for (Tag tag : List.copyOf(garden.getTags())) {
            if (tagNames.stream().noneMatch(t -> t.equals(tag.getName()))) {
                garden.getTags().remove(tag);
            }
        }

        // Add new tags
        for (String tagName : tagNames) {
            Tag tag = getOrCreateTag(tagName);
            garden.getTags().add(tag);
        }

        gardenService.addGarden(garden);
    }


}
