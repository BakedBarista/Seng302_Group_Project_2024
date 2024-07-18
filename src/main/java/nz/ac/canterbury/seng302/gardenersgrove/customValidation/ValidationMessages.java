package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

public class ValidationMessages {
    public static final String MAX_GARDEN_NAME_LENGTH_MESSAGE = "Garden size cannot exceed 50 characters";
    public static final String MAX_GARDEN_SIZE_MESSAGE = "Garden size must be less than 10,000,000";
    public static final String INVALID_GARDEN_SIZE_MESSAGE = "Garden size must be a valid positive number " +
            "(only allows numbers and a single period or comma)";
}
