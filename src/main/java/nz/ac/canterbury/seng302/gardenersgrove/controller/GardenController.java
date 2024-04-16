package nz.ac.canterbury.seng302.gardenersgrove.controller;


import com.modernmt.text.profanity.dictionary.Profanity;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationSequence;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Optional;
import java.util.Set;

import com.modernmt.text.profanity.*;
import org.springframework.web.client.RestTemplate;

/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;

    @Autowired
    ModerationService moderationService;

    ProfanityService filterProxy = new ProfanityService();;

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
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
        List<Garden> gardens = gardenService.getAllGardens();
        model.addAttribute("gardens", gardens);
        return "gardens/createGarden";
    }

    /**
     * Submits form to be displayed
     * @param garden
     * @param bindingResult
     * @param model
     * @return gardenForm
     */
    @PostMapping("/gardens/create")
    public String submitForm(@Validated(ValidationSequence.class) @ModelAttribute("garden") Garden garden,
                             BindingResult bindingResult, Model model) {
        logger.info("POST /gardens - submit the new garden form {} {} {} {} {}", garden.getName(),garden.getStreetNumber(),garden.getStreetName(), garden.getLon(), garden.getLat());
        if (bindingResult.hasErrors()) {
            model.addAttribute("garden", garden);

            return "gardens/createGarden";
        }

        // check to see if profanity is present for any language and inform the user if there is
        Profanity aProfanity = filterProxy.findAllLanguages(garden.getDescription());
        if (aProfanity != null){
            model.addAttribute("garden", garden);
            model.addAttribute("profanity", aProfanity.text());
            logger.info("Profanities detected: {}", aProfanity.text());
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
        model.addAttribute("gardens", gardenService.getAllGardens());
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
        model.addAttribute("garden", gardenService.getGardenById(id).get());
        model.addAttribute("plants", plantService.getPlantsByGardenId(id));
        List<Garden> gardens = gardenService.getAllGardens();
        model.addAttribute("gardens", gardens);
        return "gardens/gardenDetails";
    }

    /**
     * Updates the Garden
     * @param id
     * @return redirect to gardens
     */
    @GetMapping("/gardens/{id}/edit")
    public String getGarden(@PathVariable() long id, Model model) {
        logger.info("Get /garden/{}", id);
        Optional<Garden> garden = gardenService.getGardenById(id);
        logger.info(String.valueOf(garden));
        model.addAttribute("garden", garden.orElse(null));
        List<Garden> gardens = gardenService.getAllGardens();
        model.addAttribute("gardens", gardens);
        return "gardens/editGarden";
    }

    /**
     * Update garden details
     * @param id
     * @param garden
     * @param result
     * @param model
     * @return redirect to gardens
     */
    @PostMapping("/gardens/{id}/edit")
    public String updateGarden(@PathVariable long id,
                               @Validated(ValidationSequence.class) @ModelAttribute("garden") Garden garden,
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

        logger.info("POST /gardens - submit the new garden form {} {} {} {} {}", garden.getName(),garden.getStreetNumber(),garden.getStreetName(), garden.getLon(), garden.getLat());
        return "redirect:/gardens/" + id;
    }

}
