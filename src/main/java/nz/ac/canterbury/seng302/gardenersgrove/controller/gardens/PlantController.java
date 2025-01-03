package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;

import org.apache.tomcat.util.codec.binary.Base64;
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
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant.PlantStatus.HARVESTED;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.HISTORY_FORMAT_DATE;

/**
 * Controller for Plant related activities
 */
@Controller
public class PlantController {
    Logger logger = LoggerFactory.getLogger(PlantController.class);
    private final PlantService plantService;
    private final GardenUserService gardenUserService;
    private final GardenService gardenService;
    private final PlantHistoryService plantHistoryService;

    private final WikidataService wikidataService;
    private static final Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg", "image/svg+xml");
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String PLANT_SUCCESSFULLY_SAVED_LOG = "Saved new plant to Garden ID: {}";
    private static final String PLANT_UNSUCCESSFULLY_SAVED_LOG = "Failed to save new plant to garden ID: {}";
    private static final String GARDEN_ID = "gardenId";
    private static final String PLANT_ID = "plantId";
    private static final String PLANT = "plant";
    private static final String ACCESS_DENIED = "error/accessDenied";
    private static final String GARDENS_REDIRECT = "redirect:/gardens/";
    private static final String ERROR_404 = "error/404";
    private static final String REFERER = "referer";







    @Autowired
    public PlantController(PlantService plantService, GardenService gardenService, GardenUserService gardenUserService, PlantHistoryService plantHistoryService, WikidataService wikidataService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
        this.plantHistoryService = plantHistoryService;
        this.wikidataService = wikidataService;
    }

    /**
     * send user to add plant form - add plant and garden id to model for
     * use in html file through thymeleaf
     * @param model representation of results
     * @param gardenId the id of the garden the plant is being added to
     * @return redirect to add plant form
     */
    @GetMapping("/gardens/{id}/add-plant")
    public String addPlantForm(@PathVariable("id") Long gardenId, @RequestParam(required = false) boolean importPlant,
                               Model model, HttpSession session, HttpServletRequest request){

        logger.info("GET /gardens/${id}/add-plant - display the new plant form");

        String refererURI = request.getHeader("Referer");
        session.setAttribute(REFERER, refererURI);
        model.addAttribute(GARDEN_ID, gardenId);

        Plant plant = new Plant("", "", "", null);
        if (importPlant) {
            plant.setName((String) session.getAttribute("plantLabel"));
            plant.setDescription((String) session.getAttribute("plantDescription"));

            String image = (String) session.getAttribute("plantImage");
            if (image != null && image.startsWith(WikidataService.IMAGE_URL_PREFIX)) {
                try {
                    var conn = new URL(image).openConnection();
                    byte[] base64Image = Base64.encodeBase64(conn.getInputStream().readAllBytes(), false);
                    model.addAttribute("importImage", new String(base64Image));
                    model.addAttribute("importImageType", conn.getContentType());
                } catch (IOException e) {
                    logger.error("Unable to fetch plant image for import");
                }
            }
        }
        model.addAttribute(PLANT, plant);

        GardenUser owner = gardenUserService.getCurrentUser();
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return ACCESS_DENIED;
        }

        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        model.addAttribute(REFERER, refererURI);
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
    public String submitAddPlantForm(@PathVariable(GARDEN_ID) Long gardenId,
                                     @Valid @ModelAttribute(PLANT) PlantDTO plantDTO,
                                     BindingResult bindingResult,
                                     @RequestParam("image") MultipartFile file,
                                     @RequestParam("dateError") String dateValidity,
                                     Model model, HttpSession session) {
        logger.info("POST /gardens/${gardenId}/add-plant - submit the new plant form");

        String referer = (String) session.getAttribute(REFERER);

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "plantedDate",
                    "plantedDate.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(PLANT, plantDTO);
            model.addAttribute(GARDEN_ID, gardenId);
            model.addAttribute(REFERER, referer);
            logger.error("Validation error in Plant Form.");
            return "plants/addPlant";
        }

