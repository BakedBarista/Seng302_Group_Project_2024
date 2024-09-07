package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class FavouritePlantsContoller {

    private final Logger logger = LoggerFactory.getLogger(FavouritePlantsContoller.class);

    private final GardenUserService userService;
    private final PlantService plantService;

    public FavouritePlantsContoller(GardenUserService userService, PlantService plantService) {
        this.userService = userService;
        this.plantService = plantService;
    }

    /**
     * Gets all plant that matches the search input
     *
     * @param searchTerm search input
     * @return response entity
     */
    @PostMapping("users/edit-public-profile/search")
    public ResponseEntity<List<Map<String, Object>>> searchPlants(@RequestParam(name = "search", required = false, defaultValue = "") String searchTerm) {

        logger.info("Searching plants");

        List<Plant> allPlants = plantService.getAllPlants(userService.getCurrentUser(), searchTerm)
                .stream().toList();

        List<Map<String, Object>> response = new ArrayList<>();
        allPlants.forEach(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", p.getName());
            map.put("gardenName", p.getGarden().getName());
            map.put("image", p.getPlantImage());
            map.put("id", p.getId());
            response.add(map);
        });

        return ResponseEntity.ok(response);

    }
    @PutMapping("/users/edit-public-profile/favourite-plant")
    public ResponseEntity<String> updateFavouritePlants(@RequestBody Map<String, List<Long>> request) {
        List<Long> plantIds = request.get("ids");
        Long userId = userService.getCurrentUser().getId();

        try {
            for (Long plantId : plantIds) {
                Optional<Plant> plant = plantService.getPlantById(plantId);
                if (plant.isPresent()) {
                    Plant newPlant = plant.get();
                    userService.updateFavouritePlant(userId, newPlant);
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("Plant with ID %d not found.", plantId));
                    }
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(String.format("ERROR HERE: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
