package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(GardenUserService.class)
public class UserServiceTest {

//    @Autowired
//    private UserService userService;

    @Autowired
    private GardenUserRepository gardenUserRepository;

    @Test
    public void simpleTest() {
        GardenUserService gardenUserService = new GardenUserService(gardenUserRepository);
        GardenUser gardenUser = gardenUserService.addUser(new GardenUser("gre", "gret"));
    }
}
