package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;

public class SuggestedUserServiceTest {

    private SuggestedUserService suggestedUserService;
    private FriendService friendService;

    Long loggedInUserId = 1L;
    GardenUser loggedInUser;
    Long receiverId = 2L;
    GardenUser receiver;

    @BeforeEach
    public void setUp() {
        friendService = Mockito.mock(FriendService.class);
        suggestedUserService = new SuggestedUserService(friendService);

        loggedInUser = new GardenUser();
        receiver = new GardenUser();

        loggedInUser.setId(loggedInUserId);
        receiver.setId(receiverId);
    }

    @Test
    void whenPendingRecordExists_thenReturnTrue() {
        Friends friendRecord = new Friends(loggedInUser, receiver, PENDING);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, receiverId);

        Assertions.assertTrue(result);
    }

    @Test
    void whenDeclinedRecordExists_thenReturnTrue() {
        Friends friendRecord = new Friends(loggedInUser, receiver, DECLINED);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, receiverId);

        Assertions.assertTrue(result);
    }

    @Test
    void whenNoRecordExists_thenReturnFalse() {
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(Collections.emptyList());

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, receiverId);

        Assertions.assertFalse(result);
    }

    @Test
    void whenAcceptedRecordExists_thenReturnFalse() {
        Friends friendRecord = new Friends(loggedInUser, receiver, ACCEPTED);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, receiverId);

        Assertions.assertFalse(result);
    }
}
