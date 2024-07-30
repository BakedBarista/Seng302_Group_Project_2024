package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

@RestController
@RequestMapping("/api")
public class TagAPIController {
    final Logger logger = LoggerFactory.getLogger(TagAPIController.class);

    private GardenService gardenService;
    private TagService tagService;
    private StrikeService strikeService;
    private GardenUserService gardenUserService;


    public TagAPIController(GardenService gardenService, TagService tagService, StrikeService strikeService, GardenUserService gardenUserService) {
        this.gardenService = gardenService;
        this.tagService = tagService;
        this.strikeService = strikeService;
        this.gardenUserService = gardenUserService;
    }

    /**
     * Sets the tags for a garden.
     *
     * @param gardenId The ID of the garden.
     * @param tags The tags to set.
     * @param authentication The authentication object.
     * @return A response entity indicating the result of the operation.
     */
    @PutMapping("/gardens/{gardenId}/tags")
    public ResponseEntity<String> setGardenTags(
            @PathVariable Long gardenId,
                @RequestBody List<String> tags,
            Authentication authentication) {
        logger.info("Setting tags for garden {}", gardenId);
        GardenUser user = gardenUserService.getUserById((Long) authentication.getPrincipal());

        Optional<Garden> gardenOptional = gardenService.getGardenById(gardenId);
        if (gardenOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Garden garden = gardenOptional.get();
        if (!garden.getOwner().getId().equals(authentication.getPrincipal())) {
            return ResponseEntity.status(403).build();
        }

        String message;
        List<String> strippedTags = new ArrayList<>();
        for (String tag : tags) {
            String strippedTag = tag.strip();
            if (tag.isBlank()) {
                message = "Tag cannot be blank";
                return ResponseEntity.status(422).body(message);
            }
            strippedTags.add(strippedTag);
        }
        try {
            tagService.updateGardenTags(garden, strippedTags);
        } catch (ProfanityDetectedException e) {
            logger.info("profanity detected in tag");
            StrikeService.AddStrikeResult result = strikeService.addStrike(user);

            switch (result) {
                case WARNING:
                    message = "You have added an inappropriate tag for the fifth time. One more strike and your account will be blocked.";
                    break;
                case BLOCK:
                    return ResponseEntity.status(401).build();
                default:
                    message = "Tag is inappropriate";
                    break;
            }
            return ResponseEntity.status(422).body(message);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Searches for existing tags that start with the given string. Intended for use
     * with the autocomplete feature.
     *
     * @param currentValue The string to search for.
     * @return A list of tags that start with the given string.
     */
    @GetMapping("/tag-autocomplete")
    public ResponseEntity<SearchTagsResult> searchTags(@RequestParam String currentValue) {
        List<Tag> tags = tagService.getTagsByPrefix(currentValue);

        return ResponseEntity.ok(new SearchTagsResult(
                tags.stream().sorted().map(TagEntry::new).toList()));
    }

    public static class SearchTagsResult {
        private List<TagEntry> results;

        public SearchTagsResult(List<TagEntry> results) {
            this.results = results;
        }

        public List<TagEntry> getResults() {
            return results;
        }
    }

    public static class TagEntry {
        private String formatted;

        public TagEntry(Tag tag) {
            this.formatted = tag.getName();
        }

        public String getFormatted() {
            return formatted;
        }
    }

}
