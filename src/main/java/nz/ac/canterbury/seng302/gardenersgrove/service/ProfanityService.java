package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.modernmt.text.profanity.*;
import com.modernmt.text.profanity.dictionary.Profanity;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class for dealing with profanity inside of user submitted text
 */
public class ProfanityService {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList(
            "ar", "az", "bg", "bs", "ca", "cs", "da", "de", "el", "en", "es", "et", "fi", "fr", "ga", "he", "hi", "hr",
            "hu", "hy", "id", "is", "it", "ja", "ka", "ko", "lt", "lv", "mk", "ms", "mt", "no", "nl", "pl", "pt", "ro",
            "ru", "sk", "sl", "sq", "sr", "sv", "sw", "th", "tl", "tr", "uk", "vi", "xh", "zh", "zu");

    ProfanityFilter filter = new ProfanityFilter();

    /**
     * Find if there is any profanity over all supported languages
     * @param text to be checked for profanities
     * @return first profanity found
     */
    public Profanity findAllLanguages(String text) {
        if (text.length() > 20) {
            logger.info("checking for profanities for text '{}...' across all languages", text.substring(0, 20));
        }

        for (String language : SUPPORTED_LANGUAGES) {
            if (test(language, text)) {
                return find(language, text);
            }
        }
        return null;
    }

    /**
     * find profanity in a string for a single language
     * @param language that string is matched against
     * @param text string to be checked for profanities
     * @return Profanity present
     */
    public Profanity find(String language, String text) {
        return filter.find(language, text);
    }

    /**
     * check to see if a string does or does not contain profanities
     * @param language
     * @param language that string is matched against
     * @param text string to be checked for profanities
     * @return true if string contains profanities
     */
    public boolean test(String language, String text) {
        return filter.test(language, text);
    }
}
