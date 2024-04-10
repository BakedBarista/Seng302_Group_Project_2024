package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.modernmt.text.profanity.*;
import com.modernmt.text.profanity.dictionary.Profanity;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProfanityService {
    Logger logger = LoggerFactory.getLogger(GardenController.class);

    // todo - change order so we can break out faster e.g. english first?
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

    private Profanity find(String language, String text) {
        return filter.find(language, text);
    }

    private boolean test(String language, String text) {
        return filter.test(language, text);
    }
}
