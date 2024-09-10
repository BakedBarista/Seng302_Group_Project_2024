package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.Size;

/** DTO For handing the submission of messages */
public class MessageDTO {

    @Size(max = 160, message = "Messages can not be longer than 160 characters.")
    private final String message;


    public MessageDTO(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
