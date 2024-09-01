package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;

/**
 * Service class for Friends
 */
@Service
@Transactional
public class FriendService {
    private final FriendsRepository friendsRepository;

    @Autowired
    public FriendService(FriendsRepository friendsRepository) {this.friendsRepository = friendsRepository;}

    /**
     * Retrieves all friends of a user based on their ID while removing themselves
     *
     * @param user The ID of the user whose friends are being retrieved
     * @return A list of GardenUser objects
     */
    public List<GardenUser> getAllFriends(Long user) { 
        List<Friends> friendsPairList = friendsRepository.getAllFriendshipsWithStatus(user, ACCEPTED);
        List<GardenUser> friendsList = new ArrayList<>();
        for (Friends friend : friendsPairList) {
            if (friend.getSender().getId().equals(user)) {
                friendsList.add(friend.getReceiver());
            } else {
                friendsList.add(friend.getSender());
            }
        }
        return friendsList;
    }

    /**
     * Saves a friendship
     *
     * @param friendEntity The friendship entity
     */
    public void save(Friends friendEntity) {
        friendsRepository.save(friendEntity);
    }

    /**
     * Saves a friendship
     *
     * @param friendEntity The friendship entity
     */
    public void delete(Friends friendEntity) {
        friendsRepository.delete(friendEntity);
    }

    /**
     * Retrieves a friendship between two users
     *
     * @param sender The ID of the first user
     * @param receiver The ID of the second user
     * @return A Friends object 
     */
    public Friends getFriendship(Long sender, Long receiver) {
        return friendsRepository.getFriendshipBetweenUsers(sender, receiver);
    }

    /**
     * Retrieves a Friend object if they are confirmed friends
     *
     * @param sender user who sent the original friend request
     * @param receiver user who received the original friend request
     * @return The friend object if the friendship between the users is ACCEPTED
     */
    public Friends getAcceptedFriendship(Long sender, Long receiver) {
        return friendsRepository.getFriendshipBetweenUsersWithStatus(sender, receiver, ACCEPTED);
    }

    /**
     * Gets a declined friendship between 2 users if one exists.
     * @param sender user 1
     * @param receiver user 2
     * @return An optional of the declined friendship
     */
    public Optional<Friends> getDeclinedFriendship(Long sender, Long receiver) {
        return Optional.ofNullable(friendsRepository.getFriendshipBetweenUsersWithStatus(sender, receiver, DECLINED));
    }


    /**
     * Retrieves the Friend object where sender sent a request to receiver
     *
     * @param sender user who sent the original request
     * @param receiver user who received the original request
     * @return the Friend object where user 1 sent a request to receiver
     */
    public Friends getSent(Long sender, Long receiver) {
        return friendsRepository.getAllFriendshipsBetweenUsers(sender, receiver);
    }

     /**
     * Retrieves all sent friend requests by a user based on their ID.
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    public List<Friends> getSentRequests(Long user) {
        return friendsRepository.getFriendshipsFromUserWhereStatusIsNot(user, ACCEPTED);
    }

    /**
     * Retrieves all pending friend requests
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    public List<Friends> getReceivedRequests(Long user) {
        return friendsRepository.getFriendshipsToUserWithStatus(user, PENDING);
    }

    /**
     * Looks for a request from the sender id and returns this friendship request if
     * found.
     *
     * @param userId The ID of the user whose might have received the pending request
     * @param senderId The ID of the person who might have sent the request
     * @return an optional of the request if it is found.
     */
    public Optional<Friends> getPendingFriendRequest(Long userId, Long senderId) {
        return friendsRepository.findPendingFriendRequest(senderId, userId);
    }

    /**
     * Retrieves all users who declined a request from the given user
     *
     * @param user The ID of the user whose friend request has been declined
     * @return A list of the users who declined friend requests from the given user
     */
    public List<Friends> getSentRequestsDeclined(Long user) {
        return friendsRepository.getFriendshipsFromUserWithStatus(user, DECLINED);
    }

    /**
     * Removes the friendship where sender and receiver are involved. Covers both cases depending on who sent the request
     *
     * @param senderId user who sent the original request
     * @param receiverId user who received the original request
     */
    public void removeFriend(Long senderId, Long receiverId) {
        friendsRepository.deleteBySenderIdAndReceiverId(senderId, receiverId);
        friendsRepository.deleteBySenderIdAndReceiverId(receiverId, senderId);

    }

    /**
     * Remove friend/cancel request
     * @param friends Friendship to remove
     */
    public void removeFriendship(Friends friends) {
        friendsRepository.delete(friends);

    }


    /**
     * Finds any Pending or Declined relationships that exist between two users, in either direction.
     * @param user1Id the first user
     * @param user2Id the second user
     * @return an optional of the relationship if it exists.
     */
    public Optional<Friends> getPendingOrDeclinedRequests(Long user1Id, Long user2Id) {
        return friendsRepository.findPendingOrDeclinedFriendship(user1Id, user2Id);
    }

}
