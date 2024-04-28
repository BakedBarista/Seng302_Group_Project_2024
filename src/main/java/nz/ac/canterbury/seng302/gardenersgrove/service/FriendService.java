package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Friends
 */
@Service
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
     * Retrieves a friendship between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return A Friends object 
     */
    public Friends getFriendship(Long user1, Long user2) {
        return friendsRepository.getRequest(user1, user2);
    }

}
