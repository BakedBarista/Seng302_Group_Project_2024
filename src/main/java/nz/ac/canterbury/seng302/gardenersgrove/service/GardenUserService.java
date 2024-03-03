package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for GardenUser, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see
 * the @link{Autowired} annotation below
 */
@Service
public class GardenUserService {
    private GardenUserRepository gardenUserRepository;

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
     * @param GardenUser object to persist
     * @return the saved gardenUser object
     */
    public GardenUser addUser(GardenUser gardenUser) {
        return gardenUserRepository.save(gardenUser);
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
     * @param email The user's email
     * @return The user with the given email, or null if no such user exists
     */
    public GardenUser getUserByEmail(String email) {
        var user = gardenUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return null;
        }

        return user.get();
    }

}
