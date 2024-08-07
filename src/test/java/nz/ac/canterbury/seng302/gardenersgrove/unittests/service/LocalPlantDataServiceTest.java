package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocalPlantDataService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StringDistanceService;

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

        List<PlantInfoDTO> result = localPlantDataService.getSimilarPlantInfo("Ros");

        assertEquals(2, result.size());
        assertEquals("Rose", result.get(0).getLabel());
        assertEquals("Sunflower", result.get(1).getLabel());
    }
}
