package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
        model.addAttribute("plant", new Plant("","","",null));

        GardenUser owner = gardenUserService.getCurrentUser();
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return "/error/accessDenied";
        }

        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "plants/addPlant";
    }



    /**
     * check for validity of data and return user to
     * garden details page if data is valid with newly submitted plant entity
     * - else keep them on the add plant form with data persistent and error messages displayed
     * @param gardenId the id of the garden the plant is being added to
     * @param plantDTO the plant dto being checaked
     * @param bindingResult binding result
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{gardenId}/add-plant")
    public String submitAddPlantForm(@PathVariable("gardenId") Long gardenId,
                                     @Valid @ModelAttribute("plant") PlantDTO plantDTO,
                                     BindingResult bindingResult,
                                     @RequestParam("image") MultipartFile file,
                                     @RequestParam("dateError") String dateValidity,
                                     Model model) {
        logger.info("POST /gardens/${gardenId}/add-plant - submit the new plant form");

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "plantedDate",
                    "plantedDate.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("plant", plantDTO);
            model.addAttribute("gardenId", gardenId);
            logger.error("Validation error in Plant Form.");
            return "plants/addPlant";
        }

        // Save the new plant and image
        Plant savedPlant = plantService.createPlant(plantDTO, gardenId);
        if (savedPlant != null) {
            try {
                plantService.setPlantImage(savedPlant.getId(), file.getContentType(), file.getBytes());
                logger.info("Saved new plant to Garden ID: {}", gardenId);
            } catch (IOException e) {
                logger.error("Something went wrong saving the user's plant image: ", e);
            }
        } else {
            logger.error("Failed to save new plant to garden ID: {}", gardenId);
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
        GardenUser owner = gardenUserService.getCurrentUser();
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return "/error/accessDenied";
        }
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plantId", plantId);
        model.addAttribute("plant", plant.orElse(null));
        return "plants/editPlant";
    }

    /**
     *
     * @param gardenId the id of the garden that the plant belongs to
     * @param plantId the id of the plant being edited
     * @param file the image file
     * @param plant the plant entity being edited
     * @param bindingResult binding result which helps display errors
     * @param model representation of results
     * @return redirect to gardens page if data is valid
     */
    @PostMapping("/gardens/{gardenId}/plants/{plantId}/edit")
    public String submitEditPlantForm(@PathVariable("gardenId") long gardenId,
                                      @PathVariable("plantId") long plantId,
                                      @RequestParam("image") MultipartFile file,
                                      @RequestParam("dateError") String dateValidity,
                                      @Valid @ModelAttribute("plant") PlantDTO plant,
                                      BindingResult bindingResult,
                                      Model model) {

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "plantedDate",
                    "plantedDate.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("plant", plant);
            model.addAttribute("gardenId", gardenId);
            model.addAttribute("plantId", plantId);
            return "plants/editPlant";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plantId);
        if (existingPlant.isPresent()){
            plantService.updatePlant(existingPlant.get(), plant);

            if (file != null) {
                try {
                    plantService.setPlantImage(plantId, file.getContentType(), file.getBytes());
                } catch (Exception e) {
                    logger.info("Exception {}",e.toString());
                }
            }
        }

        return "redirect:/gardens/"+gardenId;
    }

    /**
     * Gets a plant image from database
     * @param id plant id
     * @return ResponseEntity as bytes and content type
     */
    @GetMapping("plants/{id}/plant-image")
    public ResponseEntity<byte[]> plantImage(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.info("GET /plants/" + id + "/plant-image");

        Optional<Plant> plant = plantService.getPlantById(id);
        Plant existingPlant = new Plant();

        if (plant.isPresent()) {
            existingPlant = plant.get();
        }
        // Return the default image if nothing specified
        if (existingPlant.getPlantImage() == null || existingPlant.getPlantImage().length == 0) {
            logger.info("Returning default plant image");
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, request.getContextPath() + "/img/default-plant.svg").build();
        }

        // Return the saved image from DB
        logger.info("Returning the plants saved image from DB");
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
