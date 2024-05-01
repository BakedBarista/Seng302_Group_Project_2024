package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Friends
 */
@Service
@Transactional
public class FriendService {
    private final FriendsRepository friendsRepository;

    public FriendService(FriendsRepository friendsRepository) {this.friendsRepository = friendsRepository;}

    /**
     * Retrieves all friends of a user based on their ID while removing themselves
     *
     * @param user The ID of the user whose friends are being retrieved
     * @return A list of GardenUser objects
     */
    public List<GardenUser> getAllFriends(Long user) { 
        List<Friends> friendsPairList = friendsRepository.getAllFriends(user);
        List<GardenUser> friendsList = new ArrayList<>();
        for (Friends friend : friendsPairList) {
            if (friend.getUser1().getId().equals(user)) {
                friendsList.add(friend.getUser2());
            } else {
                friendsList.add(friend.getUser1());
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
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return A Friends object 
     */
    public Friends getFriendship(Long user1, Long user2) {
        return friendsRepository.getRequest(user1, user2);
    }

    /**
     * Retrieves a Friend object if they are confirmed friends
     *
     * @param user1 user who sent the original friend request
     * @param user2 user who received the original friend request
     * @return The friend object if the friendship between the users is "accepted"
     */
    public Friends getAcceptedFriendship(Long user1, Long user2) {
        return friendsRepository.getAcceptedFriendship(user1, user2);
    }

    /**
     * Retrieves the Friend object where user1 sent a request to user2
     *
     * @param user1 user who sent the original request
     * @param user2 user who received the original request
     * @return the Friend object where user 1 sent a request to user2
     */
    public Friends getSent(Long user1, Long user2) {
        return friendsRepository.getSent(user1, user2);
    }

     /**
     * Retrieves all sent friend requests by a user based on their ID.
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    public List<Friends> getSentRequests(Long user) {
        return friendsRepository.getSentRequests(user);
    }

    /**
     * Retrieves all pending friend requests
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    public List<Friends> getReceivedRequests(Long user) {
        return friendsRepository.getReceivedRequests(user);
    }

    /**
     * Retrieves all users who declined a request from the given user
     *
     * @param user The ID of the user whose friend request has been declined
     * @return A list of the users who declined friend requests from the given user
     */
    public List<Friends> getSentRequestsDeclined(Long user) {
        return friendsRepository.getSentRequestsDeclined(user);
    }

    /**
     * Removes the friendship where user1 and user2 are involved. Covers both cases depending on who sent the request
     *
     * @param user1Id user who sent the original request
     * @param user2Id user who received the original request
     */
    public void removeFriend(Long user1Id, Long user2Id) {
        friendsRepository.deleteByUser1IdAndUser2Id(user1Id, user2Id);
        friendsRepository.deleteByUser1IdAndUser2Id(user2Id, user1Id);

    }

    /**
     * Remove friend/cancel request
     * @param friends Friendship to remove
     */
    public void removeFriendship(Friends friends) {
        friendsRepository.delete(friends);

    }

}
