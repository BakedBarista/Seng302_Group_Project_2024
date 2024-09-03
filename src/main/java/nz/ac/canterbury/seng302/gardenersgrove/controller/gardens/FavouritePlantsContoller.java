package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.exceptions.JsonProcessingException;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FavouritePlantsContoller {

    private final Logger logger = LoggerFactory.getLogger(PublicProfileController.class);

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
    public ResponseEntity<String> updateFavouritePlant(@RequestBody Map<String, Long> payload, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long plantId = payload.get("id");

        try {
            plantService.updateFavouritePlant(userId, plantId);
            return ResponseEntity.ok("Favourite plant updated successfully");
        } catch (Exception e) {
            logger.error("Error updating favourite plant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating favourite plant");
        }
    }
}
