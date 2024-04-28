package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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



}
