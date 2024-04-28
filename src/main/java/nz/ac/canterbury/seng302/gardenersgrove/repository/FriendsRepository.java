package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GardenFormResult repository accessor
*/

@Repository
public interface FriendsRepository extends CrudRepository<Friends, Long> {
    /**
     * Retrieves all friends of a user based on their ID
     *
     * @param user The ID of the user whose friends we want
     * @return A list of Friends od thta user
     */
    @Query("SELECT u FROM Friends u WHERE u.user2.id = ?1 or u.user1.id = ?1")
    List<Friends> getAllFriends(Long user);

    /**
     * Retrieves a friend request between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return A Friends object.
     */
    @Query("SELECT p FROM Friends p WHERE (p.user1.id = ?1 AND p.user2.id = ?2) or (p.user2.id = ?2 AND p.user1.id = ?1) ")
    Friends getRequest(Long user1, Long user2);
}
