package nz.ac.canterbury.seng302.gardenersgrove.unittests;

import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WeLoveSonarQube {

    @Test
    void givenSonarQubeFailsForGettersAndSetters_TestMakeWork () {
        Message message = new Message();
        message.setReaction("Sonar");

        Assertions.assertNull(message.getId());
        Assertions.assertEquals("Sonar", message.getReaction());
    }
}
