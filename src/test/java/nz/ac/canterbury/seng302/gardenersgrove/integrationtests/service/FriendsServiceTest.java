package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.ignoreStubs;

@DataJpaTest
@Import(FriendService.class)
public class FriendsServiceTest {
    @Autowired
    private FriendService friendService;

    @Autowired
    private GardenUserRepository gardenUserRepository;


    private GardenUser testUser1;

    private GardenUser testUser2;

    private GardenUser testUser3;

    private GardenUser testUser4;

    private GardenUser testUser5;



    @BeforeEach
    public void setUp() {
        testUser1 = new GardenUser("John", "Doe", "jdo123@uclive.ac.nz", "password",null);
        testUser2 = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password",null);
        testUser3 = new GardenUser("Jame", "Doe", "jdo457@uclive.ac.nz", "password",null);
        testUser4 = new GardenUser("James", "Doe", "jdo458@uclive.ac.nz", "password",null);
        testUser5 = new GardenUser("Jamess", "Doe", "jdo459@uclive.ac.nz", "password",null);

        testUser1 = gardenUserRepository.save(testUser1);
        testUser2 = gardenUserRepository.save(testUser2);
        testUser3 = gardenUserRepository.save(testUser3);
        testUser4 = gardenUserRepository.save(testUser4);
        testUser5 = gardenUserRepository.save(testUser5);
        
        Friends relationShip1 = new Friends(testUser1, testUser2, ACCEPTED);
        Friends relationShip2 = new Friends(testUser1, testUser3, ACCEPTED);

        friendService.save(relationShip1);
        friendService.save(relationShip2);


    }

    @Test
    public void whenGetFriendsCalled_thenReturnsFriends() {
        var friends = List.of(testUser2, testUser3);
        var users = friendService.getAllFriends(testUser1.getId());
        assertEquals(friends, users);
    }

    @Test
    public void whenGetFriendCalled_thenReturnsFriend() {
        Friends relationShip3 = new Friends(testUser2, testUser3, PENDING);
        friendService.save(relationShip3);
        var request = friendService.getFriendship(testUser2.getId(), testUser3.getId());
        assertEquals(request, relationShip3);
    }

    @Test
    public void whenGetFriendCalledEmptyDatabse_thenReturnsEmpty() {
        var request = friendService.getFriendship(testUser2.getId(), testUser3.getId());
        assertNull(request);
    }

    @Test
    public void whenGetFriendCalledInvaildId_thenReturnsEmpty() {
        Friends relationShip3 = new Friends(testUser2, testUser4, PENDING);
        friendService.save(relationShip3);
        long invaildId = -1;
        var request = friendService.getFriendship(testUser2.getId(), invaildId);
        assertNull(request);
    }
    
