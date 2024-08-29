package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.ACCEPTED;

@Service
public class SuggestedUserService {

    private final FriendService friendService;

    @Autowired
    public SuggestedUserService(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Checks if there is an existing friendship request between two users.
     * The requests can be either PENDING or DECLINED
     * @param senderId the request sender's ID
     * @param receiverId the request receiver's ID
     * @return true if request exists, false otherwise
     */
    public boolean friendRecordExists(Long senderId, Long receiverId) {
        List<Friends> sentRequests = friendService.getSentRequests(senderId);
        for (Friends sentRequest : sentRequests) {
            if (sentRequest.getReceiver().getId().equals(receiverId) && !sentRequest.getStatus().equals(ACCEPTED)) {
                return true;
            }
        }
        return false;
    }
}
