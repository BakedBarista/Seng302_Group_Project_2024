package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.MessageRead;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MessageReadRepository extends CrudRepository<MessageRead, Long> {
    Optional<MessageRead> findByReceiverIdAndUserId(Long receiverId, Long userId);
}