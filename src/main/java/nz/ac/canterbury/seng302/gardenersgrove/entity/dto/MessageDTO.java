package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

/** DTO For handing the submission of messages */
public class MessageDTO {
    private final String message;

    public MessageDTO(String message) {
        this.message = message;
    }

    /**
     * Get the stored message
     * @return the stored message
     */
    public String getMessage() {
        return message;
    }
}
