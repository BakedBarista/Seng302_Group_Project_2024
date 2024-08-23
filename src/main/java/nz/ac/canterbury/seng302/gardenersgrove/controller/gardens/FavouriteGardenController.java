package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FavouriteGardenController  {

    private GardenService gardenService;
    private GardenUserService gardenUserService;



    public FavouriteGardenController(GardenService gardenService, GardenUserService gardenUserService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
    }

    @GetMapping("/users/edit-public-profile/favourite-garden")
    public ResponseEntity<List<Garden>> favouriteGarden() {
        GardenUser currentUser = gardenUserService.getCurrentUser();
        List <Garden> publicGardens = gardenService.getPublicGardensByOwnerId(currentUser);
        System.out.println(publicGardens);



        return ResponseEntity.ok().body(publicGardens);
    }



}
