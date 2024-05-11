package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    @Query("SELECT u FROM Friends u WHERE (u.sender.id = ?1 or u.receiver.id = ?1) and u.status='accepted'")
    List<Friends> getAllFriends(Long user);

    /**
     * Retrieves a friend request between two users
     *
     * @param sender The ID of the first user
     * @param receiver The ID of the second user
     * @return A Friends object.
     */
    @Query("SELECT p FROM Friends p WHERE (p.sender.id = ?1 AND p.receiver.id = ?2) or (p.sender.id = ?2 AND p.receiver.id = ?1) ")
    Friends getRequest(Long sender, Long receiver);

    /**
     * Retrieves a Friend object if they are confirmed friends
     *
     * @param sender user who sent the original friend request
     * @param receiver user who received the original friend request
     * @return The friend object if the friendship between the users is "accepted"
     */
    @Query("SELECT p FROM Friends p WHERE ((p.sender.id = ?1 AND p.receiver.id = ?2) or (p.sender.id = ?2 AND p.receiver.id = ?1)) and p.status = 'accepted'")
    Friends getAcceptedFriendship(Long sender, Long receiver);

    /**
     *
     * @param sender user who sent the friend request
     * @param receiver user who received the friend request
     * @return the friendship where sender sent the friend request to receiver
     */
    @Query("SELECT p FROM Friends p WHERE p.sender.id = ?1 AND p.receiver.id = ?2")
    Friends getSent(Long sender, Long receiver);

    /**
     * Retrieves all sent friend requests by a user
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    @Query("SELECT u FROM Friends u WHERE u.sender.id = ?1 and u.status = 'pending'")
    List<Friends> getSentRequests(Long user);

    /**
     * Retrieves all pending friend requests received by a user
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    @Query("SELECT u FROM Friends u WHERE u.receiver.id = ?1 and u.status = 'pending'")
    List<Friends> getReceivedRequests(Long user);

    /**
     *
     * @param user user that has been declined
     * @return a list of all the users who have declined friend request from the user
     */
    @Query("SELECT u FROM Friends u WHERE u.sender.id = ?1 and u.status = 'declined'")
    List<Friends> getSentRequestsDeclined(Long user);

    void deleteBySenderIdAndReceiverId(Long senderId, Long receiverId);
}

