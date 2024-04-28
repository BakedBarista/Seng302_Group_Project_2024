package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * GardenFormResult repository accessor
*/

@Repository
public interface RequestRepository extends CrudRepository<Requests, Long> {
    /**
     * Retrieves all sent friend requests by a user
     *
     * @param user The ID of the user whose sent friend requests are being retrieved
     * @return A list of Requests objects
     */
    @Query("SELECT u FROM Requests u WHERE u.sent_user_id.id = ?1")
    List<Requests> getSentRequests(Long user);

    /**
     * Retrieves all pending friend requests received by a user
     *
     * @param user The ID of the user whose pending received friend requests are being retrieved
     * @return A list of Requests objects 
     */
    @Query("SELECT p FROM Requests p WHERE p.receive_user_id.id = ?1 and p.status = 'pending'")
    List<Requests> getReceivedRequests(Long user);
    
    /**
     * Retrieves a friend request between two users
     *
     * @param user1 The ID of the first user
     * @param user2 The ID of the second user
     * @return An Optional containing a Requests object
     */
    @Query("SELECT p FROM Requests p WHERE p.receive_user_id.id = ?1 AND p.sent_user_id.id = ?2")
    Optional<Requests> getRequest(Long user1, Long user2);
}
