package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
                .addUser(new GardenUser("fname", "lname", "email", "password", "dob"));
        Assertions.assertEquals(gardenUser.getFname(), "fname");
        Assertions.assertEquals(gardenUser.getLname(), "lname");
        Assertions.assertEquals(gardenUser.getEmail(), "email");
        Assertions.assertEquals(gardenUser.getDOB(), "dob");
    }

    /**
     * Check that we can check a user's password
     */
    @Test
    public void checkPassword() {
        GardenUser gardenUser = userService
                .addUser(new GardenUser("fname", "lname", "email", "password", "dob"));
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
        GardenUser user = new GardenUser("John", "Doe", email, password, null);
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
        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", "dob");
        userService.addUser(gardenUser);

        userService.setProfilePicture(gardenUser.getId(), contentType, bytes);

        GardenUser gardenUser2 = userService.getUserById(gardenUser.getId());
        Assertions.assertEquals(gardenUser2.getProfilePictureContentType(), contentType);
        Assertions.assertEquals(gardenUser2.getProfilePicture(), bytes);
    }

    @Test
    public void getCurrentUser() {
        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", "dob");
        userService.addUser(gardenUser);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(gardenUser, null, gardenUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof GardenUser) {
            GardenUser currentUser = (GardenUser) auth.getPrincipal();
            Assertions.assertEquals(gardenUser, currentUser);

        } else {
            throw new IllegalStateException("Authentication principal is not an instance of GardenUser");
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenTokenNotExpired_whenGetUserByResetPasswordTokenCalled_thenReturnsUser() {
        String token = "abc123xyz";

        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", "dob");
        gardenUser.setResetPasswordToken(token);
        userService.addUser(gardenUser);

        GardenUser user = userService.getUserByResetPasswordToken(token);

        Assertions.assertEquals(gardenUser, user);
    }

    @Test
    void givenTokenExpired_whenGetUserByResetPasswordTokenCalled_thenReturnsNull() {
        String token = "abc123xyz";

        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", "dob");
        userService.addUser(gardenUser);

        GardenUser user = userService.getUserByResetPasswordToken(token);

        Assertions.assertNull(user);
    }
}

