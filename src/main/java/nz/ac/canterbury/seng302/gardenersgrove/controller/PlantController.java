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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        model.addAttribute("plant", plantService.addPlant(new Plant("",0,"",""), id));
        return "plants/addPlant";
    }

    @PostMapping("/gardens/{id}/addplant")
    public String submitForm(@PathVariable("id") Long id,
                             @Validated(ValidationSequence.class) @ModelAttribute("plant") Plant plant,
                             BindingResult bindingResult, Model model) {
        logger.info(plant.getPlantedDate());

        if(!plant.getPlantedDate().isEmpty()) {
            plant.setPlantedDate(refactorPlantedDate(plant.getPlantedDate()));
        }

        logger.info(plant.getPlantedDate());
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

    public static String refactorPlantedDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String convertDateToISOFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // Formats as YYYY-MM-DD
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

        if (plant.isPresent()) {
            Plant plantOpt = plant.get();
            if (plantOpt.getPlantedDate() != null && !plantOpt.getPlantedDate().isEmpty()) {
                String convertedDate = convertDateToISOFormat(plantOpt.getPlantedDate());
                plantOpt.setPlantedDate(convertedDate);
            }
        }
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

        if(!plant.getPlantedDate().isEmpty()) {
            plant.setPlantedDate(refactorPlantedDate(plant.getPlantedDate()));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("garden_id", garden_id);
            model.addAttribute("plant_id", plant_id);
            return "plants/editPlant";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plant_id);
        if (existingPlant.isPresent()){
            existingPlant.get().setName(plant.getName());
            if(plant.getCount() != null && plant.getCount() > 0) {
                existingPlant.get().setCount(plant.getCount());
            } else {
                existingPlant.get().setCount(1);
            }
            existingPlant.get().setDescription(plant.getDescription());
            existingPlant.get().setPlantedDate(plant.getPlantedDate());
            plantService.addPlant(existingPlant.get(), garden_id);
        }

        //plantService.addPlant(updatedPlant);
        return "redirect:../../../" + garden_id;
    }


}
