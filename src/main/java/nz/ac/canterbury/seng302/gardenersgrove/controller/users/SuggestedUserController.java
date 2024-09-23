package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.CompatibilityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SuggestedUserController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserController.class);

    private final FriendService friendService;
    private final GardenUserService gardenUserService;
    private final SuggestedUserService suggestedUserService;
    private final CompatibilityService compatibilityService;
    private final ObjectMapper objectMapper;
    private final TemplateEngine templateEngine;

    private static final String SUCCESS = "success";

    @Autowired
    public SuggestedUserController(FriendService friendService, GardenUserService gardenUserService, SuggestedUserService suggestedUserService, CompatibilityService compatibilityService, ObjectMapper objectMapper, TemplateEngine templateEngine) {
        this.friendService = friendService;
        this.gardenUserService = gardenUserService;
        this.suggestedUserService = suggestedUserService;
        this.compatibilityService = compatibilityService;
        this.objectMapper = objectMapper;
        this.templateEngine = templateEngine;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     *
     * @return the home page
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
        logger.info("GET /");

        if (authentication == null) {
            logger.info("User is not logged in, directing to landing page.");
            return "home";
        }

        try {
            Long userId = (Long) authentication.getPrincipal();
            GardenUser user = gardenUserService.getUserById(userId);
            List<GardenUser> suggestedUsers = new ArrayList<>();
            suggestedUsers.addAll(friendService.receivedConnectionRequests(user));
            suggestedUsers.addAll(friendService.availableConnections(user));

            if (suggestedUsers.isEmpty()) {
                return "suggestedFriends";
            }
            logger.info("4");
            model.addAttribute("userId", suggestedUsers.get(0).getId());
            model.addAttribute("name", suggestedUsers.get(0).getFullName());
            model.addAttribute("description", suggestedUsers.get(0).getDescription());
            logger.info("Description: {}", suggestedUsers.get(0).getDescription());

            List<Friends> friendList = friendService.getReceivedRequests(userId);
            List<GardenUser> friends = friendService.getPendingRequestGardenUser(friendList, userId);

            //sorting incoming pending requests
            List<SuggestedUserDTO> pendingRequestList = getSortedSuggestedUserDTOs(friends, user, request, response);

            // this is to sort people that arnt incoming pending requests
            List<GardenUser> connectionListMinusFriends = suggestedUsers.stream().filter( x -> !friends.contains(x)).toList();

            List<SuggestedUserDTO> sortedOtherConnections = getSortedSuggestedUserDTOs(connectionListMinusFriends, user, request, response);

            // combining into a full sorted list
            List<SuggestedUserDTO> combinedList = new ArrayList<>(pendingRequestList);

            combinedList.addAll(sortedOtherConnections);
      
            String jsonUsers = objectMapper.writeValueAsString(combinedList);
            model.addAttribute("userList", jsonUsers);
        } catch (Exception e) {
            logger.error("Error getting suggested users", e);
        }
        return "suggestedFriends";
    }

    private SuggestedUserDTO makeSuggestedUserDTO(GardenUser self, GardenUser user, HttpServletRequest request,
            HttpServletResponse response) {
        SuggestedUserDTO dto = new SuggestedUserDTO(user);

        double compatibility = compatibilityService.friendshipCompatibilityQuotient(self, user);
        dto.setCompatibility((int) Math.round(compatibility));

        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", user.getId());
        variables.put("favouriteGarden", user.getFavoriteGarden());
        variables.put("favouritePlants", user.getFavouritePlants());

        // Manually render thymeleaf fragments for the backside of each card
        JakartaServletWebApplication application = JakartaServletWebApplication
                .buildApplication(request.getServletContext());
        WebContext context = new WebContext(application.buildExchange(request, response), request.getLocale(),
                variables);
        if (user.getFavoriteGarden() != null) {
            dto.setFavouriteGardenHtml(templateEngine.process("fragments/favourite-garden.html", context));
        } else {
            dto.setFavouriteGardenHtml("<div class=\"text-center my-3 text-white\">No Favourite Garden Selected</div>");
        }
        if (user.getFavouritePlants() != null) {
            dto.setFavouritePlantsHtml(templateEngine.process("fragments/favourite-plants.html", context));
        } else {
            dto.setFavouritePlantsHtml("<div class=\"text-center my-3 text-white\">No Favourite Plants Selected</div>");
        }

        return dto;
    }

    public List<SuggestedUserDTO> getSortedSuggestedUserDTOs(List<GardenUser> connectionListMinusFriends, GardenUser user, HttpServletRequest request, HttpServletResponse response){
        List<SuggestedUserDTO> otherConnections = new ArrayList<SuggestedUserDTO>();
        List<SuggestedUserDTO> sortedOtherConnections = new ArrayList<>();

        if(!connectionListMinusFriends.isEmpty()){
                otherConnections = connectionListMinusFriends.stream().map((GardenUser u) -> makeSuggestedUserDTO(user, u, request, response)).toList();
                 for (SuggestedUserDTO dto : otherConnections) {
                    sortedOtherConnections.add(dto);
                }
                sortedOtherConnections.sort(Comparator.comparingInt(SuggestedUserDTO::getCompatibility).reversed());
        }
        return sortedOtherConnections;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> handleAcceptDecline(
            @RequestParam(name = "action") String action,
            @RequestParam(name = "id") Long suggestedId,
            Authentication authentication,
            Model model) {
        logger.info("Post /");
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = gardenUserService.getUserById(loggedInUserId);
        GardenUser suggestedUser = gardenUserService.getUserById(suggestedId);
        Map<String, Object> response = new HashMap<>();

        if (loggedInUser == null) {
            logger.error("User is not logged in, doing nothing");
            response.put(SUCCESS, false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        boolean validationPassed = suggestedUserService.validationCheck(loggedInUserId, suggestedId);
        if (!validationPassed) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        switch (action) {
            case "accept":
                // Look for a pending request and accept it if one exists.
                boolean pendingRequestAccepted = suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedId);
                if (pendingRequestAccepted) {
                    logger.info("Pending request from suggested user accepted, user's are now friends");
                    response.put(SUCCESS, true);
                } else { // Send a new pending request to the suggested user
                    boolean newRequestSent = suggestedUserService.sendNewPendingRequest(loggedInUser, suggestedUser);
                    if (!newRequestSent) {
                        logger.error("Users already have a pending request. Doing nothing");
                        break;
                    }
                }
                break;
            case "decline":
                // Look for a pending request and decline it
                boolean pendingRequestDeclined = suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedId);
                if (pendingRequestDeclined) {
                    logger.info("Pending request from suggested user declined, user won't be shown again");
                } else {
                    boolean declineStatusSet = suggestedUserService.setDeclinedFriendship(loggedInUser, suggestedUser);
                    if (!declineStatusSet) {
                        logger.error("Something went wrong trying to set a declined friendship. Doing nothing");
                        break;
                    }
                }
                break;
            default:
                logger.error("Action is neither 'accept' or 'decline'. Doing nothing");
                break;
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
