package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalPlantDataService {
    Logger logger = LoggerFactory.getLogger(LocalPlantDataService.class);

    private List<PlantInfoDTO> localPlants = new ArrayList<>();
    private final List<String> plantNames;
    private final ObjectMapper objectMapper;
    private final StringDistanceService stringDistanceService;

    /**
     * Loads default data from a text file (label and description of plants),
     * this is used by this class when we want to get data for an input string
     */
    private void loadDefault() {
        logger.info("loading local plant information for LocalPlantDataService.class...");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("local_plant_data.json")) {
            localPlants = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, PlantInfoDTO.class)
            );
        } catch (IOException e) {
            logger.error("LocalPlantDataService failed to load default local plant data");
        }

        if (!localPlants.isEmpty()) {
            logger.info("successfully loaded local plant information for LocalPlantDataService.class");
        }
    }

    public LocalPlantDataService(ObjectMapper objectMapper, StringDistanceService stringDistanceService) {
        this.objectMapper = objectMapper;
        this.stringDistanceService = stringDistanceService;

        loadDefault();
        this.plantNames = localPlants.stream().map(PlantInfoDTO::getLabel).map(String::toLowerCase).toList();
    }

    /**
     * Gets the matching PlantInfoDTO for the plant name that is deemed similar.
     * @param plantName plant name to find PlantInfoDTO for
     * @return PlantInfoDTO that matches name (label)
     */
    public PlantInfoDTO getMatchingPlantInfoFromFile(String plantName) {
        logger.info("adding similar plant info for " + plantName);
        Optional<PlantInfoDTO> plant = localPlants
                .stream()
                .filter(plantInfoDTO -> plantInfoDTO.getLabel().equalsIgnoreCase(plantName))
                .findFirst();

        return plant.orElse(null);
    }

    /**
     * Send request to API to get similar plants when no results are found
     * @param userSearch plant name to be searched
     * @return JsonNode with a list of PlantInfoDTOs
     */
    public List<PlantInfoDTO> getSimilarPlantInfo(String userSearch) {
        logger.info("API returned no results - attempting to find similar plant info from local sources");

        List<String> similarPlantNames = stringDistanceService.getSimilarStrings(plantNames, userSearch);

        List<PlantInfoDTO> plantInfoList = new ArrayList<>();
        for (String similarPlantName : similarPlantNames) {
            plantInfoList.add(getMatchingPlantInfoFromFile(similarPlantName));
        }

        return plantInfoList;
    }
}
