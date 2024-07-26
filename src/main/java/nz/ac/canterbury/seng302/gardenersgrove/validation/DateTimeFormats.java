package nz.ac.canterbury.seng302.gardenersgrove.validation;
import java.time.format.DateTimeFormatter;

/**
 * Static class for holding different useful static DateTimeFormatter objects
 */
public class DateTimeFormats {

    /**
     * private constructor to prevent instantiation of this class that stores static values
     */
    private DateTimeFormats() {}
    public static final DateTimeFormatter NZ_FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter WEATHER_CARD_FORMAT_DATE = DateTimeFormatter.ofPattern("EEEE dd MMM");

    public static final DateTimeFormatter HISTORY_FORMAT_DATE = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
}
