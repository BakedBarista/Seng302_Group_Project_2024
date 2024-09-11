package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Class purely for dealing with the API calls of deleting favourite plants
 */
@RestController
@RequestMapping("/api")
public class DeleteFavouritePlantController {

    private final GardenUserService gardenUserService;

    private final PlantService plantService;

    @Autowired
    public DeleteFavouritePlantController(GardenUserService gardenUserService, PlantService plantService) {
        this.gardenUserService = gardenUserService;
        this.plantService = plantService;
    }

    private final Logger logger = LoggerFactory.getLogger(DeleteFavouritePlantController.class);

    /**
     * Method to be called by fetch in JS to remove favourite plant from the user's
     * list of favourite plants
     * @param requestBody String, Long mapping of "plantId": plantId
     * @param authentication authentication for the user
     * @return true if plant was deleted
     */
    @Transactional
    @DeleteMapping("/users/delete-favourite-plant")
    public Boolean deleteFavouritePlant(
            @RequestBody Map<String, Long> requestBody,
            Authentication authentication
    ) {
        Long plantId = requestBody.get("plantId");
        logger.info("DELETE /users/delete-favourite-plant");

        Long userId = (Long) authentication.getPrincipal();
        plantService.updatePlantUnfavourited(plantId);
        return gardenUserService.removeFavouritePlant(userId, plantId);
    }
}
