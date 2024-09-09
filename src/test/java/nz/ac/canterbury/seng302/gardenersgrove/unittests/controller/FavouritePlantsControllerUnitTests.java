package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouritePlantsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouritePlantsControllerUnitTests {

    @Mock
    private GardenUserService userService;

    @Mock
    private PlantService plantService;

    @InjectMocks
    private FavouritePlantsController favouritePlantsController;

    private Plant plant;
    private List<Plant> plantList;

    @BeforeEach
    void setUp() {

        plant = new Plant();
        plant.setId(1L);
        plant.setName("Rose");
        plantList = Collections.singletonList(plant);

       Garden mockGardens =new Garden("Rose Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", 100.0, null, null);
        plant.setGarden(mockGardens);




    }

    @Test
    void testSearchPlantsWithValidSearchTerm() {
        when(plantService.getAllPlants(any(), eq("Rose"))).thenReturn(plantList);

        ResponseEntity<List<Map<String, Object>>> response = favouritePlantsController.searchPlants("Rose");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Rose", response.getBody().get(0).get("name"));
    }

    @Test
    void testSearchPlantsWithEmptySearchTerm() {
        when(plantService.getAllPlants(any(), eq(""))).thenReturn(plantList);

        ResponseEntity<List<Map<String, Object>>> response = favouritePlantsController.searchPlants("");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Rose", response.getBody().get(0).get("name"));
    }

    @Test
    void testUpdateFavouritePlantsWithValidIds() {
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        GardenUser mockUser = new GardenUser();
        mockUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Collections.singletonList(1L));

        Set<Plant> newSet = new HashSet<>();
        newSet.add(plant);
        ResponseEntity<String> response = favouritePlantsController.updateFavouritePlants(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).updateFavouritePlant(1L, newSet);
    }

    @Test
    void testUpdateFavouritePlantsWithInvalidIds() {
        when(plantService.getPlantById(1L)).thenReturn(Optional.empty());

        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Collections.singletonList(1L));

        ResponseEntity<String> response = favouritePlantsController.updateFavouritePlants(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, never()).updateFavouritePlant(anyLong(), any());
    }

    @Test
    void testUpdateFavouritePlantsWithEmptyIds() {

        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Collections.emptyList());

        ResponseEntity<String> response = favouritePlantsController.updateFavouritePlants(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, never()).updateFavouritePlant(anyLong(), any());
    }

    @Test
    void testUpdateFavouritePlantsException() {
        // Setup the request
        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Arrays.asList(1L, 2L, 3L));

        when(plantService.getPlantById(anyLong())).thenThrow(new RuntimeException("Database error"));


        ResponseEntity<String> response = favouritePlantsController.updateFavouritePlants(request);

        // Assert that the response status is INTERNAL_SERVER_ERROR
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Assert that the response body contains the exception message
        assertTrue(response.getBody().contains("Database error"));

        // Verify that the logger was called with the expected message
        // Note: To verify logging, you might need to use a logging framework or tool.
    }
}