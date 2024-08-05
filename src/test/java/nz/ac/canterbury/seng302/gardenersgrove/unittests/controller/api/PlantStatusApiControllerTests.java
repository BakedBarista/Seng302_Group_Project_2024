package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.PlantStatusApiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHarvestedDateDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import nz.ac.canterbury.seng302.gardenersgrove.service.ThymeLeafDateFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


class PlantStatusApiControllerTests {



    private PlantService plantService;

    private PlantStatusApiController plantStatusApiController;
    private final ThymeLeafDateFormatter dateFormatter = new ThymeLeafDateFormatter();


    private Plant plant;


    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        plantService = mock(PlantService.class);
        bindingResult = mock(BindingResult.class);
        plantStatusApiController = new PlantStatusApiController(plantService);
        plant = new Plant();
        plant.setId(1L);
        plant.setName("Tomato");
        plant.setStatus(BasePlant.PlantStatus.NOT_GROWING);
    }

    @Test
    void givenPlantId_whenUpdatePlantStatus_thenReturnSuccessResponse()  throws Exception {
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updatePlantStatus(1L, BasePlant.PlantStatus.CURRENTLY_GROWING);


        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();


        verify(plantService, times(1)).save(any(Plant.class));
        assertEquals("CURRENTLY_GROWING", responseBody.get("status"));
        assertNull(responseBody.get("harvestedDate"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void givenPlantIdAndHarvestedStatus_whenUpdatePlantStatus_thenReturnSuccessResponse()  throws Exception {
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updatePlantStatus(1L, BasePlant.PlantStatus.HARVESTED);


        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();


        verify(plantService, times(1)).save(any(Plant.class));
        assertEquals("HARVESTED", responseBody.get("status"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void givenPlantIdAndHarvestedStatus_whenSetHarvestedDate_thenReturnSuccessResponse()  throws Exception {
        LocalDate harvestedDate = LocalDate.now();
        PlantHarvestedDateDTO plantHarvestedDateDTO = new PlantHarvestedDateDTO();
        plantHarvestedDateDTO.setHarvestedDate(String.valueOf(harvestedDate));
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updateHarvestedDate(1L, plantHarvestedDateDTO, bindingResult);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        verify(plantService, times(1)).save(any(Plant.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dateFormatter.format(harvestedDate, DateTimeFormats.NZ_FORMAT_DATE), responseBody.get("harvestedDate"));


    }

    @Test
    void givenInvalidHarvestDate_whenUpdateHarvestDate_thenReturnBadResponse() throws Exception {
        PlantHarvestedDateDTO plantHarvestedDateDTO = new PlantHarvestedDateDTO();
        plantHarvestedDateDTO.setHarvestedDate("2021-13-01");
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        when(bindingResult.hasErrors()).thenReturn(true);
        ResponseEntity <Map<String,Object>> response = plantStatusApiController.updateHarvestedDate(1L, plantHarvestedDateDTO, bindingResult);


        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();


        verify(plantService, times(0)).save(any(Plant.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", responseBody.get("status"));
        assertNotNull(responseBody.get("errors"));

    }
    @Test
    void givenInvalidDate_whenUpdateDate_thenReturnBadResponse() {
        Long plantId = 1L;
        PlantHarvestedDateDTO request = new PlantHarvestedDateDTO();
        request.setHarvestedDate("2021-13-01");

        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(new Plant()));
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = plantStatusApiController.updateHarvestedDate(plantId, request, bindingResult);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("status", "error");
        expectedResponse.put("errors", List.of("Invalid date format"));

        assertEquals(ResponseEntity.badRequest().body(expectedResponse), response);
    }

    @Test
    void givenHarvestDateBeforePlantedDate_whenUpdateHarvestDate_thenReturnBadResponse() {
        Long plantId = 1L;
        PlantHarvestedDateDTO request = new PlantHarvestedDateDTO();
        request.setHarvestedDate("2021-01-01");

        Plant plant = new Plant();
        plant.setPlantedDate(LocalDate.of(2021, 1, 2));

        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = plantStatusApiController.updateHarvestedDate(plantId, request, bindingResult);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("status", "error");
        expectedResponse.put("errors", List.of("Harvested date must be after planted date"));

        assertEquals(ResponseEntity.badRequest().body(expectedResponse), response);
    }






}
