package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

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

    /**
     * Check that we can retrieve a user using and email and password
     */
    @Test
    public void getUserByEmailAndPassword() {
        String email = "jdo123@uclive.ac.nz";
        String password = "P@ssw0rd!";
        GardenUser user = new GardenUser("John", "Doe", email, "address", password, null);
        userService.addUser(user);

        Assertions.assertNotNull(userService.getUserByEmailAndPassword(email, password));
        Assertions.assertNull(userService.getUserByEmailAndPassword(email, "invalid password"));
        Assertions.assertNull(userService.getUserByEmailAndPassword("invalid email", password));
    }

    /**
     * Set user profile picture
     */
    @Test
    public void setUserProfilePicture() {
        byte[] bytes = "test".getBytes();
        String contentType = "image/png";
        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "address", "password", "dob");
        userService.addUser(gardenUser);

        userService.setProfilePicture(gardenUser.getId(), contentType, bytes);

        GardenUser gardenUser2 = userService.getUserById(gardenUser.getId());
        Assertions.assertEquals(gardenUser2.getProfilePictureContentType(), contentType);
        Assertions.assertEquals(gardenUser2.getProfilePicture(), bytes);
    }
}
