package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TagAPIController {
    final Logger logger = LoggerFactory.getLogger(TagAPIController.class);

    @GetMapping("/tag-autocomplete")
    public ResponseEntity<Object> searchTags(@RequestParam String currentValue) {
        // TODO: fetch from database

        List<String> tags = List.of("Hello, world!", "Goodbye, world!");
        List<String> filteredTags = tags.stream()
                .filter(tag -> tag.toLowerCase().startsWith(currentValue.toLowerCase())).toList();

        return ResponseEntity.ok(new SearchTagsResult(
                filteredTags.stream().map(Tag::new).toList()));
    }

    private static class SearchTagsResult {
        private List<Tag> results;

        public SearchTagsResult(List<Tag> results) {
            this.results = results;
        }
    }

    private static class Tag {
        private String formatted;

        public Tag(String formatted) {
            this.formatted = formatted;
        }
    }
}
