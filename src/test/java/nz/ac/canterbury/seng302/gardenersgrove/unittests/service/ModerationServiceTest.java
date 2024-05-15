package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;
import nz.ac.canterbury.seng302.gardenersgrove.service.ModerationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class ModerationServiceTest {

    private ModerationService moderationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        moderationService = new ModerationService();
    }

    @Test
    public void givenEmptyBody_ReturnFalse() {
        Assertions.assertFalse(moderationService.checkIfDescriptionIsFlagged(""));
    }
}
