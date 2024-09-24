package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.message.MessageRead;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for storing and retrieving MessageRead objects
 */
public interface MessageReadRepository extends CrudRepository<MessageRead, Long> {
    Optional<MessageRead> findByReceiverIdAndUserId(Long receiverId, Long userId);
}