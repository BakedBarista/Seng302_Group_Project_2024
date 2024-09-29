package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling the birth flowers of users
 */
@Service
public class BirthFlowerService {

    private final Logger logger = LoggerFactory.getLogger(BirthFlowerService.class);

    private final ObjectMapper objectMapper;
    private final Map<Integer, List<String>> monthFlowersMap;

    @Autowired
    public BirthFlowerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.monthFlowersMap = loadConfig();
    }

    /**
     * Loads default data from saved JSON
     * @return map of month as integer and list of flower names
     */
    public Map<Integer, List<String>> loadConfig() {
        Map<Integer, List<String>> flowerMap = new HashMap<>();

        logger.info("loading local plant information for LocalPlantDataService.class...");
        Resource resource = new ClassPathResource("birth_flowers.json");
        try {
            File file = resource.getFile();
            flowerMap = objectMapper.readValue(file, new TypeReference<>(){});
        } catch (IOException e) {
            logger.error("failed to load default birth flower data");
        }
        return flowerMap;
    }

    /**
     * Get the list of possible flowers for a birth month
     * @param date users birthdate (usually)
     * @return list of possible flowers
     */
    public List<String> getFlowersByMonth(LocalDate date) {
        logger.info("DOB is {}", date);
        if (date == null) {
            return Collections.emptyList();
        }
        Integer month = date.getMonthValue();
        return monthFlowersMap.get(month);
    }

    /**
     * Get the default birth flower for a date
     * @param date users birthdate (usually)
     * @return single flower that is default
     */
    public String getDefaultBirthFlower(LocalDate date) {
        if (date == null) {
            return null;
        }
        List<String> flowers = getFlowersByMonth(date);
        if (flowers.isEmpty()) {
            return null;
        }
        return flowers.get(0);
    }
}
