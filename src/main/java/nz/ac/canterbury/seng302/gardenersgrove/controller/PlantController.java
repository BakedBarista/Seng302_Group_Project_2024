package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// TODO: THIS WHOLE FILE NEEDS TO BE UPDATED
/**
 * Controller for Plant related activities
 */
@Controller
public class PlantController {
    Logger logger = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;
    private PlantRepository plantRepository;

    private final GardenService gardenService;

    @Autowired
    public PlantController(PlantService plantService, GardenService gardenService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
    }

    @GetMapping("/gardens/{id}/addplant")
    public String form(@RequestParam(name="name", required = false, defaultValue = "") String name,
                       @RequestParam(name="count", required = false, defaultValue = "") String count,
                       @RequestParam(name="description", required = false, defaultValue = "") String description,
                       @RequestParam(name="plantedDate", required = false, defaultValue = "") String plantedDate,
                       Model model,
                       @PathVariable("id") Long id){

        logger.info("GET /gardens/${id}/addplant - display the new plant form");
        logger.info("Garden ID: {}", id);
        model.addAttribute("gardenId", id);
        return "plants/addPlant"; // TODO: Add the plant form
    }

    @PostMapping("/gardens/{id}/addplant")
    public String submitForm(@PathVariable("id") Long id,
                             @ModelAttribute("plant") Plant plant,
                             BindingResult bindingResult, Model model) {


        logger.info("POST /gardens/${id}/addplant - submit the new plant form");
        if(bindingResult.hasErrors()) {
            return "plants/addPlant";
        }

        Plant savedPlant = plantService.addPlant(plant, id);
        return "redirect:/gardens/" + id;
    }

    /**
     * Get single plant details
     * @param model representation of results
     * @return redirect to gardens page
     */
    @GetMapping("/gardens/{garden_id}/plants/{plant_id}/edit")
    public String editPlant(@PathVariable("garden_id") long garden_id,
                            @PathVariable("plant_id") long plant_id,
                            Model model) {
        logger.info("/garden/{}/plant/{}/edit", garden_id, plant_id);
        Optional<Plant> plant = plantService.getPlantById(plant_id);
        model.addAttribute("plant", plant.orElse(null));
        return "plants/editPlant";
    }

    /**
     * Put a single plant
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{garden_id}/plants/{plant_id}/edit")
    public String updatePlant(@PathVariable("garden_id") long garden_id,
                               @PathVariable("plant_id") long plant_id,
                               @RequestParam(name="name", required = false, defaultValue = "") String newName,
                               @RequestParam(name="count", required = false, defaultValue = "") int newCount,
                               @RequestParam(name="description", required = false, defaultValue = "") String newDescription,
                               @RequestParam(name="plantedDate", required = false, defaultValue = "") String newDate,
                               Model model) {
        logger.info("/garden/{}/plant/{}", garden_id, plant_id);

        newName = "";
        newCount = 0;
        newDescription = "";
        newDate = "";

        Optional<Plant> garden = plantService.getPlantById(plant_id);
        Plant updatedPlant = garden.orElse(null);
        updatedPlant.setName(newName);
        updatedPlant.setCount(newCount);
        updatedPlant.setDescription(newDescription);
        updatedPlant.setPlantedDate(newDate);

        //plantService.addPlant(updatedPlant);
        return "redirect:../../../" + garden_id;
    }


}
