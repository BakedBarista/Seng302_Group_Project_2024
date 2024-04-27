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
     * test query
     * @return a List of all friends.
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


    public void save(Friends friendEntity) {
        friendsRepository.save(friendEntity);
    }

    public Friends getFriendship(Long user1, Long user2) {
        return friendsRepository.getRequest(user1, user2);
    }

}
