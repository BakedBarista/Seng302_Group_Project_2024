package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO For handing the submission of messages */
public class MessageDTO {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 160, message = "Message too long.")
    private final String message;


    public MessageDTO(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
