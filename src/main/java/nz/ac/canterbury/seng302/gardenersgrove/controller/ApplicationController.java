package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    private GardenService gardenService;
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Redirects GET default url '/' to '/demo'
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String home( Model model) {
        logger.info("GET /");
        this.gardenService = new GardenService(gardenRepository);
        List<Garden> gardens = gardenService.getAllGardens();
        model.addAttribute("gardens", gardens);
        return "home";
    }

    /**
     * Sends a test email to the given email address
     */
    @GetMapping("/test-email")
    public void testEmail(@Param("to") String to) {
        emailSenderService.sendEmail(to, "Test email", "Test email sent using Spring Boot Email");
    }
}
