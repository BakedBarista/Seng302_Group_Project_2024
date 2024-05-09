package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;
    private final WeatherAPIService weatherAPIService;

    private final ModerationService moderationService;

    private final GardenUserService gardenUserService;
    private final FriendService friendService;

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService, GardenUserService gardenUserService, WeatherAPIService weatherAPIService, FriendService friendService, ModerationService moderationService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.gardenUserService = gardenUserService;
        this.weatherAPIService = weatherAPIService;
        this.friendService = friendService;
        this.moderationService = moderationService;
    }

    /**
     * Gets form to be displayed
     * @param model representation of name, location and size
     * @return gardenFormTemplate
     */
    @GetMapping("/gardens/create")
    public String form(Model model) {
        logger.info("GET /gardens/create - display the new garden form");
        model.addAttribute("garden", new Garden());
        GardenUser owner = gardenUserService.getCurrentUser();

        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "gardens/createGarden";
    }

    /**
     * Submits form to be displayed
     * @param garden   garden details
     * @param bindingResult binding result
     * @param model representation of results
     * @return gardenForm
     */
    @PostMapping("/gardens/create")
    public String submitForm(@Valid @ModelAttribute("garden") Garden garden,
                             BindingResult bindingResult, Model model) {
        logger.info("POST /gardens - submit the new garden form");

        if (bindingResult.hasErrors()) {
            model.addAttribute("garden", garden);
            return "gardens/createGarden";
        }
        GardenUser owner = gardenUserService.getCurrentUser();
        garden.setOwner(owner);

        //Checks description for inappropriate content
        if (moderationService.checkIfDescriptionIsFlagged(garden.getDescription())) {
            model.addAttribute("garden", garden);
            model.addAttribute("profanity", "The description does not match the language standards of the app.");
            return "gardens/createGarden";
        }

        Garden savedGarden = gardenService.addGarden(garden);
        return "redirect:/gardens/" + savedGarden.getId();
    }

    /**
     * Gets all garden details
     * @param model representation of results
     * @return viewGardenTemplate
     */
    @GetMapping("/gardens")
    public String responses(Model model) {
        logger.info("Get /gardens - display all gardens");
        GardenUser currentUser = gardenUserService.getCurrentUser();
        if(currentUser != null) {
            List<Garden> userGardens = gardenService.getGardensByOwnerId(currentUser.getId());
            model.addAttribute("gardens", userGardens);
        }


        return "gardens/viewGardens";
    }

    /**
     * Gets the id of garden
     * @param model representation of results
     * @return gardenDetails page
     */
    @GetMapping("/gardens/{id}")
    public String gardenDetail(@PathVariable(name = "id") Long id,
                               Model model) {

        logger.info("Get /gardens/id - display garden detail");
        Optional<Garden> gardenOpt = gardenService.getGardenById(id);
        if(gardenOpt.isPresent()) {
            Garden garden = gardenOpt.get();
            model.addAttribute("garden", garden);
            model.addAttribute("owner", garden.getOwner());
            model.addAttribute("plants", plantService.getPlantsByGardenId(id));

            List<List<Map<String, Object>>> weatherResult = weatherAPIService.getWeatherData(id, garden.getLat(), garden.getLon());
            List<Map<String, Object>> weatherPrevious = Collections.emptyList();
            List<Map<String, Object>> weatherForecast = Collections.emptyList();
            boolean displayWeatherAlert = false;

            if (!weatherResult.isEmpty()) {
                weatherPrevious = weatherResult.get(0);
                weatherForecast = weatherResult.get(1);
                displayWeatherAlert = garden.getDisplayWeatherAlert();
            }

            model.addAttribute("weatherPrevious", weatherPrevious);
            model.addAttribute("weatherForecast", weatherForecast);
            model.addAttribute("displayWeather", !weatherResult.isEmpty());
            model.addAttribute("displayRecommendation", displayWeatherAlert);
            model.addAttribute("wateringRecommendation", garden.getWateringRecommendation());
        }

        GardenUser currentUser = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(currentUser.getId());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("gardens", gardens);
        return "gardens/gardenDetails";
    }

    /**
     * Hides the weather alert for a specific garden for the remainder of the day
     * @param id the ID of the garden to hide alerts for
     * @return redirects back to the detail page
     */
    @PostMapping("/gardens/{id}/hide-weather-alert")
    public String hideWeatherAlertForGarden(@PathVariable(name = "id") Long id) {
        logger.info("POST /gardens/{}/hide-weather-alert", id);
        Optional<Garden> gardenOptional = gardenService.getGardenById(id);

        if (gardenOptional.isPresent()) {
            logger.info("Setting alert to hide for Garden {} until next day.", id);
            Garden garden = gardenOptional.get();
            garden.setDisplayWeatherAlert(false);
            gardenService.addGarden(garden);
        }
        return "redirect:/gardens/" + id;
    }

    /**
     * Updates the public status of the garden
     * @param id garden id
     * @param isPublic public status
     * @return redirect to gardens
     */
    @PostMapping("/gardens/{id}")
    public String updatePublicStatus(@PathVariable(name = "id") Long id,
                                     @RequestParam(name = "isPublic", defaultValue = "false") Boolean isPublic) {
        logger.info("POST /gardens/id - update garden public status");
        logger.info(String.valueOf(isPublic));
        Optional<Garden> garden = gardenService.getGardenById(id);
        if (garden.isPresent()) {
            garden.get().setPublic(isPublic);
            gardenService.addGarden(garden.get());
        }
        return "redirect:/gardens/" + id;
    }


    /**
     * Updates the Garden
     * @param id garden id
     * @return redirect to gardens
     */
    @GetMapping("/gardens/{id}/edit")
    public String getGarden(@PathVariable(name = "id") long id, Model model) {
        logger.info("Get /garden/{}", id);
        Optional<Garden> garden = gardenService.getGardenById(id);
        logger.info(String.valueOf(garden));
        model.addAttribute("garden", garden.orElse(null));
        GardenUser owner = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "gardens/editGarden";
    }

    /**
     * Update garden details
     * @param id garden id
     * @param garden garden details
     * @param result binding result
     * @param model representation of results
     * @return redirect to gardens
     */
    @PostMapping("/gardens/{id}/edit")
    public String updateGarden(@PathVariable(name = "id") long id,
                               @Valid @ModelAttribute("garden") Garden garden,
                               BindingResult result,
                               Model model) {
                                
            List<String> locationErrorNames = Arrays.asList("city", "country", "suburb", "streetNumber", "streetName");

            if (result.hasErrors()) {
                for (FieldError error : result.getFieldErrors()) {
                    if(locationErrorNames.contains(error.getField())){
                    if(error.getCode().equals("NotBlank")){
                        var errorMessage = "Location cannot be empty";
                        model.addAttribute("locationError", errorMessage);
                        break;
                    } else {
                        var errorMessage = "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes";
                        model.addAttribute("locationError", errorMessage);
                        break;
                    }
                }
            }

            model.addAttribute("garden", garden);
            model.addAttribute("id", id);
            return "gardens/editGarden";
        }
        //Checks description for inappropriate content
        if (moderationService.checkIfDescriptionIsFlagged(garden.getDescription())) {
            model.addAttribute("garden", garden);
            model.addAttribute("profanity", "The description does not match the language standards of the app.");
            return "gardens/editGarden";
        }

        Optional<Garden> existingGarden = gardenService.getGardenById(id);
        if (existingGarden.isPresent()) {
            existingGarden.get().setName(garden.getName());
            existingGarden.get().setStreetNumber(garden.getStreetNumber());
            existingGarden.get().setStreetName(garden.getStreetName());
            existingGarden.get().setSuburb(garden.getSuburb());
            existingGarden.get().setCity(garden.getCity());
            existingGarden.get().setCountry(garden.getCountry());
            existingGarden.get().setPostCode(garden.getPostCode());
            existingGarden.get().setSize(garden.getSize());
            existingGarden.get().setDescription(garden.getDescription());
            existingGarden.get().setLon(garden.getLon());
            existingGarden.get().setLat(garden.getLat());
            existingGarden.get().setWeatherForecast(Collections.emptyList());
            gardenService.addGarden(existingGarden.get());
        }
        return "redirect:/gardens/" + id;
    }

    /**
     * gets all public gardens
     * @param page page number
     * @param size size of page
     * @param model representation of results
     * @return publicGardens page
     */
    @GetMapping("/gardens/public")
    public String publicGardens(
            @RequestParam(defaultValue = "0") String pageStr,
            @RequestParam(defaultValue = "10") String sizeStr,
            Model model) {
        int page;
        int size;

        try {
            page = Math.max(0, Integer.parseInt(pageStr));
            size = Math.max(10, Integer.parseInt(sizeStr));
        } catch (NumberFormatException e) {
            page = 0;
            size = 10;
        }
        logger.info("Get /gardens/public - display all public gardens");
        Pageable pageable = PageRequest.of(page, size);
        Page<Garden> gardenPage = gardenService.getPageForPublicGardens(pageable);
        model.addAttribute("gardenPage", gardenPage);
        List<Garden> gardens = gardenService.getGardensByOwnerId(gardenUserService.getCurrentUser().getId());
        model.addAttribute("gardens", gardens);
        List<Garden> gardensWithPlants = gardenPage.getContent().stream()
                .map(garden -> {
                    List<Plant> plants = plantService.getPlantsByGardenId(garden.getId());
                    garden.setPlants(plants);
                    return garden;
                })
                .collect(Collectors.toList());
        return "gardens/publicGardens";
    }


    /**
     * Gets the id of garden
     * @param model representation of results
     * @return viewFriendGardens page
     */
    @GetMapping("/viewFriendGardens/{id}")
    public String viewFriendGardens(
        Authentication authentication,
        @PathVariable() Long id,
        Model model) {
            Long loggedInUserId = (Long) authentication.getPrincipal();
            Friends isFriend = friendService.getFriendship(loggedInUserId, id);
            GardenUser owner = gardenUserService.getUserById(id);

           List<Garden> privateGardens = gardenService.getPrivateGardensByOwnerId(owner);
           List<Garden> publicGardens = gardenService.getPublicGardensByOwnerId(owner);
           if (isFriend != null) {
               model.addAttribute("privateGardens", privateGardens);
           }

           model.addAttribute("publicGardens", publicGardens);

        return "gardens/friendGardens";
    }


    /**
     * send the user to public gardens with a subset of gardens matching
     * their given search
     * @param search string that user is searching
     * @param model representation of results
     * @return public garden page
     */
    @PostMapping("/gardens/public/search")
    public String searchPublicGardens(@RequestParam (defaultValue = "0") int page,
                                      @RequestParam (defaultValue = "10") int size,
                                      @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                      Model model) {
        logger.info("Search: " + search);
        Pageable pageable = PageRequest.of(page, size);
        Page<Garden> gardenPage = gardenService.findPageThatContainsQuery(search, pageable);
        model.addAttribute("gardenPage", gardenPage);
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);
        model.addAttribute("gardens", gardens);
        model.addAttribute("previousSearch", search);
        return "gardens/publicGardens";
    }
}




