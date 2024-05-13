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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        
        Friends relationShip1 = new Friends(testUser1, testUser2, "accepted");
        Friends relationShip2 = new Friends(testUser1, testUser3, "accepted");

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
        Friends relationShip3 = new Friends(testUser2, testUser3, "Pending");
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
        Friends relationShip3 = new Friends(testUser2, testUser4, "Pending");
        friendService.save(relationShip3);
        long invaildId = -1;
        var request = friendService.getFriendship(testUser2.getId(), invaildId);
        assertNull(request);
    }
    
    @Test
    public void whenGetFriendSentCalled_thenReturnsSentRequests() {
        Friends relationShip3 = new Friends(testUser3, testUser4, "Pending");
        friendService.save(relationShip3);
        var request = friendService.getSentRequests(testUser3.getId());
        assertEquals(relationShip3, request.get(0));
    }

    @Test
    public void whenGetFriendSentCalledNull_thenReturnsNull() {
        Friends relationShip3 = new Friends(testUser3, testUser4, "Pending");
        friendService.save(relationShip3);
        var request = friendService.getSentRequests(testUser4.getId());
        assertTrue(request.isEmpty());
    }

    @Test
    public void whenGetFriendRecivedCalled_thenReturnsRecivedRequests() {
        Friends relationShip3 = new Friends(testUser1, testUser5, "Pending");
        friendService.save(relationShip3);
        var request = friendService.getReceivedRequests(testUser5.getId());
        assertEquals(relationShip3, request.get(0));
    }

    @Test
    public void whenGetFriendRecivedCalledNull_thenReturnsNull() {
        Friends relationShip3 = new Friends(testUser3, testUser4, "Pending");
        friendService.save(relationShip3);
        var request = friendService.getReceivedRequests(testUser3.getId());
        assertTrue(request.isEmpty());
    }

    @Test
    public void whenRemovedFriend_thenFriendRemoved() {
        Friends relationShip3 = new Friends(testUser3, testUser4, "Pending");
        friendService.save(relationShip3);
        friendService.removeFriend(testUser3.getId(), testUser4.getId());
        var request = friendService.getFriendship(testUser3.getId(), testUser4.getId());
        assertNull(request);
    }
}
