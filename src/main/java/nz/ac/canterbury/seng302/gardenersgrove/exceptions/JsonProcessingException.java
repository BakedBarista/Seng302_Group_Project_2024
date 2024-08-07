package nz.ac.canterbury.seng302.gardenersgrove.exceptions;

/**
 * custom exception for when we have issue parsing JSON data in string format
 */
public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
