package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
//        formService.addGardenFormResult(new Garden(displayName, displayLocation, displaySize));
//        model.addAttribute("displayName", displayName);
//        model.addAttribute("displayGardenLocation", displayLocation);
//        model.addAttribute("displayGardenSize", displaySize);
        return "gardens/createGarden";
    }

    @PostMapping("/gardens/create")
    public String submitForm( @RequestParam(name="name") String gardenName,
                              @RequestParam(name = "location") String gardenLocation,
                              @RequestParam(name = "size") String gardenSize,
                              Model model) {
        logger.info("POST /gardens - submit the new garden form");

        if (validateGardenName(gardenName, model) ||
                validateGardenLocation(gardenLocation, model) ||
                validateGardenSize(gardenSize, model)) {
            model.addAttribute("gardenName", gardenName);
            model.addAttribute("gardenLocation", gardenLocation);
            model.addAttribute("gardenSize", gardenSize);

            // Return to the form with errors
            return "redirect:/gardens/create";
        }
        Garden savedGarden = gardenService.addGarden(new Garden(gardenName,gardenLocation,gardenSize));
        model.addAttribute("displayName", gardenName);
        model.addAttribute("displayGardenLocation", gardenLocation);
        model.addAttribute("displayGardenSize", gardenSize);
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
        model.addAttribute("plants", plantService.getPlantsByGardenId(id));
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

        if (validateGardenName(newName, model) ||
                validateGardenLocation(newLocation, model) ||
                validateGardenSize(newSize, model)) {
            model.addAttribute("gardenName", newName);
            model.addAttribute("gardenLocation", newLocation);
            model.addAttribute("gardenSize", newSize);
            return "redirect:../../gardens/"+id+"/edit";
        }

        Optional<Garden> garden = gardenService.getGardenById(id);
        Garden updatedGarden = garden.orElse(null);
        updatedGarden.setName(newName);
        updatedGarden.setLocation(newLocation);
        updatedGarden.setSize(newSize);

        gardenService.addGarden(updatedGarden);
        return "redirect:../../gardens";
    }

    /**
     * validate gardenName
     * @param gardenName
     * @param model
     * @return true if invalid
     */
    public boolean validateGardenName(String gardenName, Model model) {
        // Garden name validation
        if (gardenName == null || gardenName.trim().isEmpty()) {
            model.addAttribute("nameError", "Garden name cannot be empty");
            return true;
        } else if (!gardenName.matches("^[A-Za-z0-9 .,'-]+$")) {
            model.addAttribute("nameError", "Garden name must only include letters, numbers, spaces, dots, hyphens or apostrophes");
            return true;
        }
        return false;
    }

    /**
     * validate garden location
     * @param gardenLocation
     * @param model
     * @return true if invalid
     */
    public boolean validateGardenLocation(String gardenLocation, Model model) {
        if (gardenLocation == null || gardenLocation.trim().isEmpty()) {
            model.addAttribute("locationError", "Location cannot be empty");
            return true;
        } else if (!gardenLocation.matches("^[A-Za-z0-9 ,.'-]+$")) {
            model.addAttribute("locationError", "Location name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes");
            return true;
        }
        return false;
    }

    /**
     * validate garden size
     * @param gardenSize
     * @param model
     * @return true if invalid
     */
    public boolean validateGardenSize(String gardenSize, Model model) {
        if (!gardenSize.trim().isEmpty()) {
            gardenSize = gardenSize.replace(',', '.'); // Replace comma with dot for number parsing
            try {
                double size = Double.parseDouble(gardenSize);
                if (size < 0) {
                    throw new NumberFormatException("Size must be positive");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("sizeError", "Garden size must be a positive number");
                return true;
            }
        }
        return false;
    }
}
