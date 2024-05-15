package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Plant related activities
 */
@Controller
public class PlantController {
    Logger logger = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;
    private final GardenUserService gardenUserService;
    private final GardenService gardenService;

    @Autowired
    public PlantController(PlantService plantService, GardenService gardenService, GardenUserService gardenUserService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    /**
     * send user to add plant form - add plant and garden id to model for
     * use in html file through thymeleaf
     * @param model representation of results
     * @param gardenId the id of the garden the plant is being added to
     * @return redirect to add plant form
     */
    @GetMapping("/gardens/{id}/add-plant")
    public String addPlantForm(@PathVariable("id") Long gardenId, Model model){

        logger.info("GET /gardens/${id}/add-plant - display the new plant form");
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plant", new Plant("","","",""));

        GardenUser owner = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "plants/addPlant";
    }

                             
    /**
     * check for validity of data and return user to
     * garden details page if data is valid with newly submitted plant entity
     * - else keep them on the add plant form with data persistent and error messages displayed
     * @param gardenId the id of the garden the plant is being added to
     * @param plant the plant entity being added
     * @param bindingResult binding result
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{gardenId}/add-plant")
    public String submitAddPlantForm(@PathVariable("gardenId") Long gardenId,
                                      @Valid @ModelAttribute("plant") Plant plant,
                                     BindingResult bindingResult,
                                     @RequestParam("image") MultipartFile file,
                                      Model model) throws Exception {


        if (!plant.getPlantedDate().isEmpty() && !plant.getPlantedDate().matches("\\d{4}-\\d{2}-\\d{2}")){
            bindingResult.rejectValue("plantedDate", "plantedDate.formatError", "Date must be in the format DD-MM-YYYY");

        }
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
        Plant savedPlant = plantService.addPlant(plant, gardenId);
        //Save plant image
        if(file != null && !file.isEmpty() && savedPlant != null) {
            try{
            plantService.setPlantImage(savedPlant.getId(), file.getContentType(), file.getBytes());
        } catch (Exception e){
                logger.info("Exception {}",e.toString());
            }
        }
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

        if (plant.isPresent()) {
            Plant plantOpt = plant.get();
            if (plantOpt.getPlantedDate() != null && !plantOpt.getPlantedDate().isEmpty()) {
                DateTimeFormatter htmlDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate databaseDate = LocalDate.parse(plantOpt.getPlantedDate(), htmlDateFormat);
                plantOpt.setPlantedDate(databaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        GardenUser owner = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plantId", plantId);
        model.addAttribute("plant", plant.orElse(null));
        return "plants/editPlant";
    }

    /**
     * Put a single plant
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{gardenId}/plants/{plantId}/edit")
    public String submitEditPlantForm(@PathVariable("gardenId") long gardenId,
                               @PathVariable("plantId") long plantId, @RequestParam("image") MultipartFile file,
                               @Valid @ModelAttribute("plant") Plant plant,
                               BindingResult bindingResult, Model model) throws Exception{

        logger.info(plant.getPlantedDate());
        if (!plant.getPlantedDate().isEmpty() && !plant.getPlantedDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            bindingResult.rejectValue("plantedDate", "plantedDate.formatError", "Date must be in the format YYYY-MM-DD");
        }

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
            Plant savedPlant = plantService.addPlant(existingPlant.get(), gardenId);
            if(file != null && !file.isEmpty() && savedPlant != null) {
                try {
                    plantService.setPlantImage(savedPlant.getId(), file.getContentType(), file.getBytes());
            } catch (Exception e) {
                    logger.info("Exception {}",e.toString());
                }
            }
        }

        return "redirect:/gardens/"+gardenId;
    }

    /**
     * take in date given via form and convert to dd/mm/yyyy (fix for thymeleaf form issue)*
     * note : catches DateTimeParseException when date is already in dd/mm/yyyy for test purposes
     *
     * @param date the date string that needs to be formatted
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


    /**
     * Gets a plant image from database
     * @param id plant id
     * @return ResponseEntity as bytes and content type
     */
    @GetMapping("plants/{id}/plant-image")
    public ResponseEntity<byte[]> plantImage(@PathVariable("id") Long id) {
        logger.info("GET /plants/" + id + "/plant-image");

        Optional<Plant> plant = plantService.getPlantById(id);
        Plant existingPlant = new Plant();
        if (plant.isPresent()) {
            existingPlant = plant.get();
        }
        if (existingPlant.getPlantImage() == null) {
                return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/img/plant.png").build();
            }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(existingPlant.getPlantImageContentType()))
                .body(existingPlant.getPlantImage());

    }

    /**
     * Saves image to the plant with given id
     * @param file Image file
     * @param id Plant id the image is saving to
     * @param referer The page where the request sent from
     * @return Redirects to the current page
     * @throws Exception File exception
     */
    @PostMapping("plants/{id}/plant-image")
    public String uploadPlantImage(
            @RequestParam("image") MultipartFile file,
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.REFERER) String referer) throws Exception {
        logger.info("POST /plants " + id + "/plant-image");
        try {
            plantService.setPlantImage(id, file.getContentType(), file.getBytes());
        } catch (Exception e) {
            logger.info("Exception {}",e.toString());
        }
        return "redirect:" + referer;
    }
}
