package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * GardenFormResult repository accessor
*/

@Repository
public interface RequestRepository extends CrudRepository<Requests, Long> {
    @Query("SELECT u FROM Requests u WHERE u.sent_user_id.id = ?1")
    List<Requests> getSentRequests(Long user);
    
    @Query("SELECT p FROM Requests p WHERE p.receive_user_id.id = ?1 and p.status = 'pending'")
    List<Requests> getReceivedRequests(Long user);
 
    @Query("SELECT p FROM Requests p WHERE p.receive_user_id.id = ?1 AND p.sent_user_id.id = ?2")
    Requests getRequest(Long user1, Long user2);
}
