package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.*;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;

@Service
public class SuggestedUserService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserService.class);
    private final FriendService friendService;
    private final CompatibilityService compatibilityService;
    private final BirthFlowerService birthFlowerService;
    private final TemplateEngine templateEngine;



    @Autowired
    public SuggestedUserService(FriendService friendService, CompatibilityService compatibilityService,
                                BirthFlowerService birthFlowerService, TemplateEngine templateEngine) {
        this.friendService = friendService;
        this.compatibilityService = compatibilityService;
        this.birthFlowerService = birthFlowerService;
        this.templateEngine = templateEngine;
    }

    /**
     * Performs a number of validation checks before dealing with the suggested user's accept or decline.
     * Checks the ID isn't the logged-in users own ID.
     * Checks the users aren't already friends.
     * Checks there isn't an existing declined request.
     * @param loggedInUserId the logged-in users ID.
     * @param suggestedUserId the suggested users ID.
     * @return true if the checks have passed. false if they fail.
     */
    public boolean validationCheck(Long loggedInUserId, Long suggestedUserId) {
        boolean requestedIdIsLoggedInUser = suggestedUserId.equals(loggedInUserId);
        if (requestedIdIsLoggedInUser) {
            logger.error("Suggested friend request was to logged in user's own ID. Doing nothing.");
            return false;
        }

        boolean alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, suggestedUserId) != null;
        if (alreadyFriends) {
            logger.error("Suggested friend and logged in user are already friends. Doing nothing");
            return false;
        }

        boolean alreadyDeclined = friendService.getDeclinedFriendship(loggedInUserId, suggestedUserId).isPresent();
        if (alreadyDeclined) {
            logger.error("Suggest friend and logged in user have a decline friendship. Doing nothing");
            return false;
        }
        return true;
    }

    /**
     * Checks if there is an existing friendship request between two users.
     * The requests can be either PENDING or DECLINED
     * @param senderId the request sender's ID
     * @param receiverId the request receiver's ID
     * @return true if request exists, false otherwise
     */
    public boolean friendRecordExists(Long senderId, Long receiverId) {
        List<Friends> sentRequests = friendService.getSentRequests(senderId);

        for (Friends sentRequest : sentRequests) {
            if (sentRequest.getReceiver().getId().equals(receiverId) && !sentRequest.getStatus().equals(ACCEPTED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check's for existing, pending received requests from the other user.
     * Will accept this request if it finds one. If none can be found, then
     * nothing happens and false returned.
     * @param loggedInUserId the receiver of the pending request
     * @param suggestedUserId the sender of the pending request
     * @return True if request accepted, otherwise false.
     */
    public boolean attemptToAcceptPendingRequest(Long loggedInUserId, Long suggestedUserId) {
        Optional<Friends> pendingRequest = friendService.getPendingFriendRequest(loggedInUserId, suggestedUserId);

        if (pendingRequest.isPresent()) {
            logger.info("Pending request found from ID: {} to ID: {} to accept", suggestedUserId, loggedInUserId);
            logger.info("Accepting request");
            pendingRequest.get().setStatus(ACCEPTED);
            friendService.save(pendingRequest.get());
            return true;
        }
        logger.info("No pending request from ID: {} to ID: {} to accept", suggestedUserId, loggedInUserId);
        return false;
    }

    /**
     * Check's for existing, pending received requests from the other user.
     * Will decline this request if it finds one. If none can be found, then
     * nothing happens and false returned.
     * @param loggedInUserId the sender of the pending request
     * @param suggestedUserId the receiver of the pending request
     * @return True if request declined, otherwise false.
     */
    public boolean attemptToDeclinePendingRequest(Long loggedInUserId, Long suggestedUserId) {
        Optional<Friends> pendingRequest = friendService.getPendingFriendRequest(loggedInUserId, suggestedUserId);

        if (pendingRequest.isPresent()) {
            logger.info("Pending request found from ID: {} to {} to decline", suggestedUserId, loggedInUserId);
            logger.info("Declining request");
            pendingRequest.get().setStatus(DECLINED);
            friendService.save(pendingRequest.get());
            return true;
        }
        logger.info("No pending request from ID: {} to {} to decline", suggestedUserId, loggedInUserId);
        return false;
    }

    /**
     * Sends a pending request to the suggested user if there is no pre-existing
     * pending or declined relationship.
     * @param loggedInUser the logged-in user
     * @param suggestedUser the suggested user that's displayed
     * @return true if the friendship was sent. false otherwise.
     */
    public boolean sendNewPendingRequest(GardenUser loggedInUser, GardenUser suggestedUser) {
        boolean friendRecordExists = friendRecordExists(loggedInUser.getId(), suggestedUser.getId());
        if (friendRecordExists) {
            logger.error("Friendship (pending or declined) between user's already exists, not sending a new request.");
            return false;
        }

        logger.info("Sending a pending request from ID: {} to ID: {}", loggedInUser.getId(), suggestedUser.getId());
        Friends pendingRequest = new Friends(loggedInUser, suggestedUser, PENDING);
        friendService.save(pendingRequest);
        return true;
    }

    /**
     * Creates a declined friendship between two users if there isn't already one existing.
     * @param loggedInUser the logged-in user
     * @param suggestedUser the suggested user that's displayed
     * @return true if the decline status is set. false otherwise.
     */
    public boolean setDeclinedFriendship(GardenUser loggedInUser, GardenUser suggestedUser) {
        boolean alreadyFriends = friendService.getAcceptedFriendship(loggedInUser.getId(), suggestedUser.getId()) != null;
        boolean friendRecordExists = friendRecordExists(loggedInUser.getId(), suggestedUser.getId());
        if (alreadyFriends || friendRecordExists) {
            logger.error("There is already an existing friendship betwen user's, can't set decline");
            return false;
        }

        logger.info("Creating a new declined request from ID: {} to ID: {}", loggedInUser.getId(), suggestedUser.getId());
        Friends declinedRequest = new Friends(loggedInUser, suggestedUser, DECLINED);
        friendService.save(declinedRequest);
        return true;
    }

    /**
     * Creates a DTO for a suggested user by calculating the compatibility
     *
     * @param self Logged in garden user
     * @param user Suggested garden user
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return DTO of suggested user
     */
    private SuggestedUserDTO makeSuggestedUserDTO(GardenUser self, GardenUser user, HttpServletRequest request,
                                                  HttpServletResponse response) {
        SuggestedUserDTO dto = new SuggestedUserDTO(user);

        double compatibility = compatibilityService.friendshipCompatibilityQuotient(self, user);
        dto.setCompatibility((int) Math.round(compatibility));

        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", user.getId());
        variables.put("birthFlower", user.getBirthFlower());
        variables.put("birthFlowerColor", birthFlowerService.getFlowerColor(user.getBirthFlower()));
        variables.put("favouriteGarden", user.getFavoriteGarden());
        variables.put("favouritePlants", user.getFavouritePlants());

        // Manually render thymeleaf fragments for the backside of each card
        JakartaServletWebApplication application = JakartaServletWebApplication
                .buildApplication(request.getServletContext());
        WebContext context = new WebContext(application.buildExchange(request, response), request.getLocale(),
                variables);
        if (user.getBirthFlower() != null) {
            dto.setBirthFlowerHtml(templateEngine.process("fragments/birth-flower.html", context));
        } else {
            dto.setBirthFlowerHtml("<div class=\"text-center my-3 text-white\">No Birth Flower Selected</div>");
        }
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

    /**
     * Gets all the suggested user DTOs, ordered as they should appear in the feed
     *
     * @param self Logged in garden user
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return list of all available connections
     */
    public List<SuggestedUserDTO> getSuggestionFeedContents(GardenUser self, HttpServletRequest request, HttpServletResponse response) {
        List<SuggestedUserDTO> receivedConnectionRequests = friendService.receivedConnectionRequests(self)
            .stream()
            .map(u -> makeSuggestedUserDTO(self, u, request, response))
            .sorted(Comparator.comparingInt(SuggestedUserDTO::getCompatibility).reversed())
            .toList();
        List<SuggestedUserDTO> availableConnections = friendService.availableConnections(self)
            .stream()
            .map(u -> makeSuggestedUserDTO(self, u, request, response))
            .sorted(Comparator.comparingInt(SuggestedUserDTO::getCompatibility).reversed())
            .toList();

        List<SuggestedUserDTO> feedContents = new ArrayList<>();
        feedContents.addAll(receivedConnectionRequests);
        feedContents.addAll(availableConnections);
        return feedContents;
    }
}
