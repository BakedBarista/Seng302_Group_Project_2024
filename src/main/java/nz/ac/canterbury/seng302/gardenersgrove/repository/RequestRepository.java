package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

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
    
    @Query("SELECT u FROM Requests u WHERE u.received_user_id.id = ?1")
    List<Requests> getReceivedRequests(Long user);

}
