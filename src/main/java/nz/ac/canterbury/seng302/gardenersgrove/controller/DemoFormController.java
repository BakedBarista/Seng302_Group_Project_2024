package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FormResult;
import nz.ac.canterbury.seng302.gardenersgrove.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for form example.
 * Note the @link{Autowired} annotation giving us access to the @lnik{FormService} class automatically
 */
@Controller
public class DemoFormController {
    Logger logger = LoggerFactory.getLogger(DemoFormController.class);

    private final FormService formService;

    @Autowired
    public DemoFormController(FormService formService) {
        this.formService = formService;
    }
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param displayName previous name entered into form to be displayed
     * @param displayLanguage previous favourite programming language entered into form to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/form")
    public String form(@RequestParam(name="displayName", required = false, defaultValue = "") String displayName,
                       @RequestParam(name="displayFavouriteLanguage", required = false, defaultValue = "") String displayLanguage,
                       Model model) {
        logger.info("GET /form");
        model.addAttribute("displayName", displayName);
        model.addAttribute("displayFavouriteLanguage", displayLanguage);
        model.addAttribute("isJava", displayLanguage.equalsIgnoreCase("java"));
        return "demoFormTemplate";
    }

    /**
     * Posts a form response with name and favourite language
     * @param name name if user
     * @param favouriteLanguage users favourite programming language
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
     */
    @PostMapping("/form")
    public String submitForm( @RequestParam(name="name") String name,
                              @RequestParam(name = "favouriteLanguage") String favouriteLanguage,
                              Model model) {
        logger.info("POST /form");
        formService.addFormResult(new FormResult(name, favouriteLanguage));
        model.addAttribute("displayName", name);
        model.addAttribute("displayFavouriteLanguage", favouriteLanguage);
        model.addAttribute("isJava", favouriteLanguage.equalsIgnoreCase("java"));
        return "demoFormTemplate";
    }

    /**
     * Gets all form responses
     * @param model (map-like) representation of results to be used by thymeleaf
     * @return thymeleaf demoResponseTemplate
     */
    @GetMapping("/form/responses")
    public String responses(Model model) {
        logger.info("GET /form/responses");
        model.addAttribute("responses", formService.getFormResults());
        return "demoResponsesTemplate";
    }
}
