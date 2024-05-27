package nz.ac.canterbury.seng302.gardenersgrove.customValidation;
import java.time.format.DateTimeFormatter;

public class DateTimeFormats {

    /**
     * private constructor to prevent instantiation of this class that stores static values
     */
    private DateTimeFormats() {}
    public static final DateTimeFormatter NZ_FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter WEATHER_CARD_FORMAT_DATE = DateTimeFormatter.ofPattern("EEEE dd MMM");
}
