package nz.ac.canterbury.seng302.gardenersgrove.controller;


import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller for garden forms
 */
@Controller
public class GardenController {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private final GardenService gardenService;

    @Autowired
    public GardenController(GardenService gardenService) {
        this.gardenService = gardenService;
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
        model.addAttribute("garden", new Garden()); // Create a new garden object to be used in the form
        return "gardens/createGarden";
    }

    //In this case Model Attribute is used to bind the form data to an instance of the garden class. Automatically populates the garden object with the form data
    //BindingResult is used to check for errors in the form. After spring binds the form data to the garden object, it checks for errors and if there are any,
    // it adds them to the BindingResult object which we can use to check if there have been any errors.
    @PostMapping("/gardens/create")
    public String submitForm(@Valid @ModelAttribute("garden") Garden garden,
                             BindingResult bindingResult, Model model) {
        logger.info("POST /gardens - submit the new garden form");

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

    @GetMapping("/gardens/{id}")
    public String gardenDetail(@PathVariable(name = "id") Long id,
                               Model model) {
        logger.info("Get /gardens/id - display garden detail");
        model.addAttribute("garden", gardenService.getGardenById(id).get());
        return "gardens/gardenDetails";
    }

    /**
     * Get single garden details
     * @param model representation of results
     * @return editGarden page
     */
    @GetMapping("/gardens/{id}/edit")
    public String getGarden(@PathVariable() long id, Model model) {
        logger.info("Get /garden/{}", id);
        Optional<Garden> garden = gardenService.getGardenById(id);
        model.addAttribute("garden", garden.orElse(null));
        return "/gardens/editGarden";
    }

    /**
     * Get single garden details
     * @param model representation of results
     * @return redirect to gardens page
     */
    @PostMapping("/gardens/{id}/edit")
    public String updateGarden(@PathVariable() long id,
                               @RequestParam(name="name") String newName,
                               @RequestParam(name="location") String newLocation,
                               @RequestParam(name="size") String newSize,
                               Model model) {

        model.addAttribute("gardenName", newName);
        model.addAttribute("gardenLocation", newLocation);
        model.addAttribute("gardenSize", newSize);


        Optional<Garden> garden = gardenService.getGardenById(id);
        Garden updatedGarden = garden.orElse(null);
        updatedGarden.setName(newName);
        updatedGarden.setLocation(newLocation);
        updatedGarden.setSize(newSize);

        gardenService.addGarden(updatedGarden);
        return "redirect:../../gardens";
    }






}
