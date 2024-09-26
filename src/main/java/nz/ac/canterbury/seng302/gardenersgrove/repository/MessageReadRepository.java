package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.MessageRead;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and retrieving MessageRead objects
 */
public interface MessageReadRepository extends CrudRepository<MessageRead, Long> {
    Optional<MessageRead> findByReceiverIdAndUserId(Long receiverId, Long userId);

    @Query("SELECT m FROM MessageRead m WHERE m.receiverId = ?1")
    List<MessageRead> findAllByUserId(Long userId);
}