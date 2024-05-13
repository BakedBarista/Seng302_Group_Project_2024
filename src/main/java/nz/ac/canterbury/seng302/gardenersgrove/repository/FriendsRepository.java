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
    @Query("SELECT u FROM Friends u WHERE (u.user1.id = ?1 or u.user2.id = ?1) and u.status='accepted'")
    List<Friends> getAllFriends(Long user);

    /**
     * Retrieves a friend request between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return A Friends object.
     */
    @Query("SELECT p FROM Friends p WHERE (p.user1.id = ?1 AND p.user2.id = ?2) or (p.user1.id = ?2 AND p.user2.id = ?1) ")
    Friends getRequest(Long user1, Long user2);

    /**
     * Retrieves a Friend object if they are confirmed friends
     *
     * @param user1 user who sent the original friend request
     * @param user2 user who received the original friend request
     * @return The friend object if the friendship between the users is "accepted"
     */
    @Query("SELECT p FROM Friends p WHERE ((p.user1.id = ?1 AND p.user2.id = ?2) or (p.user1.id = ?2 AND p.user2.id = ?1)) and p.status = 'accepted'")
    Friends getAcceptedFriendship(Long user1, Long user2);

    /**
     *
     * @param user1 user who sent the friend request
     * @param user2 user who received the friend request
     * @return the friendship where user1 sent the friend request to user2
     */
    @Query("SELECT p FROM Friends p WHERE p.user1.id = ?1 AND p.user2.id = ?2")
    Friends getSent(Long user1, Long user2);

    /**
     * Retrieves all sent friend requests by a user
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    @Query("SELECT u FROM Friends u WHERE u.user1.id = ?1 and u.status = 'Pending'")
    List<Friends> getSentRequests(Long user);

    /**
     * Retrieves all pending friend requests received by a user
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    @Query("SELECT u FROM Friends u WHERE u.user2.id = ?1 and u.status = 'Pending'")
    List<Friends> getReceivedRequests(Long user);

    /**
     *
     * @param user user that has been declined
     * @return a list of all the users who have declined friend request from the user
     */
    @Query("SELECT u FROM Friends u WHERE u.user1.id = ?1 and u.status = 'declined'")
    List<Friends> getSentRequestsDeclined(Long user);

    void deleteByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}

