package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;


public class ProfranityServiceTest {
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ProfanityService.loadConfigs();
    }

    @Test
    void givenListBadWords_ThenReturnTrue(){
        ArrayList<String> result = ProfanityService.badWordsFound("fuck");
        assertTrue(!result.isEmpty());
    }

    @Test
    void givenListEmptyWords_ThenReturnEmpty(){
        ArrayList<String> result = ProfanityService.badWordsFound("");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListGoodWords_ThenReturnEmpty(){
        ArrayList<String> result = ProfanityService.badWordsFound("this is a very interesting story about gardens, there are no swear words in here :)");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListBadWordsDisguised_ThenReturnTrue(){
        ArrayList<String> result = ProfanityService.badWordsFound("b!tch");
        assertTrue(!result.isEmpty());
    }
}
