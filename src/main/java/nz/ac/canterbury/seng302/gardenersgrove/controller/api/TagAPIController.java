package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TagAPIController {
    final Logger logger = LoggerFactory.getLogger(TagAPIController.class);

    @PutMapping("/gardens/{gardenId}/tags")
    public ResponseEntity<Object> setGardenTags(
            @PathVariable Long gardenId,
            @RequestBody List<String> tags) {
        logger.info("Setting tags for garden {}", gardenId);

        logger.info(String.join(", ", tags));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tag-autocomplete")
    public ResponseEntity<SearchTagsResult> searchTags(@RequestParam String currentValue) {
        // TODO: fetch from database

        List<String> tags = List.of("wild", "berries", "green");
        List<String> filteredTags = tags.stream()
                .filter(tag -> tag.toLowerCase().startsWith(currentValue.toLowerCase())).toList();

        return ResponseEntity.ok(new SearchTagsResult(
                filteredTags.stream().map(Tag::new).toList()));
    }

    public static class SearchTagsResult {
        private List<Tag> results;

        public SearchTagsResult(List<Tag> results) {
            this.results = results;
        }

        public List<Tag> getResults() {
            return results;
        }
    }

    public static class Tag {
        private String formatted;

        public Tag(String formatted) {
            this.formatted = formatted;
        }

        public String getFormatted() {
            return formatted;
        }
    }
}