    @Test
    public void whenGetFriendSentCalled_thenReturnsSentRequests() {
        Friends relationShip3 = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationShip3);
        var request = friendService.getSentRequests(testUser3.getId());
        assertEquals(relationShip3, request.get(0));
    }

    @Test
    public void whenGetFriendSentCalledNull_thenReturnsNull() {
        Friends relationShip3 = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationShip3);
        var request = friendService.getSentRequests(testUser4.getId());
        assertTrue(request.isEmpty());
    }

    @Test
    public void whenGetFriendRecivedCalled_thenReturnsRecivedRequests() {
        Friends relationShip3 = new Friends(testUser1, testUser5, PENDING);
        friendService.save(relationShip3);
        var request = friendService.getReceivedRequests(testUser5.getId());
        assertEquals(relationShip3, request.get(0));
    }

    @Test
    public void whenGetFriendRecivedCalledNull_thenReturnsNull() {
        Friends relationShip3 = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationShip3);
        var request = friendService.getReceivedRequests(testUser3.getId());
        assertTrue(request.isEmpty());
    }

    /**
     * Test to check if a friend is removed from the database
     */
    @Test
    public void whenRemovedFriend_thenFriendRemoved() {
        Friends relationShip3 = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationShip3);
        friendService.removeFriend(testUser3.getId(), testUser4.getId());
        var request = friendService.getFriendship(testUser3.getId(), testUser4.getId());
        assertNull(request);
    }

    /**
     * Test to check if a friend is added to the database
     */
    @Test
    void whenInviteAccepted_thenFriendAdded() {
        Friends relationship = new Friends(testUser3, testUser4, ACCEPTED);
        friendService.save(relationship);
        var request = friendService.getAcceptedFriendship(testUser3.getId(), testUser4.getId());
        assertNotNull(request);
        assertEquals(relationship.getFriend_id(), request.getFriend_id());
    }

    /**
     * Test to check if a friend is added to the database
     */
    @Test
    void whenGetSentFriendship_thenFriendshipReturned() {
        Friends relationship = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationship);
        Friends friendship = friendService.getSent(testUser3.getId(), testUser4.getId());
        assertEquals(relationship, friendship);
    }

    /**
     * Test to check if a friend is added to the database and then deleted
     */
    @Test
     void whenFriendshipCreated_andFriendshipIsDeleted_thenFriendshipDoesntExist() {
        Friends relationship = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationship);
        friendService.delete(relationship);
        assertNull(friendService.getSent(testUser3.getId(), testUser4.getId()));
    }

    /**
     * Test to check if a friend is added to the database and then removed
     */
    @Test
     void whenFriendshipCreated_andFriendIsRemoved_thenFriendIsNoLongerAFriend() {
        Friends relationship = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationship);
        friendService.removeFriendship(relationship);
        assertNull(friendService.getSent(testUser3.getId(), testUser4.getId()));
    }

    /**
     * Test to check if a friendship is declined an the result is correctly stored
     */
    @Test
     void whenRequestIsSent_andRecipientDeclines_thenDatabaseIsUpdated() {
        Friends relationship = new Friends(testUser3, testUser4, DECLINED);
        friendService.save(relationship);
        List<Friends> sentRequestsDeclined = friendService.getSentRequestsDeclined(testUser3.getId());
        assertEquals(relationship, sentRequestsDeclined.get(0));
    }

    @Test
    void whenPendingRequestExists_thenFriendEntityReturned() {
        Friends relationship = new Friends(testUser3, testUser4, PENDING);
        friendService.save(relationship);

        Optional<Friends> pendingRequest = friendService.getPendingFriendRequest(testUser4.getId(), testUser3.getId());
        assertTrue(pendingRequest.isPresent());
        assertEquals(relationship, pendingRequest.get());
    }

    @Test
    void whenNoRequestExists_thenEmptyOptionalReturned() {
        Optional<Friends> pendingRequest = friendService.getPendingFriendRequest(testUser4.getId(), testUser3.getId());
        assertTrue(pendingRequest.isEmpty());
    }

    @Test
    void whenFriendRequestRecieved_thenIsShownAtTopOfFeed() {
        Friends relationship = new Friends(testUser1, testUser2, PENDING);
        friendService.save(relationship);
        
        var requests = friendService.receivedConnectionRequests(testUser2);

        assertEquals(testUser1, requests.get(0));
    }

    @Test
    void whenConnectionsAreAvailable_thenTheyAreShownInFeed() {
        var feed = friendService.availableConnections(testUser1);

        assertFalse(feed.isEmpty());
        assertTrue(feed.contains(testUser4));
        assertTrue(feed.contains(testUser5));
    }

    @Test
    void whenUsersAreAlreadyFriends_thenTheyAreNotShownInEachOthersFeeds() {
        var feed1 = friendService.availableConnections(testUser1);
        var feed2 = friendService.availableConnections(testUser2);

        assertFalse(feed1.contains(testUser2));
        assertFalse(feed1.contains(testUser3));

        assertFalse(feed2.contains(testUser1));
    }

    @Test
    void whenConnectionsAreAvailable_thenUserIsNotRecommendedThemselves() {
        var feed = friendService.availableConnections(testUser1);

        assertFalse(feed.contains(testUser1));
    }

    @Test
    void whenReceiverNotEqualUser_thenAddReceiverToPending() {
        List<Friends> friends = new ArrayList<>();
        Friends friendship = new Friends(testUser1, testUser2, PENDING);
        friends.add(friendship);

        var pendingList = friendService.getPendingRequestGardenUser(friends, testUser2.getId());
        assertEquals(1, pendingList.size());
        assertTrue(!pendingList.contains(testUser2));

    }


}











