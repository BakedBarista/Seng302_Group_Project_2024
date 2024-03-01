package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(GardenUserService.class)
public class UserServiceTest {

    @Autowired
    private GardenUserService userService;

    /**
     * Simple test to check if inserting information into the database works
     */
    @Test
    public void insertingUser() {
        GardenUser gardenUser = userService
                .addUser(new GardenUser("fname", "lname", "email", "address", "password", "dob"));
        Assertions.assertEquals(gardenUser.getFname(), "fname");
        Assertions.assertEquals(gardenUser.getLname(), "lname");
        Assertions.assertEquals(gardenUser.getEmail(), "email");
        Assertions.assertEquals(gardenUser.getDOB(), "dob");
        Assertions.assertEquals(gardenUser.getAddress(), "address");
    }

    /**
     * Check that we can check a user's password
     */
    @Test
    public void checkPassword() {
        GardenUser gardenUser = userService
                .addUser(new GardenUser("fname", "lname", "email", "address", "password", "dob"));
        Assertions.assertTrue(gardenUser.checkPassword("password"));
        Assertions.assertFalse(gardenUser.checkPassword("incorrect password"));
    }
}
