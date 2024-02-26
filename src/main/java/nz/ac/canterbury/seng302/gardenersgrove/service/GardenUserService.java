package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for User, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see the @link{Autowired} annotation below
 */
@Service
public class GardenUserService {
    private GardenUserRepository gardenUserRepository;

//    @Autowired
    public GardenUserService(GardenUserRepository gardenUserRepository) {
        this.gardenUserRepository = gardenUserRepository;
    }
    /**
     * Gets all FormResults from persistence
     * @return all FormResults currently saved in persistence
     */
    public List<GardenUser> getUser() {
        return gardenUserRepository.findAll();
    }

    /**
     * Adds a formResult to persistence
     * @param user object to persist
     * @return the saved formResult object
     */
    public GardenUser addUser(GardenUser gardenUser) {
        return gardenUserRepository.save(gardenUser);
    }
}