        // Save the new plant and image
        Plant savedPlant = plantService.createPlant(plantDTO, gardenId);
        session.removeAttribute(REFERER);

        if (savedPlant != null) {
            try {
                plantService.setPlantImage(savedPlant.getId(), file);
                logger.info(PLANT_SUCCESSFULLY_SAVED_LOG, gardenId);
            } catch (Exception e) {
                logger.error(PLANT_UNSUCCESSFULLY_SAVED_LOG, gardenId);
                model.addAttribute(REFERER, referer);
            }
        } else {
            logger.error(PLANT_UNSUCCESSFULLY_SAVED_LOG, gardenId);
            return GARDENS_REDIRECT + gardenId;
        }
        return GARDENS_REDIRECT + gardenId;

    }

    /**
     * take user to edit plant form
     * @param model representation of results
     * @return redirect to gardens page
     */
    @GetMapping("/gardens/{gardenId}/plants/{plantId}/edit")
    public String editPlantForm(@PathVariable(GARDEN_ID) long gardenId,
                            @PathVariable(PLANT_ID) long plantId,
                            Model model, HttpSession session, HttpServletRequest request) {
        logger.info("/garden/{}/plant/{}/edit", gardenId, plantId);

        String refererURI = request.getHeader("Referer");
        session.setAttribute(REFERER, refererURI);

        Optional<Plant> plant = plantService.getPlantById(plantId);
        GardenUser owner = gardenUserService.getCurrentUser();
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return ACCESS_DENIED;
        }
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        model.addAttribute(GARDEN_ID, gardenId);
        model.addAttribute(PLANT_ID, plantId);
        model.addAttribute(PLANT, plant.orElse(null));
        model.addAttribute(REFERER, refererURI);

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
    public String submitEditPlantForm(@PathVariable(GARDEN_ID) long gardenId,
                                      @PathVariable(PLANT_ID) long plantId,
                                      @RequestParam("image") MultipartFile file,
                                      @RequestParam("dateError") String dateValidity,
                                      @Valid @ModelAttribute(PLANT) PlantDTO plant,
                                      BindingResult bindingResult,
                                      Model model, HttpSession session) {

        String referer = (String) session.getAttribute(REFERER);

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "plantedDate",
                    "plantedDate.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(PLANT, plant);
            model.addAttribute(GARDEN_ID, gardenId);
            model.addAttribute(PLANT_ID, plantId);
            model.addAttribute(REFERER, referer);
            return "plants/editPlant";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plantId);
        session.removeAttribute(REFERER);

        if (existingPlant.isPresent()){
            plantService.updatePlant(existingPlant.get(), plant);

            if (file != null) {
                try {
                    plantService.setPlantImage(plantId, file);
                    logger.info(PLANT_SUCCESSFULLY_SAVED_LOG, gardenId);
                } catch (Exception e) {
                    logger.error(PLANT_UNSUCCESSFULLY_SAVED_LOG, gardenId);
                    model.addAttribute(REFERER, referer);

                }
            }
        }

        return GARDENS_REDIRECT+gardenId;
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

        // Return the saved image from DB
        if(existingPlant.getPlantImage() != null) {
            logger.info("Returning the plants saved image from DB");
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(existingPlant.getPlantImageContentType()))
                    .body(existingPlant.getPlantImage());
        }
        return null;


    }

    /**
     * Saves image to the plant with given id
     * @param file Image file
     * @param id Plant id the image is saving to
     * @param referer The page where the request sent from
     * @return Redirects to the current page
     */
    @PostMapping("plants/{id}/plant-image")
    public String uploadPlantImage(
            @RequestParam("image") MultipartFile file,
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.REFERER) String referer) {
        logger.info("POST /plants " + id + "/plant-image");
        try {
            plantService.setPlantImage(id, file);
            logger.info(PLANT_SUCCESSFULLY_SAVED_LOG, id);
        } catch (Exception e) {
            logger.error(PLANT_UNSUCCESSFULLY_SAVED_LOG, id);
        }
        return "redirect:" + referer;
    }


    /**
     * send user to add plant history form - add plant and garden id to model for
     * use in html file through thymeleaf
     * @param model representation of results
     * @param gardenId the id of the garden the plant is being added to
     * @return redirect to add plant form
     */
    @GetMapping("/gardens/{gardenId}/plants/{plantId}/history")
    public String addPlantHistoryForm(@PathVariable(GARDEN_ID) Long gardenId, @PathVariable(PLANT_ID) Long plantId, Model model) {

        logger.info("GET /gardens/{}/plants/{}/history - display the plant history form", gardenId, plantId);


        GardenUser owner = gardenUserService.getCurrentUser();
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return ACCESS_DENIED;
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plantId);

        if (existingPlant.isPresent()){
            Plant plant = existingPlant.get();
            LocalDate timestamp = LocalDate.now();

            if (plantHistoryService.historyExists(plant, timestamp)) {
                logger.warn("Update already exists today");
                return ERROR_404;
            }
        }

        model.addAttribute(GARDEN_ID, gardenId);
        model.addAttribute(PLANT_ID, plantId);
        model.addAttribute(PLANT, new Plant("", "", "", null));
        return "plants/plantHistory";
    }


    /**
     *
     * @param gardenId the id of the garden that the plant belongs to
     * @param plantId the id of the plant being edited
     * @param file the image file
     * @param plantHistoryDTO the plant
     * @param bindingResult binding result which helps display errors
     * @param model representation of results
     * @return redirect to gardens page if data is valid
     */
    @PostMapping("/gardens/{gardenId}/plants/{plantId}/history")
    public String submitPlantHistoryForm(@PathVariable(GARDEN_ID) long gardenId,
                                      @PathVariable(PLANT_ID) long plantId,
                                      @RequestParam("image") MultipartFile file,
                                      @RequestParam("description") String description,
                                      @Valid @ModelAttribute(PLANT) PlantHistoryItemDTO plantHistoryDTO,
                                      BindingResult bindingResult,
                                      Model model) throws IOException {

        logger.info("GET /gardens/{}/plants/{}/history - display the plant history form", gardenId, plantId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("description", plantHistoryDTO);
            return "plants/plantHistory";
        }

        Optional<Plant> existingPlant = plantService.getPlantById(plantId);

        if (existingPlant.isPresent()){
            try {
                boolean validImage = ACCEPTED_FILE_TYPES.contains(file.getContentType()) && (file.getSize() <= MAX_FILE_SIZE);

                if (!file.isEmpty() && validImage) {
                    plantHistoryService.addHistoryItem(existingPlant.get(), file.getContentType(), file.getBytes(), description);
                }
            } catch (IOException e) {
                logger.info("Exception {}",e.toString());
            }
        }

        return GARDENS_REDIRECT + gardenId + "/plants/" + plantId;
    }

    /**
     * Controller for the plant history page
     *
     * @param plantId plant id
     * @param model representation of results
     * @return redirect to plant detail page
     */
    @GetMapping("/gardens/{gardenId}/plants/{plantId}")
    public String getPlantTimeline(@PathVariable(GARDEN_ID) long gardenId,
                                   @PathVariable(PLANT_ID) long plantId,
                                   Model model) {
        logger.info("Serving up plant detail page.");
        logger.info("/garden/{}/plant/{}", gardenId, plantId);

        Optional<Plant> plant = plantService.getPlantById(plantId);
        Optional<Garden> garden = gardenService.getGardenById(gardenId);


        if (garden.isPresent()) {
            GardenUser owner = garden.get().getOwner();
            GardenUser currentUser = gardenUserService.getCurrentUser();
            boolean isNotOwner = !owner.getId().equals(currentUser.getId());
            boolean isNotPublic = !garden.get().getIsPublic();

            if (isNotOwner && isNotPublic){
                logger.warn("User tried to access a non-public garden that is not theirs, denying access.");
                return ACCESS_DENIED;
            }

        if (plant.isPresent()) {
            Plant plantItem =  plant.get();
            LocalDate timestamp = LocalDate.now();

            if (plantHistoryService.historyExists(plantItem, timestamp) || plantItem.getStatus().equals(HARVESTED)) {
                logger.warn("Update already exists today");
                model.addAttribute("disabledRecordButton", true);
            } else {
                model.addAttribute("disabledRecordButton", false);
            }

            if (plantItem.getStatus().equals(HARVESTED)) {
                logger.warn("Plant harvested");
                model.addAttribute("showHarvestCard", true);
            } else {
                model.addAttribute("showHarvestCard", false);
            }



            List <PlantHistoryItem> plantHistory = plantHistoryService.getPlantHistory(plantItem);
            model.addAttribute("plantHistory", plantHistory);
            model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
            model.addAttribute("NZ_FORMAT_DATE", HISTORY_FORMAT_DATE);

            model.addAttribute("garden", garden.get());
            model.addAttribute("owner", owner);
            model.addAttribute("currentUser", currentUser);
        } else {
            logger.warn("User tried to access a non-existant garden, returning 404.");
            return ERROR_404;
        }

        model.addAttribute(GARDEN_ID, gardenId);
        model.addAttribute(PLANT_ID, plantId);
        model.addAttribute(PLANT, plant.orElse(null));

        return "plants/plantDetails";

        }

        return ERROR_404;
    }

    /**
     * Gets plant history image from the database
     *
     * @param plantId plant ID
     * @param recordId plant record ID
     * @return Response entity
     */
    @GetMapping("plants/{plantId}/history/{recordId}/image")
    public ResponseEntity<byte[]> historyImage(@PathVariable("plantId") Long plantId, @PathVariable("recordId") Long recordId, HttpServletRequest request) {
        logger.info("GET /plants/{}/history/{}/image", plantId, recordId);

        Optional<Plant> plant = plantService.getPlantById(plantId);
        Optional<PlantHistoryItem> historyItem = plantHistoryService.getPlantHistoryById(recordId);

        if (plant.isEmpty() || historyItem.isEmpty()) {
            logger.info("Returning default plant image");
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, request.getContextPath() + "/img/default-plant.svg").build();
        }

        // Return the saved image from DB
        logger.info("Returning the plants saved image from DB");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(historyItem.get().getImageContentType()))
                .body(historyItem.get().getImage());
    }

    /**
     * take user to search plant information form
     * @return redirect to a more detailed page about a specific plant
     */
    @GetMapping("/plant-information")
    public String plantInformationForm(
            @RequestParam(required = false) String q,
            Model model) throws ExternalServiceException {
        if (q != null) {
            List<PlantInfoDTO> plants = wikidataService.getPlantInfo(q);
            model.addAttribute("plants", plants);
        }

        return "plants/plantInformation";
    }

    /**
     * Asks the user to select a garden to import the plant to
     * @return a page to select a garden
     */
    @PostMapping("/plant-information/add-to-garden")
    public String plantInformationAddToGarden(
            @RequestParam String label,
            @RequestParam String description,
            @RequestParam String image,
            HttpSession session,
            Model model) {
        // Save info to add to the plant
        session.setAttribute("plantLabel", label);
        session.setAttribute("plantDescription", description);
        session.setAttribute("plantImage", image);

        List<Garden> gardens = gardenService.getGardensByOwnerId(gardenUserService.getCurrentUser().getId());
        model.addAttribute("gardens", gardens);

        return "plants/plantInformationAddToGarden";
    }
}
