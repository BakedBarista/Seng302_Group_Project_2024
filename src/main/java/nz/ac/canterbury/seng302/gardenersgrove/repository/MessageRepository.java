package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Handles saving and serving of messages
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Long>{
    Optional<Message> findById(long id);
    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1)")
    List<Message> findMessagesBetweenUsers(Long user1, Long user2);
}
