package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DeleteFavouritePlantController {

    private final Logger logger = LoggerFactory.getLogger(DeleteFavouritePlantController.class);

    @DeleteMapping("/users/delete-favourite-plant/{id}")
    public void deleteFavouritePlant(
            @PathVariable("id") Long plantId,
            Authentication authentication
    ) {
        logger.info("DELETE /users/delete-favourite-plant/{}", plantId);
    }
}
