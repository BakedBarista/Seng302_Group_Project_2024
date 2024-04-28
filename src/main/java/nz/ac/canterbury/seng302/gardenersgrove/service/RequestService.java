package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RequestRepository;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    /**
     * Constructs a new RequestService
     *
     * @param requestRepository The RequestRepository used by the service.
     */
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository= requestRepository;
    }

    /**
     * Retrieves all sent friend requests by a user based on their ID.
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    public List<Requests> getSentRequests(Long user) {
        return requestRepository.getSentRequests(user);
    }

    /**
     * Retrieves all pending friend requests
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    public List<Requests> getReceivedRequests(Long user) {
        return requestRepository.getReceivedRequests(user);
    }

    /**
     * Saves a friend request entity
     *
     * @param requestEntity The friend request entity to be saved
     */
    public void save(Requests requestEntity) {
        requestRepository.save(requestEntity);
    }

    /**
     * Deletes a friend request entity.
     *
     * @param requestEntity The friend request entity to be deleted.
     */
    public void delete(Requests requestEntity) {
        requestRepository.delete(requestEntity);
    }

    /**
     * Retrieves a friend request between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return An Optional containing a Requests object
     */
    public Optional<Requests> getRequest(Long user1, Long user2) {
        return requestRepository.getRequest(user1, user2);
    }

    
}
