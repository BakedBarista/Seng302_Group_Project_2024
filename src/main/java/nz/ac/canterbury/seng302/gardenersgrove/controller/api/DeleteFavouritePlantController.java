package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Class purely for dealing with the API calls of deleting favourite plants
 *
 * - maybe could be more generalized for managing favourite plants if needed
 */
@RestController
@RequestMapping("/api")
public class DeleteFavouritePlantController {

    private final GardenUserService gardenUserService;

    @Autowired
    public DeleteFavouritePlantController(GardenUserService gardenUserService) {
        this.gardenUserService = gardenUserService;
    }

    private final Logger logger = LoggerFactory.getLogger(DeleteFavouritePlantController.class);

    /**
     * Method to be called by fetch in JS to remove favourite plant from the user's
     * list of favourite plants
     * @param plantId id of the plant
     * @param authentication authentication for the user
     */
    @DeleteMapping("/users/delete-favourite-plant")
    public void deleteFavouritePlant(
            @RequestBody Map<String, Long> requestBody,
            Authentication authentication
    ) {

        Long plantId = requestBody.get("plantId");
        System.out.println(plantId);

        logger.info("DELETE /users/delete-favourite-plant/{}", plantId);

        Long userId = (Long) authentication.getPrincipal();
        gardenUserService.removeFavouritePlant(userId, plantId);

    }
}
