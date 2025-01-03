package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class to help thymeleaf deal with the formatting of LocalDates into String using DateTimeFormatter,
 * required because Thymeleaf has issues doing this itself.
 */
public class ThymeLeafDateFormatter {

    private static String DEFAULT_RESPONSE = "-";

    /**
     * empty constructor so that you can pass the class instance to thymeleaf
     */
    public ThymeLeafDateFormatter() {}

    /**
     * Formats the LocalDate into a String using a given DateTimeFormatter object, this is primarily used by Thymeleaf
     * because Thymeleaf cannot handle this itself (in some circumstances)
     * @param date LocalDate to format
     * @param formatter formatter provided
     * @return the date as a formatted string or "-" if the date is null
     */
    public String format(LocalDate date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        } else {
            return DEFAULT_RESPONSE;
        }
    }

    /**
     * Formats the LocalDateTime into a String using a given DateTimeFormatter object, this is primarily used by Thymeleaf
     * because Thymeleaf cannot handle this itself (in some circumstances)
     * @param date LocalDateTime to format
     * @param formatter formatter provided
     * @return the date as a formatted string or "-" if the date is null
     */
    public String format(LocalDateTime date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        } else {
            return DEFAULT_RESPONSE;
        }
    }
}
