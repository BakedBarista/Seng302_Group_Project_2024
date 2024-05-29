package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.DateTimeFormats.NZ_FORMAT_DATE;


/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);
    private final GardenService gardenService;
    private final PlantService plantService;
    private final WeatherAPIService weatherAPIService;
    
    private final TagService tagService;

    private final ModerationService moderationService;

    private final GardenUserService gardenUserService;
    private final FriendService friendService;

    private final ProfanityService profanityService;
    private final String PROFANITY = "profanity";

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService, GardenUserService gardenUserService, WeatherAPIService weatherAPIService, TagService tagService, FriendService friendService, ModerationService moderationService, ProfanityService profanityService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.gardenUserService = gardenUserService;
        this.weatherAPIService = weatherAPIService;
        this.tagService = tagService;
        this.friendService = friendService;
        this.moderationService = moderationService;
        this.profanityService = profanityService;
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
                             BindingResult bindingResult, Authentication authentication, Model model) {
        logger.info("POST /gardens - submit the new garden form");

        checkGardenError(model,bindingResult,garden);
        if (bindingResult.hasErrors() || model.containsAttribute(PROFANITY)) {
            model.addAttribute("garden", garden);
            return "gardens/createGarden";
        }



        Long userId = (Long) authentication.getPrincipal();

        GardenUser owner = gardenUserService.getUserById(userId);

        garden.setOwner(owner);

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
            model.addAttribute("NZ_FORMAT_DATE", NZ_FORMAT_DATE);
            model.addAttribute("plants", plantService.getPlantsByGardenId(id));
            List<List<Map<String, Object>>> weatherResult;

            if(garden.getLat() != null || garden.getLon() != null){
                weatherResult = weatherAPIService.getWeatherData(id, garden.getLat(), garden.getLon());
            }else {
                weatherResult = new ArrayList<>();
            }

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
        if (!garden.isPresent() || !garden.get().getOwner().getId().equals(owner.getId())) {
            return "/error/accessDenied";
        }
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

        checkGardenError(model,result,garden);
        if (result.hasErrors() || model.containsAttribute(PROFANITY)) {
            model.addAttribute("garden", garden);
            model.addAttribute("id", id);
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
     * @param model representation of results
     * @return publicGardens page
     */
    @GetMapping("/gardens/public")
    public String publicGardens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
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
    @GetMapping("/gardens/public/search")
    public String searchPublicGardens(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                      @RequestParam(name = "tags", required = false) List<String> tags,
                                      Model model) {
        Pageable pageable = PageRequest.of(page, size);
        List<Tag> validTags = new ArrayList<>();
       String invalidTag = "";

        for (String tagName : tags) {
            Tag tag = tagService.getTag(tagName);
            if (tag != null) {
                validTags.add(tag);
            } else {
                invalidTag = tagName ;
            }
        }

        List<String> validTagNames = validTags.stream().map(Tag::getName).toList();
        Page<Garden> gardenPage = gardenService.findGardensBySearchAndTags(search, validTagNames, pageable);

        if (!invalidTag.isEmpty()) {
            // Error for invalid tag
            String errorMessage = "No tag matching: " + String.join(", ", invalidTag);
            model.addAttribute("error", errorMessage);
            model.addAttribute("invalidTag", invalidTag);  // Assuming only one tag is processed at a time
        }

        model.addAttribute("gardenPage", gardenPage);
        model.addAttribute("previousSearch", search);
        model.addAttribute("previousTags", validTagNames);  // Only valid tags should go back into the tags list

        return "gardens/publicGardens";
    }

    /**
     * Helper method to check garden errors
     * @param model model to add error attributes
     * @param bindingResult to get location error
     * @param garden garden object
     */
    public void checkGardenError(Model model, BindingResult bindingResult, Garden garden) {
        List<String> locationErrorNames = Arrays.asList("city", "country", "suburb", "streetNumber", "streetName", "postCode");
        boolean profanityFlagged = !profanityService.badWordsFound(garden.getDescription()).isEmpty();
        if (!profanityFlagged) {
            profanityFlagged = moderationService.checkIfDescriptionIsFlagged(garden.getDescription());
        }
        if (bindingResult.hasErrors() || profanityFlagged) {
            if (profanityFlagged) {
                model.addAttribute(PROFANITY, "The description does not match the language standards of the app.");
            }
            for (FieldError error : bindingResult.getFieldErrors()) {
                if (locationErrorNames.contains(error.getField())) {
                    var errorCode = error.getCode();
                    if (errorCode != null) {
                        String errorMessage;
                        if (errorCode.equals("Pattern")) {
                            errorMessage = "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes";
                        } else {
                            errorMessage = "Location cannot be empty";
                        }
                        model.addAttribute("locationError", errorMessage);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Create test data
     * @throws IOException When problem reading file.
     * ChatGPT help with processing sql queries to arraylist
     */
    @PostConstruct
    public void dummyGardens() throws IOException {
        try {
            logger.info("Adding test data");

            // Create user
            GardenUser user = new GardenUser("Jan", "Doe", "jan.doe@gmail.com", "password", LocalDate.of(1970, 1, 1));
            gardenUserService.addUser(user);

            // Garden names
            List<String> gardenNames = Arrays.asList(
                    "Gardeners Paradise", "My Parents Garden", "Home Number 1", "GreenFingers", "Dirt Pile",
                    "Potato Heaven", "Needs Weeding", "My Work in Progress", "Freshly Built", "Greener Pastures",
                    "Grassy Grove", "Husbands Project", "Rainy Garden", "Tomato Garden", "Berries",
                    "Roots", "Need a professional", "Growers Garden", "Community Garden", "Free for all Garden"
            );

            // Plant details
            List<String[]> plantsDetails = Arrays.asList(
                    new String[][]{
                            {"Tomato", "Red"}, {"Cucumber", "Yellow"}, {"Potato", "Purple"},
                            {"Cabbage", "Pink"}, {"Lettuce", "White"}, {"Onion", "Orange"},
                            {"Spring Onion", "Blue"}, {"Asparagus", "Green"}, {"Pumpkin", "Purple"},
                            {"Carrot", "Red"}
                    }
            );

            for (int i = 0; i < gardenNames.size(); i++) {
                String gardenName = gardenNames.get(i);
                String streetNumber = Integer.toString(i + 1);
                Garden garden = new Garden(gardenName, streetNumber, "Ilam Road", "Ilam", "Christchurch", "New Zealand", "8041", -43.5320, 172.6366, (String.valueOf(1000 + (i * 50))), "Test Garden");
                garden.setOwner(user);
                garden.setPublic(true);
                gardenService.addGarden(garden);

                List<Plant> plants = new ArrayList<>();
                for (int j = 0; j < plantsDetails.size(); j++) {
                    String[] plantDetail = plantsDetails.get(j);
                    String plantName = plantDetail[0];
                    String plantDescription = plantDetail[1];
                    Plant plant = new Plant(plantName, "15", plantDescription, LocalDate.of(2024, 3, 1));
                    Plant savedPlant = plantService.addPlant(plant, garden.getId());

                    plants.add(savedPlant);
                }

                garden.setPlants(plants);
                gardenService.addGarden(garden);
            }
        } catch (Exception e) {
            logger.info("Failed to add garden");
        }
    }
}




