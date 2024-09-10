package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

/** DTO For handing the submission of messages */
public class MessageDTO {
    private final String message;

    private final String submissionToken;


    public MessageDTO(String message, String submissionToken) {

        this.message = message;
        this.submissionToken = submissionToken;
    }

    public String getMessage() {
        return message;
    }

    public String getSubmissionToken() {return submissionToken;}
}
