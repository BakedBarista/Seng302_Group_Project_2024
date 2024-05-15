package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import java.util.List;
import java.util.Optional;

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
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;

@RestController
@RequestMapping("/api")
public class TagAPIController {
    final Logger logger = LoggerFactory.getLogger(TagAPIController.class);

    private GardenService gardenService;
    private TagService tagService;

    public TagAPIController(GardenService gardenService, TagService tagService) {
        this.gardenService = gardenService;
        this.tagService = tagService;
    }

    @PutMapping("/gardens/{gardenId}/tags")
    public ResponseEntity<Object> setGardenTags(
            @PathVariable Long gardenId,
            @RequestBody List<String> tags,
            Authentication authentication) {
        logger.info("Setting tags for garden {}", gardenId);

        Optional<Garden> gardenOptional = gardenService.getGardenById(gardenId);
        if (gardenOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Garden garden = gardenOptional.get();
        if (!garden.getOwner().getId().equals(authentication.getPrincipal())) {
            return ResponseEntity.status(403).build();
        }

        tagService.updateGardenTags(garden, tags);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tag-autocomplete")
    public ResponseEntity<SearchTagsResult> searchTags(@RequestParam String currentValue) {
        // TODO: fetch from database

        List<String> tags = List.of("wild", "berries", "green");
        List<String> filteredTags = tags.stream()
                .filter(tag -> tag.toLowerCase().startsWith(currentValue.toLowerCase())).toList();

        return ResponseEntity.ok(new SearchTagsResult(
                filteredTags.stream().map(TagEntry::new).toList()));
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

        public TagEntry(String formatted) {
            this.formatted = formatted;
        }

        public String getFormatted() {
            return formatted;
        }
    }
}
