package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

@DataJpaTest
@Import({ GardenUserService.class, BirthFlowerService.class, ObjectMapper.class })
public class UserServiceTest {

    @Autowired
    private GardenUserService userService;

    /**
     * Simple test to check if inserting information into the database works
     */
    @Test
    public void insertingUser() {
        GardenUser gardenUser = userService
                .addUser(new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1)));
        Assertions.assertEquals(gardenUser.getFname(), "fname");
        Assertions.assertEquals(gardenUser.getLname(), "lname");
        Assertions.assertEquals(gardenUser.getEmail(), "email");
        Assertions.assertEquals(gardenUser.getDateOfBirth(), LocalDate.of(1970, 1, 1));
    }

    /**
     * Check that we can check a user's password
     */
    @Test
    public void checkPassword() {
        GardenUser gardenUser = userService
                .addUser(new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1)));
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
        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1));
        userService.addUser(gardenUser);

        userService.setProfilePicture(gardenUser.getId(), contentType, bytes);

        GardenUser gardenUser2 = userService.getUserById(gardenUser.getId());
        Assertions.assertEquals(gardenUser2.getProfilePictureContentType(), contentType);
        Assertions.assertEquals(gardenUser2.getProfilePicture(), bytes);
    }

    @Test
    public void getCurrentUser() {
        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1));
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

        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1));
        gardenUser.setResetPasswordToken(token);
        userService.addUser(gardenUser);

        GardenUser user = userService.getUserByResetPasswordToken(token);

        Assertions.assertEquals(gardenUser, user);
    }

    @Test
    void givenTokenExpired_whenGetUserByResetPasswordTokenCalled_thenReturnsNull() {
        String token = "abc123xyz";

        GardenUser gardenUser = new GardenUser("fname", "lname", "email", "password", LocalDate.of(1970, 1, 1));
        userService.addUser(gardenUser);

        GardenUser user = userService.getUserByResetPasswordToken(token);

        Assertions.assertNull(user);
    }

    @Test
    void whenAddUserWithDOB_thenBirthFlowerSet() {
        GardenUser user = new GardenUser("John", "Doe", "john.doe.128@gmail.com", "", LocalDate.of(2004, 12, 10));

        userService.addUser(user);

        assertEquals(LocalDate.of(2004, 12, 10), user.getDateOfBirth());
        assertEquals("Holly", user.getBirthFlower());
    }

    @Test
    void whenAddUserWithoutDOB_thenBirthFlowerIsNull() {
        GardenUser user = new GardenUser("John", "Doe", "john.doe.140@gmail.com", "", null);

        userService.addUser(user);

        assertNull(user.getDateOfBirth());
        assertNull(user.getBirthFlower());
    }
}

