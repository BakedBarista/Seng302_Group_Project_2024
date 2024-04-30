package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;
    private final WeatherAPIService weatherAPIService;

    private final GardenUserService gardenUserService;

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService, GardenUserService gardenUserService WeatherAPIService weatherApiService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.gardenUserService = gardenUserService;
        this.weatherApiService = weatherApiService;
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
        }


        GardenUser currentUser = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(currentUser.getId());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("gardens", gardens);

        //TODO: Implement with carls location API for lat lng
//        List<Map<String, Object>> forecastResult = weatherAPIService.getForecastWeather(id, 0.5, 0.5);
        model.addAttribute("weatherForecast", forecastResult);
        model.addAttribute("displayWeather", !forecastResult.isEmpty());
        return "gardens/gardenDetails";
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
    public String getGarden(@PathVariable() long id, Model model) {
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
    public String updateGarden(@PathVariable long id,
                               @Valid @ModelAttribute("garden") Garden garden,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
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
            gardenService.addGarden(existingGarden.get());
        }
        return "redirect:/gardens/" + id;
    }


    @GetMapping("/gardens/public")
    public String publicGardens(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            Model model) {
        logger.info("Get /gardens/public - display all public gardens");
        Pageable pageable = PageRequest.of(page, size);
        Page<Garden> gardenPage = gardenService.getPublicGardens(pageable);
        model.addAttribute("gardenPage", gardenPage);
        GardenUser owner = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "gardens/publicGardens";
    }

    /**
     * send the user to public gardens with a subset of gardens matching
     * their given search
     * @param search string that user is searching
     * @param model
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
