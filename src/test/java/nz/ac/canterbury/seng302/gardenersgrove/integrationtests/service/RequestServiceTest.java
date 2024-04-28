package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;

@DataJpaTest
@Import(RequestService.class)
public class RequestServiceTest {
    
    @Autowired
    private RequestService requestService;

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
   
    }
    
    @Test
    public void whenGetRequestSentCalled_thenReturnsSentReqeusts() {
        Requests request1 = new Requests(testUser1, testUser2, "pending");
        Requests request2 =  new Requests(testUser1, testUser3, "pending");

        requestService.save(request1);
        requestService.save(request2);
        var friends = List.of(request1, request2);
        var requests = requestService.getSentRequests(testUser1.getId());
        assertEquals(friends, requests);
    }

    @Test
    public void whenGetRequestRecivedCalled_thenReturnsRecivedReqeusts() {
        Requests request1 = new Requests(testUser2, testUser3, "pending");
        Requests request2 =  new Requests(testUser4, testUser3, "pending");

        requestService.save(request1);
        requestService.save(request2);
        var friends = List.of(request1, request2);
        var requests = requestService.getReceivedRequests(testUser3.getId());
        assertEquals(friends, requests);
    }

    @Test
    public void whenGetRequestCheckCalled_thenReturnsCheckReqeusts() {
        Requests request1 = new Requests(testUser1, testUser4, "pending");

        requestService.save(request1);
        var requests = requestService.getRequest(testUser4.getId(), testUser1.getId());
        assertEquals(request1, requests.get());
    }

}
