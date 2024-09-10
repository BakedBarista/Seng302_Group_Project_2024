package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class SuggestedUserServiceTest {
    @Autowired
    private FriendService friendService;

    @Autowired
    private GardenUserRepository gardenUserRepository;

    @Autowired
    private SuggestedUserService suggestedUserService;

    private static GardenUser loggedInUser;
    private static GardenUser suggestedUser;

    @BeforeAll
    static void setUp(@Autowired GardenUserRepository gardenUserRepository) {
        loggedInUser = gardenUserRepository.save(new GardenUser("John", "Doe", "jdo123567@uclive.ac.nz", "password", null));
        suggestedUser = gardenUserRepository.save(new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password", null));
    }

    @Test
    void whenSuggestedUserHasSentRequest_andLoggedInUserAccepts_thenAcceptRequest() {
        friendService.save(new Friends(suggestedUser, loggedInUser, Friends.Status.PENDING));

        boolean accepted = suggestedUserService.attemptToAcceptPendingRequest(loggedInUser.getId(), suggestedUser.getId());
        List<GardenUser> loggedInUserFriends = friendService.getAllFriends(loggedInUser.getId());
        Optional<Friends> pendingRequestFromSuggested = friendService.getPendingFriendRequest(suggestedUser.getId(), loggedInUser.getId());

        Assertions.assertTrue(accepted);
        Assertions.assertEquals(suggestedUser.getId(), loggedInUserFriends.get(0).getId());
        Assertions.assertTrue(pendingRequestFromSuggested.isEmpty());
    }

    @Test
    void whenSuggestedUserIsNotFriend_andLoggedInUserAccepts_thenSendRequest() {
        boolean requestSent = suggestedUserService.sendNewPendingRequest(loggedInUser, suggestedUser);
        Optional<Friends> pendingRequests = friendService.getPendingFriendRequest(suggestedUser.getId(), loggedInUser.getId());

        Assertions.assertTrue(requestSent);
        Assertions.assertTrue(pendingRequests.isPresent());
        Assertions.assertEquals(Friends.Status.PENDING, pendingRequests.get().getStatus());
    }
}
