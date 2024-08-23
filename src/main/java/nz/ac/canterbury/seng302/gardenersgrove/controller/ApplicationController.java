package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
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
 * This controller defines the base application end points.
 */
@Controller
public class ApplicationController {
    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private GardenService gardenService;
    private GardenUserService gardenUserService;

    @Autowired
    public ApplicationController(GardenService gardenService, GardenUserService gardenUserService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     * @return the home page
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

    @GetMapping("/ws-test")
    public String wsTest() {
        return "wsTest";
    }
}
