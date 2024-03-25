package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Optional;

/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private final GardenService gardenService;
    private final PlantService plantService;

    @Autowired
    public GardenController(GardenService gardenService, PlantService plantService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
    }

    /**
     * Gets form to be displayed
     * @param displayName  garden name to be displayed
     * @param displayLocation garden location to be displayed
     * @param displaySize garden size to be displayed
     * @param model representation of name, location and size
     * @return gardenFormTemplate
     */
    @GetMapping("/gardens/create")
    public String form(@RequestParam(name="displayName", required = false, defaultValue = "") String displayName,
                       @RequestParam(name="displayLocation", required = false, defaultValue = "") String displayLocation,
                       @RequestParam(name="displaySize", required = false, defaultValue = "") String displaySize,
                       Model model) {
        logger.info("GET /gardens/create - display the new garden form");
        model.addAttribute("garden", new Garden());
        List<Garden> gardens = gardenService.getAllGardens();
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
        garden.setSize(garden.getSize());
        if (bindingResult.hasErrors()) {
            model.addAttribute("garden", garden);

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
     * @param id garden id
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
            existingGarden.get().setLocation(garden.getLocation());
            existingGarden.get().setSize(garden.getSize());
            gardenService.addGarden(existingGarden.get());
        }
        return "redirect:/gardens/" + id;
    }







}
