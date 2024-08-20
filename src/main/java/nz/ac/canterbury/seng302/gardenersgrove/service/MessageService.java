package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TODO
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * TODO
     * @param messageDTO
     */
    public Message sendMessage(Long sender, Long receiver, MessageDTO messageDTO) {
        LocalDateTime timestamp = LocalDateTime.now();
        Message message = new Message(sender, receiver, timestamp, messageDTO.getMessage());
        messageRepository.save(message);
        return message;
    }

    /**
     * TODO
     * @param messageId
     * @return
     */
    public Optional<Message> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }
}
