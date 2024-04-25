package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UploadController uploadController;
    private static final List<String> allowedExtension = Arrays.asList("jpg", "png","jpeg", "svg");
    private static final Integer MAX_FILE_SIZE = 10*1024*1024;

    private final GardenService gardenService;

    @Autowired
    public PlantController(PlantService plantService, GardenService gardenService, UploadController uploadController) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.uploadController = uploadController;
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
        model.addAttribute("plant", new Plant("","","",""));

        List<Garden> gardens = gardenService.getAllGardens();
        model.addAttribute("gardens", gardens);
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
                                     BindingResult bindingResult,
                                     @RequestParam("image") MultipartFile file,
                                      Model model) {


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
        try {
            if (file != null && !file.isEmpty()) {
                // Check file size
                if (file.getSize() > MAX_FILE_SIZE) {
                    model.addAttribute("plant", plant);
                    model.addAttribute("gardenId", gardenId);
                    model.addAttribute("fileSizeError", "Image must be less than 10MB");
                    logger.error("File size exceeds the limit");
                    return "plants/addPlant";
                }

                // Check file type
                String fileName = file.getOriginalFilename();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (!allowedExtension.contains(extension.toLowerCase())) {
                    model.addAttribute("plant", plant);
                    model.addAttribute("gardenId", gardenId);
                    model.addAttribute("fileTypeError", "Image must be of type png, jpg or svg");
                    logger.error("Invalid file format");
                    return "plants/addPlant";
                }

                // Proceed with uploading the file
                Plant plantToAdd = plantService.addPlant(plant, gardenId);
                model.addAttribute("image", uploadController.upload(file, plantToAdd.getId()));
            } else {
                // No file uploaded
                logger.error("No file uploaded");
                plantService.addPlant(plant, gardenId);
            }
        } catch (Exception error) {
            //TODO: something with this error
            logger.error(String.valueOf(error));
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
        List<Garden> gardens = gardenService.getAllGardens();

        if (plant.isPresent()) {
            Plant plantOpt = plant.get();
            if (plantOpt.getPlantedDate() != null && !plantOpt.getPlantedDate().isEmpty()) {
                DateTimeFormatter htmlDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate databaseDate = LocalDate.parse(plantOpt.getPlantedDate(), htmlDateFormat);
                plantOpt.setPlantedDate(databaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        model.addAttribute("gardens", gardens);
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
                               @PathVariable("plantId") long plantId, @RequestParam("image") MultipartFile file,
                               @Valid @ModelAttribute("plant") Plant plant,
                               BindingResult bindingResult, Model model) {

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
}
