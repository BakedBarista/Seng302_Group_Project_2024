package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GardenFormResult repository accessor
 */

@Repository
public interface FriendsRepository extends CrudRepository<Friends, Long> {
    @Query("SELECT u.user1 FROM Friends u WHERE u.user2 = ?1")
    List<GardenUser> getAllFriends(long id);
    
}
