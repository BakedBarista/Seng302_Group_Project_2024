package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for GardenUser, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see
 * the @link{Autowired} annotation below
 */
@Service
public class GardenUserService {
    private final GardenUserRepository gardenUserRepository;
    private final BirthFlowerService birthFlowerService;

    @Autowired
    public GardenUserService(GardenUserRepository gardenUserRepository, BirthFlowerService birthFlowerService) {
        this.gardenUserRepository = gardenUserRepository;
        this.birthFlowerService = birthFlowerService;
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
        if (gardenUser.getId() == null && gardenUserRepository.findByEmail(gardenUser.getEmail()).isPresent()) {
            throw new IllegalStateException("User with email " + gardenUser.getEmail() + " already exists");
        }
        if (gardenUser.getBirthFlower() == null && gardenUser.getDateOfBirth() != null) {
            String birthFlower = birthFlowerService.getDefaultBirthFlower(gardenUser.getDateOfBirth());
            gardenUser.setBirthFlower(birthFlower);
        }

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
     * Sets the profile banner of the user with the given ID.
     *
     * @param id ID of the user whose profile banner is to be set
     * @param contentType contentType The content type of the profile banner
     * @param profileBanner profileBanner The byte array representing the profile banner
     */
    public void setProfileBanner(long id, String contentType, byte[] profileBanner) {
        var user = gardenUserRepository.findById(id);
        if (user.isEmpty()) {
            return;
        }

        user.get().setProfileBanner(contentType, profileBanner);
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
        String identifier = authentication.getName();
        try {
            long userId = Long.parseLong(identifier);
            return getUserById(userId);
        } catch (NumberFormatException e) {
            return getUserByEmail(identifier);
        }
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

    /**
     * Obfuscates an email address by base64 encoding it
     *
     * @param email The email address to obfuscate
     * @return The email encoded in base64
     */
    public String obfuscateEmail(String email) {
        byte[] bytes = email.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Deobfuscates an email address by base64 decoding it
     *
     * @param obfuscatedEmail The obfuscated email address
     * @return The email decoded from base64
     */
    public String deobfuscateEmail(String obfuscatedEmail) {
        byte[] bytes = Base64.getDecoder().decode(obfuscatedEmail);
        return new String(bytes);
    }

    /**
     * Get the favourite garden of the user
     * @param userId user id
     */
    public Garden getFavoriteGarden(Long userId) {
        Optional<GardenUser> user = gardenUserRepository.findById(userId);
        return user.map(GardenUser::getFavoriteGarden).orElse(null);
    }

    /**
     * Get the favourite plants of the user
     * @param userId user id
     * @return set of favourite plants
     */
    public Set<Plant> getFavoritePlants(Long userId) {
        GardenUser user = gardenUserRepository.findById(userId)
                .orElseThrow();

        return user.getFavouritePlants();
    }

    /**
     * Update the favourite plant
     * @param userId user id
     * @param plants set of plants
     */
    public void updateFavouritePlant(Long userId, Set<Plant> plants) {
        GardenUser user = getUserById(userId);
        if (plants.size() <= 3) {
            user.setFavouritePlants(plants);
            gardenUserRepository.save(user);
        } else {
            throw new IllegalArgumentException("Cannot have more than 3 favourite plants");
        }
    }

    /**
     * Removes the favourite plant of a user - this is specified by the ID of the plant itself
     * rather than an index within the list of favourite plants for a user.
     * @param userId the id of the user who has the favourite plant
     * @param plantId the id of the plant that the user favourites
     * @return true if the plant was successfully removed from the favourite plant list.
     */
    public Boolean removeFavouritePlant(Long userId, Long plantId) {
        GardenUser user = gardenUserRepository.findById(userId).orElseThrow();
        boolean match = user.getFavouritePlants().stream().anyMatch(plant -> plant.getId().equals(plantId));
        if (match) {
            user.removeFavouritePlant(plantId);
            addUser(user);
        }
        return match;
    }
}
