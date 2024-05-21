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
    @Query("SELECT u FROM Friends u WHERE (u.sender.id = ?1 or u.receiver.id = ?1) and u.status=?2")
    List<Friends> getAllFriendshipsWithStatus(Long user, Friends.Status status);

    /**
     * Retrieves a friend request between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return A Friends object.
     */
    @Query("SELECT p FROM Friends p WHERE p.friend_id = (SELECT MAX(f.friend_id) FROM Friends f WHERE (f.sender.id = ?1 AND f.receiver.id = ?2) OR (f.sender.id = ?2 AND f.receiver.id = ?1))")
    Friends getFriendshipBetweenUsers(Long user1, Long user2);

    /**
     * Retrieves a Friend object if they are confirmed friends
     *
     * @param user1 user who sent the original friend request
     * @param user2 user who received the original friend request
     * @return The friend object if the friendship between the users is ACCEPTED
     */
    @Query("SELECT p FROM Friends p WHERE ((p.sender.id = ?1 AND p.receiver.id = ?2) or (p.sender.id = ?2 AND p.receiver.id = ?1)) and p.status = ?3")
    Friends getFriendshipBetweenUsersWithStatus(Long user1, Long user2, Friends.Status status);

    /**
     *
     * @param sender user who sent the friend request
     * @param receiver user who received the friend request
     * @return the friendship where sender sent the friend request to receiver
     */
    @Query("SELECT p FROM Friends p WHERE p.friend_id = (SELECT MAX(f.friend_id) FROM Friends f WHERE f.sender.id = ?1 AND f.receiver.id = ?2)")
    Friends getAllFriendshipsBetweenUsers(Long sender, Long receiver);

    /**
     * Retrieves all sent friend requests by a user
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    @Query("SELECT u FROM Friends u WHERE u.sender.id = ?1 AND u.status != ?2")
    List<Friends> getFriendshipsFromUserWhereStatusIsNot(Long sender, Friends.Status status);

    /**
     * Retrieves all pending friend requests received by a user
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    @Query("SELECT u FROM Friends u WHERE u.receiver.id = ?1 and u.status = ?2")
    List<Friends> getFriendshipsToUserWithStatus(Long receiver, Friends.Status status);

    /**
     *
     * @param user user that has been declined
     * @return a list of all the users who have declined friend request from the user
     */
    @Query("SELECT u FROM Friends u WHERE u.sender.id = ?1 and u.status = ?2")
    List<Friends> getFriendshipsFromUserWithStatus(Long sender, Friends.Status status);

    void deleteBySenderIdAndReceiverId(Long senderId, Long receiverId);
}

