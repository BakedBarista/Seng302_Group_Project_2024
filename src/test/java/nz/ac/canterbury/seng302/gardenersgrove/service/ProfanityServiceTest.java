package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.modernmt.text.profanity.dictionary.Profanity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;


public class ProfanityServiceTest {

    ProfanityService profanityService = new ProfanityService();

    @Test
    public void testThatIterativeOverAllLanguages_WithFullDescription_LessThanATenthSecond() {
        String description = "okay word ipsum dolor sit amet, consectetur adipiscing elit. Vivamus bibendum feugiat ex, " +
                "神秘的な世界への探求は終わりのない冒険です。Pellentesque habitant morbi tristique senectus et netus et " +
                "malesuada fames ac turpis egestas. Новите технологии донесоа со огромна брзина, révolutionnant " +
                "ainsi la façon dont nous interagissons avec le monde. Fusce tincidunt vulputate lorem, quis tempor" +
                " est. Suspendisse potenti. Nunc fringilla tempor libero, sit amet ultricies velit. Cras fermentum " +
                "dolor id urna viverra, สร้างโลกของคุณด้วยความคิดสร้างส";

        Instant timeStart = Instant.now();
        profanityService.findAllLanguages(description);
        Instant timeEnd = Instant.now();

        Assertions.assertTrue(Duration.between(timeStart, timeEnd).toSeconds() < 0.1);
    }

    @Test
    public void testThatWhenIHaveNoProfanity_ThereIsNoProfanityReturned() {
        // contains "cum" and "tit" as substrings - but should still be legal
        String description = "In the midst of dire circumstance, the author crafted a captivating story designed to " +
                "titillate readers and offer a temporary escape from reality.";

        Profanity profanity = profanityService.findAllLanguages(description);

        Assertions.assertNull(profanity);
    }

    @Test
    public void testThatWhenIHaveProfanity_FirstProfanityIsReturned() {
        String description = "Shit what do I write for this description fuck";

        Profanity profanity = profanityService.findAllLanguages(description);

        Assertions.assertNotNull(profanity);
    }
}
