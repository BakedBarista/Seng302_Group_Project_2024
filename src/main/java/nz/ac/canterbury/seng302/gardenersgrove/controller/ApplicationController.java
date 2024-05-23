package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class ApplicationController {
    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private GardenService gardenService;
    private GardenUserService gardenUserService;

    @Autowired
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    /**
     * Redirects GET default url '/' to '/demo'
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String home( Model model) {
        logger.info("GET /");
        try {
            GardenUser owner = gardenUserService.getCurrentUser();
            if(owner.getId() != null) {
                List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
                model.addAttribute("gardens", gardens);
            }
        }
        catch (Exception e) {
        logger.error("Error getting gardens for user");
        }
        return "home";
    }
}
