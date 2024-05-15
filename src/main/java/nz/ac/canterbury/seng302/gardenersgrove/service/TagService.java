package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;

/**
 * Service to retrieve and manipulate tags.
 */
@Service
public class TagService {
    private TagRepository tagRepository;
    private GardenService gardenService;

    public TagService(TagRepository tagRepository, GardenService gardenService) {
        this.tagRepository = tagRepository;
        this.gardenService = gardenService;
    }

    public Tag getTag(String name) {
        Optional<Tag> tag = tagRepository.findByName(name);
        if (tag.isEmpty()) {
            return null;
        }
        return tag.get();
    }

    public Tag getOrCreateTag(String name) {
        Tag tag = getTag(name);
        if (tag == null) {
            tag = new Tag(name);
            tagRepository.save(tag);
        }
        return tag;
    }

    public void updateGardenTags(Garden garden, Collection<String> tagNames) {
        // Remove tags that are not in the new list
        for (Tag tag : garden.getTags()) {
            if (!tagNames.stream().anyMatch(t -> t.equals(tag.getName()))) {
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
