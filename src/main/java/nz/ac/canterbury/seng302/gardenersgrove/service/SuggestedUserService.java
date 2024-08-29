package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.ACCEPTED;

@Service
public class SuggestedUserService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserService.class);
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

        // TODO: Remove this horrible for loop
        for (Friends sentRequest : sentRequests) {
            if (sentRequest.getReceiver().getId().equals(receiverId) && !sentRequest.getStatus().equals(ACCEPTED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check's for existing, pending received requests from the other user.
     *
     * Will accept this request if it finds one. If none can be found, then
     * nothing happens and false returned.
     * @param senderId the sender of the pending request
     * @param receiverId the receiver of the pending request
     * @return True if request accepted, otherwise false.
     */
    public boolean attemptToAcceptPendingRequest(Long senderId, Long receiverId) {
        Optional<Friends> pendingRequest = friendService.getPendingFriendRequest(receiverId, senderId);

        if (pendingRequest.isPresent()) {
            logger.info("Pending request found from ID: {} to {}", senderId, receiverId);
            logger.info("Accepting request");
            pendingRequest.get().setStatus(ACCEPTED);
            friendService.save(pendingRequest.get());
            return true;
        }
        logger.info("No pending request from ID: {} to {}", senderId, receiverId);
        return false;
    }
}
