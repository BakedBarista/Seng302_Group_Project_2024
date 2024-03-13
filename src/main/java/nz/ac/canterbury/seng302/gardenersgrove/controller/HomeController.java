package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the homepage
 */
@Controller
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Shows the user the form
     *
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String register() {
        logger.info("GET /");
        return "homeTemplate";
    }

}
