package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WikiDataAPIController {
    Logger logger = LoggerFactory.getLogger(WikiDataAPIController.class);
    private final WikidataService wikidataService;

    public WikiDataAPIController(WikidataService wikidataService) {
        this.wikidataService = wikidataService;
    }

    /**
     *
     * @param search plant name to be searched
     * @return parsed json data from response
     */
    @GetMapping("/searchPlant")
    public String searchPlant(@RequestParam String search) {
        logger.info("Searching {}", search);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return wikidataService.getPlantInfo(search);
    }
}
