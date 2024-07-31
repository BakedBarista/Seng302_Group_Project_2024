package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.PlantStatusApiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;

import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


class PlantStatusApiControllerTests {



    private PlantService plantService;

    private PlantStatusApiController plantStatusApiController;


    private Plant plant;

    @BeforeEach
    void setUp() {
        plantService = mock(PlantService.class);
        plantStatusApiController = new PlantStatusApiController(plantService);
        plant = new Plant();
        plant.setId(1L);
        plant.setName("Tomato");
        plant.setStatus(BasePlant.PlantStatus.NOT_GROWING);
    }

    @Test
    void testUpdatePlantStatusSuccess() throws Exception {
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updatePlantStatus(1L, BasePlant.PlantStatus.CURRENTLY_GROWING);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("CURRENTLY_GROWING", responseBody.get("status"));
        assertNull(responseBody.get("harvestedDate"));

        verify(plantService, times(1)).save(any(Plant.class));

    }

    @Test
    void testUpdatePlantStatusHarvested() throws Exception {
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updatePlantStatus(1L, BasePlant.PlantStatus.HARVESTED);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("HARVESTED", responseBody.get("status"));

        verify(plantService, times(1)).save(any(Plant.class));
    }




}
