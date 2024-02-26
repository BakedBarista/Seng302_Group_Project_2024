package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.User;
import nz.ac.canterbury.seng302.gardenersgrove.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for User, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see the @link{Autowired} annotation below
 */
@Service
public class UserService {
    private UserRepository userRepository;

//    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Gets all FormResults from persistence
     * @return all FormResults currently saved in persistence
     */
    public List<User> getUser() {
        return userRepository.findAll();
    }

    /**
     * Adds a formResult to persistence
     * @param user object to persist
     * @return the saved formResult object
     */
    public User addUser(User user) {
        return userRepository.save(user);
    }
}
