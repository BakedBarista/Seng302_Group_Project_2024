package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SuggestedUserController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserController.class);

    private final FriendService friendService;
    private final GardenUserService gardenUserService;

    private final ObjectMapper objectMapper;


    @Autowired
    public SuggestedUserController(FriendService friendService, GardenUserService gardenUserService, ObjectMapper objectMapper) {
        this.friendService = friendService;
        this.gardenUserService = gardenUserService;
        this.objectMapper = objectMapper;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     * @return the home page
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        logger.info("GET /");
        try {
            Long userId = (Long) authentication.getPrincipal();
            GardenUser user = gardenUserService.getUserById(userId);

            List<GardenUser> suggestedUsers  = friendService.availbleConnections(user);

            model.addAttribute("userId", suggestedUsers.get(0).getId());
            model.addAttribute("name", suggestedUsers.get(0).getFullName());
            model.addAttribute("description", suggestedUsers.get(0).getDescription());


            String jsonUsers = objectMapper.writeValueAsString(suggestedUsers);
            logger.info(jsonUsers);
            model.addAttribute("userList", jsonUsers);
            
        }
        catch (Exception e) {
            logger.error("Error getting gardens for user");
        }
        return "home";
    }
}
