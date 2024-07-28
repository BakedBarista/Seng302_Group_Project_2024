package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.CurrentWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.WeatherData;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.WeatherAPICurrentResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.NZ_FORMAT_DATE;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.WEATHER_CARD_FORMAT_DATE;


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
    private final LocationService locationService;

    private final ProfanityService profanityService;
    private final String PROFANITY = "profanity";

    @Value("${geoapify.api.key}")
    private String location_apiKey;

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService, GardenUserService gardenUserService,
                            WeatherAPIService weatherAPIService, TagService tagService, FriendService friendService,
                            ModerationService moderationService, ProfanityService profanityService, LocationService locationService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.gardenUserService = gardenUserService;
        this.weatherAPIService = weatherAPIService;
        this.tagService = tagService;
        this.friendService = friendService;
        this.moderationService = moderationService;
        this.profanityService = profanityService;
        this.locationService = locationService;
    }

    /**
     * Gets form to be displayed
     * @param model representation of name, location and size
     * @return gardenFormTemplate
     */
    @GetMapping("/gardens/create")
    public String getCreateGardenForm(Model model) {
        logger.info("GET /gardens/create - display the new garden form");
        model.addAttribute("garden", new GardenDTO());
        GardenUser owner = gardenUserService.getCurrentUser();

        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "gardens/createGarden";
    }

    /**
     * Submits form to be displayed
     * @param gardenDTO   garden details
     * @param bindingResult binding result
     * @param model representation of results
     * @return gardenForm
     */
    @PostMapping("/gardens/create")
    public String submitCreateGardenForm(@Valid @ModelAttribute("garden") GardenDTO gardenDTO,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         Model model) {
        logger.info("POST /gardens - submit the new garden form");

        logger.info(gardenDTO.toString());

        checkGardenDTOError(model, bindingResult, gardenDTO);
        if (bindingResult.hasErrors() || model.containsAttribute(PROFANITY)) {
            model.addAttribute("garden", gardenDTO);
            return "gardens/createGarden";
        }

        // Request API
        List<Double> latAndLng = locationService.getLatLng(gardenDTO.getLocation());

        // Null check
        if (!latAndLng.isEmpty()) {
            gardenDTO.setLat(latAndLng.get(0));
            gardenDTO.setLon(latAndLng.get(1));
        } else {
            gardenDTO.setLat(null);
            gardenDTO.setLon(null);
        }

        Garden garden = gardenDTO.toGarden();
        Long userId = (Long) authentication.getPrincipal();
        GardenUser owner = gardenUserService.getUserById(userId);
        garden.setOwner(owner);

        logger.info(garden.toString());

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
     * Gets the details page for a garden based on its ID
     * @param model representation of results
     * @param id the ID of the garden wanted
     * @return gardenDetails page
     */
    @GetMapping("/gardens/{id}")
    public String gardenDetail(@PathVariable(name = "id") Long id,
                               Model model) {

        logger.info("Get /gardens/id - display garden detail");
        Optional<Garden> gardenOpt = gardenService.getGardenById(id);
        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());


        if(gardenOpt.isPresent()) {
            Garden garden = gardenOpt.get();
            model.addAttribute("garden", garden);
            model.addAttribute("owner", garden.getOwner());
            GardenUser currentUser = gardenUserService.getCurrentUser();
            boolean isNotOwner = !garden.getOwner().getId().equals(currentUser.getId());
            boolean isNotPublic = !garden.getIsPublic();

            if (isNotOwner && isNotPublic){
                return "/error/accessDenied";
            }
            model.addAttribute("NZ_FORMAT_DATE", NZ_FORMAT_DATE);
            model.addAttribute("plants", plantService.getPlantsByGardenId(id));

            logger.info("Getting weather information for Garden: {}", id);
            Double lat = garden.getLat();
            Double lng = garden.getLon();
            WeatherAPICurrentResponse currentResponse = new WeatherAPICurrentResponse();
            CurrentWeather currentWeather = new CurrentWeather();
            List<WeatherData> forecastWeather = new ArrayList<>();
            boolean wateringRecommendation = false;
            boolean displayWeatherAlert = false;
            boolean displayWeather = false;

            if (lat == null || lng == null) {
                logger.info("Garden ID: {} has no Lat and Lng, no weather will be displayed.", id);
            } else {
                GardenWeather gardenWeather = weatherAPIService.getWeatherData(id, lat, lng);
                logger.info("garden weather shit");
                // Check that the weather returned isn't null
                 if (gardenWeather == null) {
                     logger.error("Garden weather was returned as null, can't display");
                 } else {
                     // Extracts all the needed weather data
                     logger.info("Displaying the weather for Garden {}", id);
                     currentResponse = weatherAPIService.getCurrentWeatherFromAPI(lat, lng);
                     forecastWeather = gardenWeather.getForecastWeather();
                     currentWeather = weatherAPIService.extractCurrentWeatherData(currentResponse);

                     wateringRecommendation = weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse);
                     garden.setWateringRecommendation(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
                     displayWeatherAlert = garden.getDisplayWeatherAlert();
                     displayWeather = true;

                     if (garden.getAlertHidden() == null || !garden.getAlertHidden().isEqual(LocalDate.now())) {
                         logger.info("Garden alert hide status expired, showing watering alert again.");
                         garden.setAlertHidden(null);
                         garden.setDisplayWeatherAlert(true);
                         gardenService.addGarden(garden);
                         displayWeatherAlert = true;
                     }
                 }
            }

            model.addAttribute("forecastWeather", forecastWeather);
            model.addAttribute("currentWeather", currentWeather);
            model.addAttribute("wateringRecommendation", wateringRecommendation);
            model.addAttribute("displayWeatherAlert", displayWeatherAlert);
            model.addAttribute("displayWeather", displayWeather);
            model.addAttribute("WEATHER_CARD_FORMAT_DATE", WEATHER_CARD_FORMAT_DATE);

            List<Garden> gardens = gardenService.getGardensByOwnerId(currentUser.getId());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("gardens", gardens);
            return "gardens/gardenDetails";
        }
        return "error/404";
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
            garden.setAlertHidden(LocalDate.now());
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
     * @param gardenDTO garden details
     * @param result binding result
     * @param model representation of results
     * @return redirect to gardens
     */
    @PostMapping("/gardens/{id}/edit")
    public String updateGarden(@PathVariable(name = "id") long id,
                               @Valid @ModelAttribute("garden") GardenDTO gardenDTO,
                               BindingResult result,
                               Model model) {

        checkGardenDTOError(model, result, gardenDTO);
        if (result.hasErrors() || model.containsAttribute(PROFANITY)) {
            model.addAttribute("garden", gardenDTO);
            model.addAttribute("id", id);
            return "gardens/editGarden";
        }

        List<Double> latAndLng = locationService.getLatLng(gardenDTO.getLocation());

        Optional<Garden> existingGarden = gardenService.getGardenById(id);
        if (existingGarden.isPresent()) {
            existingGarden.get().setName(gardenDTO.getName());
            existingGarden.get().setStreetNumber(gardenDTO.getStreetNumber());
            existingGarden.get().setStreetName(gardenDTO.getStreetName());
            existingGarden.get().setSuburb(gardenDTO.getSuburb());
            existingGarden.get().setCity(gardenDTO.getCity());
            existingGarden.get().setCountry(gardenDTO.getCountry());
            existingGarden.get().setPostCode(gardenDTO.getPostCode());
            existingGarden.get().setSize(gardenDTO.getSize());
            existingGarden.get().setDescription(gardenDTO.getDescription());

            // Null check
            if (!latAndLng.isEmpty()) {
                existingGarden.get().setLat(latAndLng.get(0));
                existingGarden.get().setLon(latAndLng.get(1));
            } else {
                existingGarden.get().setLat(null);
                existingGarden.get().setLon(null);
            }

            existingGarden.get().setGardenWeather(null);
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
                                      @RequestParam(name = "tags", required = false) String tags,
                                      Model model) {
        Pageable pageable = PageRequest.of(page, size);

        boolean hasTags = !tags.isEmpty();
        List<String> tagNames = null;
        if (hasTags) {
            tagNames = Arrays.asList(tags.split(","));
        }
        Page<Garden> gardenPage = gardenService.findGardensBySearchAndTags(search, tagNames, pageable);

        model.addAttribute("gardenPage", gardenPage);
        model.addAttribute("previousSearch", search);
        model.addAttribute("tagString", tags);

        return "gardens/publicGardens";
    }

    /**
     * Helper method to check garden errors
     * @param model model to add error attributes
     * @param bindingResult to get location error
     * @param garden garden object
     */
    public void checkGardenDTOError(Model model, BindingResult bindingResult, GardenDTO garden) {
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

            GardenUser user1 = new GardenUser("Luke", "Stynes", "stynesluke@gmail.com", "password", LocalDate.of(1970, 1, 1));
            gardenUserService.addUser(user1);

            logger.info("User " + user.getFullName() + " added");

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
                GardenDTO gardenDTO = new GardenDTO(gardenName, streetNumber, "Ilam Road", "Ilam", "Christchurch", "New Zealand", "8041", -43.53, 172.63, "Test Garden", (String.valueOf(1000 + (i * 50))));
                Garden garden = gardenDTO.toGarden();
                garden.setOwner(user);
                garden.setPublic(true);
                gardenService.addGarden(garden);

                logger.info("Garden " + gardenName + "added");

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




