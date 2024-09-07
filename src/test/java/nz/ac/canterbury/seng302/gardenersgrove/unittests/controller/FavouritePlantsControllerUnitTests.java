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
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        GardenUser mockUser = new GardenUser();
        mockUser.setId(1L);
        plant = new Plant();
        plant.setId(1L);
        plant.setName("Rose");
        plantList = Collections.singletonList(plant);

       Garden mockGardens =new Garden("Rose Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", 100.0, null, null);
        plant.setGarden(mockGardens);

        when(userService.getCurrentUser()).thenReturn(mockUser);



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

        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Collections.singletonList(1L));

        ResponseEntity<String> response = favouritePlantsController.updateFavouritePlants(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).updateFavouritePlant(1L, plant);
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
}