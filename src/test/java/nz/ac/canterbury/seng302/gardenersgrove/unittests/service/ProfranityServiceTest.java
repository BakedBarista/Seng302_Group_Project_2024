package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ProfranityService;

public class ProfranityServiceTest {
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ProfranityService.loadConfigs();
    }

    @Test
    void givenListBadWords_ThenReturnTrue(){
        ArrayList<String> result = ProfranityService.badWordsFound("fuck");
        assertTrue(!result.isEmpty());
    }

    @Test
    void givenListEmptyWords_ThenReturnEmpty(){
        ArrayList<String> result = ProfranityService.badWordsFound("");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListGoodWords_ThenReturnEmpty(){
        ArrayList<String> result = ProfranityService.badWordsFound("this is a very interesting story about gardens, there are no swear words in here :)");
        assertTrue(result.isEmpty());
    }

    @Test
    void givenListBadWordsDisguised_ThenReturnTrue(){
        ArrayList<String> result = ProfranityService.badWordsFound("b!tch");
        assertTrue(!result.isEmpty());
    }
}
