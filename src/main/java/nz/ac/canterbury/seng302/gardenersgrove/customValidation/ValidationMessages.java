package nz.ac.canterbury.seng302.gardenersgrove.customValidation;

public class ValidationMessages {
    public static final String MAX_GARDEN_SIZE_LENGTH_MESSAGE = "Garden size cannot exceed 50 characters";
    public static final String MAX_GARDEN_SIZE_MESSAGE = "Garden size must be less than 10,000,000";
    public static final String INVALID_GARDEN_SIZE_MESSAGE = "Garden size must be a valid positive number " +
            "(only allows numbers and a single period or comma)";
    public static final String EMPTY_GARDEN_NAME_MESSAGE = "Garden name cannot be empty";
    public static final String MAX_GARDEN_NAME_MESSAGE = "Garden name cannot exceed 100 characters";
    public static final String INVALID_GARDEN_NAME_MESSAGE = "Garden name must only include letters, " +
            "numbers, spaces, dots, commas, hyphens, or apostrophes";
    public static final String INVALID_GARDEN_DESCRIPTION = "Description must be 512 characters or " +
            "less and contain some text";
    public static final String GARDEN_STREET_NUMBER_MESSAGE = "Please enter a valid street number";
    public static final String GARDEN_STREET_NAME_MESSAGE = "Please enter a valid street name";
    public static final String GARDEN_SUBURB_MESSAGE = "Please enter a valid suburb";
    public static final String GARDEN_CITY_MESSAGE = "Please enter a valid city";
    public static final String GARDEN_COUNTRY_MESSAGE = "Please enter a valid country";
    public static final String GARDEN_POST_CODE_MESSAGE = "Please enter a valid post code";
    public static final String GARDEN_CITY_REQUIRED_MESSAGE = "City is required";
    public static final String GARDEN_COUNTRY_REQUIRED_MESSAGE = "Country is required";

}
