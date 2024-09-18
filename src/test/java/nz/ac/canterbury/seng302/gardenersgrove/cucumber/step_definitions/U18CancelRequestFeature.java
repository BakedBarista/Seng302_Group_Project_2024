package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;
import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.PENDING;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;


import java.time.LocalDate;
import java.util.List;


import io.cucumber.java.en.And;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;





import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.BeforeAll;


public class U18CancelRequestFeature {

    private static FriendsRepository friendsRepository;

    private static GardenUserRepository gardenUserRepository;
    private static GardenUserService userService;
    private static  FriendService friendService;
    private GardenUser user;
    private static Long loggedInUserId;

    private static GardenUser friend = new GardenUser();


    @BeforeAll
    public static void beforeAll() {
        System.out.println("Before all");
        gardenUserRepository = mock(GardenUserRepository.class);
        friendsRepository = mock(FriendsRepository.class);
        userService = new GardenUserService(gardenUserRepository);
        friendService = new FriendService(friendsRepository);

         loggedInUserId = 1L;

        friend.setId(loggedInUserId);
        friend.setEmail("logged.in@gmail.com");
        friend.setFname("Current");
        friend.setFname("User");
    }

    @Given("I send a friend request to {string} {string}")
    public void i_send_a_friend_request_to(String fName, String lName) {
         user = U2LogInFeature.user;

        GardenUser friend = new GardenUser(fName, lName, "john.doe@gmail.com", "password", LocalDate.of(1999, 1, 1));

        if(user != null) {
            Friends friendRequest = new Friends(user, friend, PENDING, null);
            friendService.save(friendRequest);
        } else {
            throw new IllegalArgumentException("No user found with username");
        }
    }
    @When("I click on the cancel friend request button")
    public void i_click_on_the_cancel_request_button() {
        Friends friendship = friendService.getFriendship(user.getId(),friend.getId());
        friendService.removeFriendship(friendship);
    }
    @Then("I should not see {string} {string} in my friends list")
    public void i_should_not_see_in_my_friends_list(String fname, String lname) {
        List<GardenUser> friends = friendService.getAllFriends(user.getId());
        boolean isFriendPresent = friends.stream()
                .anyMatch(friend -> friend.getFname().equals(fname));
        assertFalse(isFriendPresent, "Friend should not be present in the list");
    }
    @And("I should not see {string} {string}  in my pending friend requests list")
    public void i_should_not_see_in_my_pending_friend_requests_list(String fname, String lname) {
        List<Friends> pendingRequests = friendService.getSentRequests(user.getId());
        boolean isFriendPresent = pendingRequests.stream()
                .anyMatch(friend -> friend.getReceiver().getFname().equals(fname));
        assertFalse(isFriendPresent, "Friend should not be present in the list");
    }

}
