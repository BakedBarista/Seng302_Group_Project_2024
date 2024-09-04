package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.DeleteFavouritePlantController;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;

public class DeleteFavouritePlantControllerTest {

    private static GardenUserService gardenUserService;

    private static DeleteFavouritePlantController controller;

    @BeforeAll
    static void setup() {
        gardenUserService = mock(GardenUserService.class);
        controller = new DeleteFavouritePlantController(gardenUserService);
    }

    @Test
    void givenIAmAuthorized_whenIDeletePlantWithId_thenPlantWithIdIsRemoved() {
        Long userId = 999L;
        Long plantId = 1L;
        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userId);

        controller.deleteFavouritePlant(plantId, authentication);

        Mockito.verify(gardenUserService).removeFavouritePlant(userId, plantId);
    }
}
