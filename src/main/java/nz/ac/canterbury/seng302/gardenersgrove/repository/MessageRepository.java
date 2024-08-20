package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Handles saving and serving of messages
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Long>{
    Optional<Message> findById(long id);
}
