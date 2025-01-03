package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.DeleteFavouritePlantController;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

class DeleteFavouritePlantControllerTest {

    private static GardenUserService gardenUserService;

    private static DeleteFavouritePlantController controller;

    @BeforeAll
    static void setup() {
        gardenUserService = mock(GardenUserService.class);
        PlantService plantService = mock(PlantService.class);
        controller = new DeleteFavouritePlantController(gardenUserService, plantService);
    }

    @Test
    void givenIAmAuthorized_whenIDeletePlantWithId_thenPlantWithIdIsRemoved() {
        Long userId = 999L;
        Long plantId = 1L;
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("plantId", plantId);

        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userId);

        controller.deleteFavouritePlant(requestBody, authentication);

        Mockito.verify(gardenUserService).removeFavouritePlant(userId, plantId);
    }
}
