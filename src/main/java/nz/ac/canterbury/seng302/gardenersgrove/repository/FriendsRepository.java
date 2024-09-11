package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
     * Looks for a pending friend request from one user to another.
     * @param senderId
     * @param receiverId
     * @return an Optional containing the pending request if it exists
     */
    @Query("SELECT f FROM Friends f WHERE f.sender.id = :senderId AND f.receiver.id = :receiverId AND f.status = 'PENDING'")
    Optional<Friends> findPendingFriendRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     *
     * @param user user that has been declined
     * @return a list of all the users who have declined friend request from the user
     */
    @Query("SELECT u FROM Friends u WHERE u.sender.id = ?1 and u.status = ?2")
    List<Friends> getFriendshipsFromUserWithStatus(Long sender, Friends.Status status);

    void deleteBySenderIdAndReceiverId(Long senderId, Long receiverId);

    /**
     * Finds any pending or declined friendships that may exist between 2 users.
     * The relationship is from either direction.
     * Will not return
     * @param userId1 the first user's id
     * @param userId2 the second user's id
     * @return an optional of the friends entity if it exists
     */
    @Query("SELECT f FROM Friends f WHERE ((f.sender.id = :userId1 AND f.receiver.id = :userId2) OR (f.receiver.id = :userId2 AND f.sender.id = :userId1)) AND (f.status = 'PENDING' OR f.status = 'DECLINED')")
    Optional<Friends> findPendingOrDeclinedFriendship(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Transactional
    void deleteBySenderId(Long senderId);

    /**
     * Get all users who are available to connect with a given user
     * @param userId user that is getting connections
     * @return a list of all the users who are available to connect with a given user
     */
    @Query("SELECT otherUser FROM GardenUser otherUser WHERE NOT EXISTS (SELECT f FROM Friends f WHERE f.sender.id = :userId AND f.receiver = otherUser) AND NOT EXISTS (SELECT f FROM Friends f WHERE f.sender = otherUser AND f.receiver.id = :userId) AND otherUser.id != :userId")
    List<GardenUser> getAvailableConnections(Long userId);
}

