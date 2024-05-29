package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ProfanityServiceTest {

    private ProfanityService profanityService;
    @BeforeEach
    void setup() {
        profanityService = new ProfanityService();
        profanityService.loadConfigs();
    }

    @Test
    void givenListBadWords_ThenReturnTrue(){
        ArrayList<String> result = profanityService.badWordsFound("fuck");
        assertFalse(result.isEmpty());
    }

    @Test
    void givenListEmptyWords_ThenReturnEmpty(){
        ArrayList<String> result = profanityService.badWordsFound("");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListGoodWords_ThenReturnEmpty(){
        ArrayList<String> result = profanityService.badWordsFound("this is a very interesting story about gardens, there are no swear words in here :)");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListBadWordsDisguised_ThenReturnTrue(){
        ArrayList<String> result = profanityService.badWordsFound("b!tch");
        assertFalse(result.isEmpty());
    }

    @Test
    void testBadWordsFound_WithLeetSpeak() {
        String input = "@ss";
        ArrayList<String> result = profanityService.badWordsFound(input);
        assertEquals(1, result.size());
        assertEquals("ass", result.get(0));
    }

    @Test
    void testIsStandaloneWord_Standalone() {
        String input = "badword in a sentence.";
        boolean result = profanityService.isStandaloneWord(input, 0, 7);
        assertTrue(result);
    }

    @Test
    void testIsStandaloneWord_NotStandalone() {
        String input = "badwordin a sentence.";
        boolean result = profanityService.isStandaloneWord(input, 0, 7);
        assertFalse(result);
    }
}
