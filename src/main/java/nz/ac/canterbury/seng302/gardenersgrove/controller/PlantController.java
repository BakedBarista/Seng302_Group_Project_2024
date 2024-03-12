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
import java.time.format.DateTimeParseException;
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

    /**
     * send user to add plant form - add plant and garden id to model for
     * use in html file through thymeleaf
     * @param model
     * @param gardenId
     * @return
     */
    @GetMapping("/gardens/{id}/add-plant")
    public String addPlantForm(@PathVariable("id") Long gardenId, Model model){

        logger.info("GET /gardens/${id}/add-plant - display the new plant form");
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plant", plantService.addPlant(new Plant("","0","",""), gardenId));
        return "plants/addPlant";
    }

    /**
     * check for validity of data and return user to
     * garden details page if data is valid with newly submitted plant entity
     * - else keep them on the add plant form with data persistent and error messages displayed
     * @param gardenId
     * @param plant
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/gardens/{gardenId}/add-plant")
    public String submitAddPlantForm(@PathVariable("gardenId") Long gardenId,
                             @Valid @ModelAttribute("plant") Plant plant,
                             BindingResult bindingResult, Model model) {
        logger.info(plant.getPlantedDate());

        if(!plant.getPlantedDate().isEmpty()) {
            plant.setPlantedDate(refactorPlantedDate(plant.getPlantedDate()));
        }


        logger.info(plant.getPlantedDate());
        logger.info("POST /gardens/${gardenId}/add-plant - submit the new plant form");
        if(bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("gardenId", gardenId);
            logger.info("Error In Form");
            return "plants/addPlant";
        }
        plantService.addPlant(plant, gardenId);

        return "redirect:/gardens/" + gardenId;
    }

    /**
     * take user to edit plant form
     * @param model representation of results
     * @return redirect to gardens page
     */
    @GetMapping("/gardens/{gardenId}/plants/{plantId}/edit")
    public String editPlantForm(@PathVariable("gardenId") long gardenId,
                            @PathVariable("plantId") long plantId,
                            Model model) {
        logger.info("/garden/{}/plant/{}/edit", gardenId, plantId);

        Optional<Plant> plant = plantService.getPlantById(plantId);
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plantId", plantId);
        model.addAttribute("plant", plant.orElse(null));
        model.addAttribute("imagePath",plantService.getPlantById(plantId).get().getPlantImagePath());
        return "plants/editPlant";
    }

    /**
     * Put a single plant
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{gardenId}/plants/{plantId}/edit")
    public String submitEditPlantForm(@PathVariable("gardenId") long gardenId,
                               @PathVariable("plantId") long plantId,
                               @Validated(ValidationSequence.class) @ModelAttribute("plant") Plant plant,
                               BindingResult bindingResult, Model model) {
        logger.info("/garden/{}/plant/{}", gardenId, plant);

        if(!plant.getPlantedDate().isEmpty()) {
            plant.setPlantedDate(refactorPlantedDate(plant.getPlantedDate()));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("gardenId", gardenId);
            model.addAttribute("plantId", plantId);
            return "plants/editPlant";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plantId);
        if (existingPlant.isPresent()){
            existingPlant.get().setName(plant.getName());
            existingPlant.get().setCount(plant.getCount());
            existingPlant.get().setDescription(plant.getDescription());
            existingPlant.get().setPlantedDate(plant.getPlantedDate());
            plantService.addPlant(existingPlant.get(), gardenId);
        }

        //plantService.addPlant(updatedPlant);
        return "redirect:/gardens/"+gardenId;
    }

    /**
     * take in date given via form and convert to dd/mm/yyyy (fix for thymeleaf form issue)
     *
     * note : catches DateTimeParseException when date is already in dd/mm/yyyy for test purposes
     *
     * @param date
     * @return parsed date in correct format
     */
    public static String refactorPlantedDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException alreadyCorrectFormatForTest) {
            return date;
        }
    }
}
