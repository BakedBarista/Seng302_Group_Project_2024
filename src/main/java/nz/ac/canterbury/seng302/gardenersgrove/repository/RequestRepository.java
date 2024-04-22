package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * GardenFormResult repository accessor
*/

@Repository
public interface RequestRepository extends CrudRepository<Requests, Long> {
    @Query("SELECT u.user1 FROM Friends u WHERE u.user2.id = ?1")
    List<GardenUser> getAllFriends(Long user);
}
