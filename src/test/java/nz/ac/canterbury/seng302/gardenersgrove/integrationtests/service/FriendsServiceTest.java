package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

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



    @BeforeEach
    public void setUp() {
        testUser1 = new GardenUser("John", "Doe", "jdo123@uclive.ac.nz", "password",null);
        testUser2 = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password",null);
        testUser3 = new GardenUser("Jame", "Doe", "jdo457@uclive.ac.nz", "password",null);
        testUser4 = new GardenUser("James", "Doe", "jdo458@uclive.ac.nz", "password",null);

        testUser1 = gardenUserRepository.save(testUser1);
        testUser2 = gardenUserRepository.save(testUser2);
        testUser3 = gardenUserRepository.save(testUser3);
        testUser3 = gardenUserRepository.save(testUser4);
        
        Friends relationShip1 = new Friends(testUser1, testUser2, "pending");
        Friends relationShip2 = new Friends(testUser1, testUser3, "pending");

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
    public void whenGetRequestCalled_thenReturnsRequest() {
        Friends relationShip3 = new Friends(testUser2, testUser3, "pending");
        friendService.save(relationShip3);
        var request = friendService.getFriendship(testUser2.getId(), testUser3.getId());
        assertEquals(request, relationShip3);
    }

    @Test
    public void whenGetRequestCalledEmptyDatabse_thenReturnsEmpty() {
        var request = friendService.getFriendship(testUser2.getId(), testUser3.getId());
        assertNull(request);
    }

    @Test
    public void whenGetRequestCalledInvaildId_thenReturnsEmpty() {
        Friends relationShip3 = new Friends(testUser2, testUser4, "pending");
        friendService.save(relationShip3);
        long invaildId = -1;
        var request = friendService.getFriendship(testUser2.getId(), invaildId);
        assertNull(request);
    }

}
