package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller defines the base application end points.
 */
@Controller
public class ApplicationController {
    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    /**
     * The magic web socket test
     * @return the web socket test page
     */
    @GetMapping("/ws-test")
    public String wsTest() {
        return "wsTest";
    }
}
