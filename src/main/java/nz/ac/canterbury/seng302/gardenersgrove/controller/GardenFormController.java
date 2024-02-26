package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenFormResult;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenFormService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for garden forms
 */
@Controller
public class GardenFormController {
    Logger logger = LoggerFactory.getLogger(GardenFormController.class);

    private final GardenFormService formService;

    @Autowired
    public GardenFormController(GardenFormService formService) {
        this.formService = formService;
    }

    /**
     * Gets form to be displayed
     * @param displayName  garden name to be displayed
     * @param displayLocation garden location to be displayed
     * @param displaySize garden size to be displayed
     * @param model representation of name, location and size
     * @return gardenFormTemplate
     */
    @GetMapping("/gardenform")
    public String form(@RequestParam(name="displayName", required = false, defaultValue = "") String displayName,
                       @RequestParam(name="displayLocation", required = false, defaultValue = "") String displayLocation,
                       @RequestParam(name="displaySize", required = false, defaultValue = "") String displaySize,
                       Model model) {
        logger.info("Get /gardenform");
        formService.addGardenFormResult(new GardenFormResult(displayName, displayLocation, displaySize));
        model.addAttribute("displayName", displayName);
        model.addAttribute("displayGardenLocation", displayLocation);
        model.addAttribute("displayGardenSize", displaySize);
        return "gardenFormTemplate";
    }

    @PostMapping("/gardenform")
    public String submitForm( @RequestParam(name="name") String gardenName,
                              @RequestParam(name = "location") String gardenLocation,
                              @RequestParam(name = "size") String gardenSize,
                              Model model) {
        logger.info("POST /gardenform");
        boolean hasErrors = false;
        // Garden name validation
        if (gardenName == null || gardenName.trim().isEmpty()) {
            model.addAttribute("nameError", "Garden name cannot be empty");
            hasErrors = true;
        } else if (!gardenName.matches("^[A-Za-z0-9 .,'-]+$")) {
            model.addAttribute("nameError", "Garden name must only include letters, numbers, spaces, dots, hyphens or apostrophes");
            hasErrors = true;
        }

        // Location validation
        if (gardenLocation == null || gardenLocation.trim().isEmpty()) {
            model.addAttribute("locationError", "Location cannot be empty");
            hasErrors = true;
        } else if (!gardenLocation.matches("^[A-Za-z0-9 ,.'-]+$")) {
            model.addAttribute("locationError", "Location name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes");
            hasErrors = true;
        }

        // Garden size validation
        if (gardenSize == null || gardenSize.trim().isEmpty()) {
            model.addAttribute("sizeError", "Garden size must be a positive number");
            hasErrors = true;
        } else {
            gardenSize = gardenSize.replace(',', '.'); // Replace comma with dot for number parsing
            try {
                double size = Double.parseDouble(gardenSize);
                if (size <= 0) {
                    throw new NumberFormatException("Size must be positive");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("sizeError", "Garden size must be a positive number");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            model.addAttribute("gardenName", gardenName);
            model.addAttribute("gardenLocation", gardenLocation);
            model.addAttribute("gardenSize", gardenSize);

            // Return to the form with errors
            return "gardenFormTemplate";
        }
        formService.addGardenFormResult(new GardenFormResult(gardenName,gardenLocation,gardenSize));
        model.addAttribute("displayName", gardenName);
        model.addAttribute("displayGardenLocation", gardenLocation);
        model.addAttribute("displayGardenSize", gardenSize);
        return "redirect:./gardenform/gardens";
    }

    /**
     * Gets all garden details
     * @param model representation of results
     * @return viewGardenTemplate
     */
    @GetMapping("/gardenform/gardens")
    public String responses(Model model) {
        logger.info("Get /gardenform/gardens");
        model.addAttribute("gardens", formService.getFormResults());
        return "viewGardenTemplate";
    }
}
