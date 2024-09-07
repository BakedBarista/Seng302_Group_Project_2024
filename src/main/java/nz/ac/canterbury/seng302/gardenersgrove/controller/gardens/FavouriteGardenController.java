package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;


import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling requests related to favourite gardens
 */
@RestController
public class FavouriteGardenController  {

    Logger logger = LoggerFactory.getLogger(FavouriteGardenController.class);

    private GardenService gardenService;
    private GardenUserService gardenUserService;



    public FavouriteGardenController(GardenService gardenService, GardenUserService gardenUserService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    /**
     * Get all public gardens that the user can favourite
     * @param searchTerm search term to filter gardens by
     * @return list of public gardens
     */
    @PostMapping("/users/edit-public-profile/favourite-garden")
    public ResponseEntity<List<Garden>> favouriteGarden(@RequestParam(name="search", required = false, defaultValue = "") String searchTerm) {
        GardenUser currentUser = gardenUserService.getCurrentUser();
        List<Garden> publicGardens = gardenService.getPublicGardensByOwnerId(currentUser).stream()
                .filter(garden -> garden.getName() != null && garden.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
        return ResponseEntity.ok(publicGardens);
    }

    /**
     * Update the favourite garden of the current user
     * @param id garden id to favourite
     * @param model model to add the favourite garden to
     * @return response entity
     * @throws JsonProcessingException if the json is invalid
     */
    @PutMapping("/users/edit-public-profile/favourite-garden")
    public ResponseEntity<String> updateFavouriteGarden(@RequestBody String id, Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        long gardenId;
        try {
            Map<String, Object> map = mapper.readValue(id, Map.class);
            gardenId = Long.parseLong(map.get("id").toString());
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid json format");
        }
        Optional<Garden> garden = gardenService.getGardenById(gardenId);
        GardenUser currentUser = gardenUserService.getCurrentUser();
        if (garden.isPresent()) {
            Garden existingGarden = garden.get();
            gardenService.addFavouriteGarden(currentUser.getId(), existingGarden.getId());
            model.addAttribute("favouriteGarden", existingGarden);
        }


        return ResponseEntity.ok("Favourite Garden Updated");
    }

}
