package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class FavouriteGardenController  {

    private GardenService gardenService;
    private GardenUserService gardenUserService;



    public FavouriteGardenController(GardenService gardenService, GardenUserService gardenUserService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    @PostMapping("/users/edit-public-profile/favourite-garden")
    public ResponseEntity<List<Garden>> favouriteGarden(@RequestParam(name="search",required = false,defaultValue = "") String searchTerm) {
        System.out.println(searchTerm);
        GardenUser currentUser = gardenUserService.getCurrentUser();
        List <Garden> publicGardens = gardenService.getPublicGardensByOwnerId(currentUser).stream().filter(garden-> garden.getName().toLowerCase().contains(searchTerm.toLowerCase())).toList();
        System.out.println(publicGardens);



        return ResponseEntity.ok(publicGardens);
    }

    @PutMapping("/users/edit-public-profile/favourite-garden")
    public ResponseEntity<String> updateFavouriteGarden(@RequestParam(name="id") Long id) {
        Optional<Garden> garden = gardenService.getGardenById(id);
        GardenUser currentUser = gardenUserService.getCurrentUser();
        Garden existingGarden = new Garden();
        if (garden.isPresent()) {
            existingGarden = garden.get();
            gardenService.addFavouriteGarden(currentUser.getId(), existingGarden.getId());
        }
        System.out.println(currentUser.getFavoriteGarden());
        return ResponseEntity.ok("Favourite Garden Updated");
    }

}
