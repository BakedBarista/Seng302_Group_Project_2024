package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for GardenUser, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see
 * the @link{Autowired} annotation below
 */
@Service
public class GardenUserService {
    private final GardenUserRepository gardenUserRepository;

    @Autowired
    public GardenUserService(GardenUserRepository gardenUserRepository) {
        this.gardenUserRepository = gardenUserRepository;
    }

    /**
     * Gets all GardenUser from persistence
     * 
     * @return all GardenUser currently saved in persistence
     */
    public List<GardenUser> getUser() {
        return gardenUserRepository.findAll();
    }

    /**
     * Adds a gardenUser to persistence
     * 
     * @param gardenUser object to persist
     * @return the saved gardenUser object
     */
    public GardenUser addUser(GardenUser gardenUser) {
        return gardenUserRepository.save(gardenUser);
    }

    /**
     * Gets a single GardenUser by their email
     *
     * @param email The user's email
     * @return The user with the given email, or null if no such user exists
     */
    public GardenUser getUserByEmail(String email) {
        var user = gardenUserRepository.findByEmail(email);
        return user.orElse(null);
    }

    /**
     * Retrieves a list of GardenUser entities based on a search query that includes both first name and last name.
     * If only one name is provided, it searches by the first name only. If more than two names are provided,
     * it returns an empty list
     *
     * @param name           first name and last name separated by a space
     * @param currentUserId  the ID of the current user
     * @return a list of GardenUser entities
     */
    public List<GardenUser> getUserBySearch(String name, Long currentUserId) {
        String[] names = name.split(" ");
        String first = names[0];

        List<GardenUser> empty = new ArrayList<GardenUser>();

        if(names.length == 1){
            return gardenUserRepository.findBySearchNoLname(first, currentUserId);
        }

        String last = names[1];

        if(names.length > 2){
            return empty;
        }

        return gardenUserRepository.findBySearch(first, last, currentUserId);
    }

    /**
     * Checks if the search query matches the current user's name. The search query should contain the
     * first name and last name separated by a space. If only one name is provided, it searches by
     * the first name only. If more than two names are provided, it returns an empty optional
     *
     * @param name           first name and last name separated by a space
     * @param currentUserId  current user
     * @return an optional containing the GardenUser entity
     */
    public Optional<GardenUser> checkSearchMyself(String name, Long currentUserId) {
        String[] names = name.split(" ");
        String first = names[0];

        if(names.length == 1){
            return gardenUserRepository.findBySearchMeNoLname(first, currentUserId);
        }

        String last = names[1];
        if(names.length > 2){
            return Optional.empty();
        }

        return gardenUserRepository.findBySearchMe(first, last, currentUserId);
    }

    /**
     * Gets a single GardenUser by their email and password
     * 
     * @param email    The user's email
     * @param password The user's password
     * @return The user with the given email and password, or null if no such user
     *         exists
     */
    public GardenUser getUserByEmailAndPassword(String email, String password) {
        var user = gardenUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return null;
        }

        if (user.get().checkPassword(password)) {
            return user.get();
        }
        return null;
    }

    /**
     * Gets a single GardenUser by their email
     * 
     * @param id The user's email
     * @return The user with the given id, or null if no such user exists
     */
    public GardenUser getUserById(long id) {
        var user = gardenUserRepository.findById(id);
        return user.orElse(null);

    }

    /**
     * Sets the profile picture of the user with the given ID.
     *
     * @param id ID of the user whose profile picture is to be set
     * @param contentType contentType The content type of the profile picture
     * @param profilePicture rofilePicture The byte array representing the profile picture
     */
    public void setProfilePicture(long id, String contentType, byte[] profilePicture) {
        var user = gardenUserRepository.findById(id);
        if (user.isEmpty()) {
            return;
        }

        user.get().setProfilePicture(contentType, profilePicture);
        gardenUserRepository.save(user.get());
    }

    /**
     * Gets the currently authenticated user
     *
     * @return The currently authenticated user
     */
    public GardenUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }
        long userId = Long.parseLong(authentication.getName());
        return getUserById(userId);
    }

    /**
     * Gets a single GardenUser by their reset password token
     * 
     * @param token The reset password token to search for
     * @return The user with the given reset password token, or null if no such user
     */
    public GardenUser getUserByResetPasswordToken(String token) {
        if (token == null) {
            return null;
        }
        var user = gardenUserRepository.findByResetPasswordToken(token);
        return user.orElse(null);
    }

}
