package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.util.List;


import io.cucumber.java.en.And;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;



import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class U18CancelRequestFeature {
    private GardenUserService userService;
    private FriendService friendService;

    private GardenUser currentUser;
    private GardenUser friend;

    @Given("I send a friend request to {string} {string}")
    public void i_send_a_friend_request_to(String fName, String lName) {
        GardenUser currentUser = userService.getCurrentUser();
        GardenUser friend = new GardenUser(fName, lName, "john.doe@gmail.com", "password", "");

        if(currentUser != null) {
            Friends friendRequest = new Friends(currentUser, friend, "pending");
            friendService.save(friendRequest);
        } else {
            throw new IllegalArgumentException("No user found with username");
        }
    }
    @When("I click on the cancel friend request button")
    public void i_click_on_the_cancel_request_button() {
        Friends friendship = friendService.getFriendship(currentUser.getId(),friend.getId());
        friendService.removeFriendship(friendship);
    }
    @Then("I should not see {string} {string} in my friends list")
    public void i_should_not_see_in_my_friends_list(String fname, String lname) {
        List<GardenUser> friends = friendService.getAllFriends(currentUser.getId());
        boolean isFriendPresent = friends.stream()
                .anyMatch(friend -> friend.getFname().equals(fname));
        assertFalse(isFriendPresent, "Friend should not be present in the list");
    }
    @And("I should not see {string} {string} in my pending friend requests list")
    public void i_should_not_see_in_my_pending_friend_requests_list(String fname, String lname) {
        List<Friends> pendingRequests = friendService.getPendingRequests(currentUser.getId());
        boolean isFriendPresent = pendingRequests.stream()
                .anyMatch(friend -> friend.getReceiver().getFname().equals(fname));
        assertFalse(isFriendPresent, "Friend should not be present in the list");
    }




}
