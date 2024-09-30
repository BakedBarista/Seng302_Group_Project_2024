package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private final Map<String, String> flowerColorMap;

    @Autowired
    public BirthFlowerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.monthFlowersMap = loadConfig();
        this.flowerColorMap = loadColors();
    }

    /**
     * Loads default data from saved JSON
     * @return map of month as integer and list of flower names
     */
    public Map<Integer, List<String>> loadConfig() {
        Map<Integer, List<String>> flowerMap = new HashMap<>();

        logger.info("loading local plant information for LocalPlantDataService.class...");
        Resource resource = new ClassPathResource("birth_flowers.json");
        try (InputStream inputStream = resource.getInputStream()) {
            flowerMap = objectMapper.readValue(inputStream, new TypeReference<>(){});
            logger.info("Successfully loaded birth_flowers.json");
        } catch (IOException e) {
            logger.error("Failed to load default birth flower data", e);
        }
        return flowerMap;
    }

    /**
     * Loads colour data from saved JSON
     * @return map of flower name to hex colour
     */
    public Map<String, String> loadColors() {
        Map<String, List<String>> colorToFlowerMap = new HashMap<>();

        logger.info("loading local plant information for LocalPlantDataService.class...");
        Resource resource = new ClassPathResource("birth_flower_colors.json");
        try (InputStream inputStream = resource.getInputStream()) {
            colorToFlowerMap = objectMapper.readValue(inputStream, new TypeReference<>(){});
            logger.info("Successfully loaded birth_flowers.json");
        } catch (IOException e) {
            logger.error("Failed to load default birth flower data", e);
        }

        Map<String, String> colorMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : colorToFlowerMap.entrySet()) {
            String color = entry.getKey();
            for (String flower : entry.getValue()) {
                logger.info("flower: {}, color: {}", flower, color);
                colorMap.put(flower, color);
            }
        }

        return colorMap;
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

    /**
     * Get the colour of a flower
     * @param flower name of flower
     * @return hex colour of flower
     */
    public String getFlowerColor(String flower) {
        if (flower == null) {
            return null;
        }
        return flowerColorMap.get(flower);
    }

    /**
     * Gets all the birth flowers as a list
     * @return all the birth flowers as a list
     */
    public List<String> getAllFlowers() {
        return monthFlowersMap.values().stream().flatMap(List::stream).toList();
    }

    /**
     * Gets a json map that maps flower names to their hex colour
     * @return a json map that maps flower names to their hex colour
     */
    public String getFlowerColorsJson() {
        try {
            return objectMapper.writeValueAsString(flowerColorMap);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert flower color map to json", e);
            return "{}";
        }
    }
}
