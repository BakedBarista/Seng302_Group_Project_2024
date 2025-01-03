package nz.ac.canterbury.seng302.gardenersgrove.validation;

public class ValidationConstants {

    private ValidationConstants() {}
    public static final int NAME_MAX_LEN = 64;
    public static final int USER_MIN_AGE = 13;
    public static final int USER_MAX_AGE = 120;

    public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9]+(?:[._-]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)?\\.)+[a-zA-Z]{2,7}$";
    
    public static final String EMAIL_LENGTH_REGEX = "^(?=.{1,64}@)(?=.{1,255}$).+$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    public static final String NAME_REGEX = "^('?\\p{L}[\\p{L}\\s'-]*)$";
    public static final String DESCRIPTION_REGEX = "^(.*\\p{L}.*)?$";

    public static final String GARDEN_REGEX = "^[\\p{L}0-9 .,'-]*$";
    public static final String POSITIVE_WHOLE_NUMBER_REGEX = "^[0-9]*(?:\\.0*)?$";

    public static final String TAG_REGEX = "^[\\p{L}0-9\\s\\-_'\"]*$";
    public static final int TAG_MAX_LEN = 25;
    public static final int MAX_GARDEN_SIZE = 9999999;

    public static final int MAX_USER_DESCRIPTION_LENGTH = 512;

}
