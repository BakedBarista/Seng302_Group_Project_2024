package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GardenFormResult repository accessor
*/

@Repository
public interface FriendsRepository extends CrudRepository<Friends, Long> {
    @Query("SELECT u FROM Friends u WHERE u.user2.id = ?1 or u.user1.id = ?1")
    List<Friends> getAllFriends(Long user);

    @Query("SELECT u.user1 FROM Friends u WHERE u.user2.id = ?1")
    Friends checkFriend(Long user);

    @Query("SELECT p FROM Friends p WHERE (p.user1.id = ?1 AND p.user2.id = ?2) or (p.user2.id = ?2 AND p.user1.id = ?1) ")
    Friends getRequest(Long user1, Long user2);
}
