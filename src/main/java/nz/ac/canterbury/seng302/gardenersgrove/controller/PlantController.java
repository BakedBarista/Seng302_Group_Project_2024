package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationSequence;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    public String form(Model model, @PathVariable("id") Long id){

        logger.info("GET /gardens/${id}/addplant - display the new plant form");
        model.addAttribute("gardenId", id);
        model.addAttribute("plant", new Plant());
        return "plants/addPlant";
    }

    @PostMapping("/gardens/{id}/addplant")
    public String submitForm(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("plant") Plant plant,
                             BindingResult bindingResult, Model model) {


        logger.info("POST /gardens/${id}/addplant - submit the new plant form");
        if(bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("gardenId", id);
            logger.info("Error In Form");
            return "plants/addPlant";
        }
        plantService.addPlant(plant, id);


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
        //plantService.getPlantById(plant_id).get().setPlantImagePath("/images/default.png");
        model.addAttribute("garden_id", garden_id);
        model.addAttribute("plant", plant.orElse(null));
        model.addAttribute("imagePath",plantService.getPlantById(plant_id).get().getPlantImagePath());
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
                              @Validated(ValidationSequence.class) @ModelAttribute("plant") Plant plant,
                               BindingResult bindingResult, Model model) {
        logger.info("/garden/{}/plant/{}", garden_id, plant_id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("garden_id", garden_id);
            model.addAttribute("plant_id", plant_id);
            return "plants/editPlant";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plant_id);
        if (existingPlant.isPresent()){
            existingPlant.get().setName(plant.getName());
            existingPlant.get().setCount(plant.getCount());
            existingPlant.get().setDescription(plant.getDescription());
            existingPlant.get().setPlantedDate(plant.getPlantedDate());
            plantService.addPlant(existingPlant.get(), garden_id);
        }

        //plantService.addPlant(updatedPlant);
        return "redirect:../../../" + garden_id;
    }


}
