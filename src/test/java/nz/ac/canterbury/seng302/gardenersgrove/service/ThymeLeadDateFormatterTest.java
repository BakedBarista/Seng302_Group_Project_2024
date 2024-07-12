package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.customValidation.DateTimeFormats;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ThymeLeadDateFormatterTest {

    ThymeLeafDateFormatter thymeLeafDateFormatter = new ThymeLeafDateFormatter();

    @Test
    void givenValidDate_AndNzDateFormatter_ReturnStringOfFormattedDate() {
        DateTimeFormatter format = DateTimeFormats.NZ_FORMAT_DATE;
        LocalDate date = LocalDate.of(2024, 1, 1);

        Assertions.assertEquals("01/01/2024", thymeLeafDateFormatter.format(date, format));
    }

    @Test
    void givenValidDate_AndWeatherCardFormatter_ReturnStringOfFormattedDate() {
        DateTimeFormatter format = DateTimeFormats.WEATHER_CARD_FORMAT_DATE;
        LocalDate date = LocalDate.of(2024, 1, 1);

        Assertions.assertEquals("Monday 01 Jan", thymeLeafDateFormatter.format(date, format));
    }

    @Test
    void givenNullDate_AndNzDateFormatter_ReturnStringOfFormattedDate() {
        DateTimeFormatter format = DateTimeFormats.NZ_FORMAT_DATE;
        LocalDate date = null;

        Assertions.assertEquals("-", thymeLeafDateFormatter.format(date, format));
    }
}
