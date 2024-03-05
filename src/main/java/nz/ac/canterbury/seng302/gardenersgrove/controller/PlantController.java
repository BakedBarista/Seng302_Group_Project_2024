package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// TODO: THIS WHOLE FILE NEEDS TO BE UPDATED
/**
 * Controller for Plant related activities
 */
@Controller
public class PlantController {
    Logger logger = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;

    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping("/gardens/{id}/add_plant")
    public String form(@RequestParam(name="name", required = false, defaultValue = "") String name,
                       @RequestParam(name="count", required = false, defaultValue = "") String count,
                       @RequestParam(name="description", required = false, defaultValue = "") String description,
                       @RequestParam(name="plantedDate", required = false, defaultValue = "") String plantedDate,
                       @PathVariable(name="id") Long id,
                       Model model) {
        logger.info("GET /gardens/${id}/add-plant - display the new plant form");
        return "gardens/createGarden"; // TODO: Add the plant form
    }
}
