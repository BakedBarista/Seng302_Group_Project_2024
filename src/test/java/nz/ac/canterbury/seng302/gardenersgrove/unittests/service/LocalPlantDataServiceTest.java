package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocalPlantDataService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StringDistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalPlantDataServiceTest {

    @Mock
    private StringDistanceService stringDistanceService;

    private ObjectMapper objectMapper;
    private LocalPlantDataService localPlantDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        localPlantDataService = new LocalPlantDataService(objectMapper, stringDistanceService);

        // Manually setting localPlants since it's normally loaded from a file
        List<PlantInfoDTO> localPlants = new ArrayList<>();
        localPlants.add(new PlantInfoDTO("Rose", "A beautiful flower", "", ""));
        localPlants.add(new PlantInfoDTO("Tulip", "A spring flower", "", ""));
        localPlants.add(new PlantInfoDTO("Sunflower", "A tall, bright flower", "", ""));

        try {
            Field localPlantsField = LocalPlantDataService.class.getDeclaredField("localPlants");
            localPlantsField.setAccessible(true);
            localPlantsField.set(localPlantDataService, localPlants);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test

    void givenMatchingPlantFromFileWhenCheckedReturnCorrectResults() {
        PlantInfoDTO result = localPlantDataService.getMatchingPlantInfoFromFile("Rose");
        assertEquals("Rose", result.getLabel());
        assertEquals("A beautiful flower", result.getDescription());

        result = localPlantDataService.getMatchingPlantInfoFromFile("Tulip");
        assertEquals("Tulip", result.getLabel());
        assertEquals("A spring flower", result.getDescription());
    }

    @Test
    void whenSimilarNameEnteredReturnCorrectPlant() {
        List<String> similarPlantNames = new ArrayList<>();
        similarPlantNames.add("Rose");
        similarPlantNames.add("Sunflower");

        when(stringDistanceService.getSimilarStrings(anyList(), anyString())).thenAnswer(invocation -> {
            List<String> plantNames = invocation.getArgument(0);
            String searchString = invocation.getArgument(1);
            if ("Ros".equals(searchString)) {
                return similarPlantNames;
            }
            return Collections.emptyList();
        });

        JsonNode result = localPlantDataService.getSimilarPlantInfo("Ros");

        //assertEquals(2, result.get("plants").size());
        assertEquals("Rose", result.get("plants").get(0).get("label").asText());
        assertEquals("Sunflower", result.get("plants").get(1).get("label").asText());
    }
}
