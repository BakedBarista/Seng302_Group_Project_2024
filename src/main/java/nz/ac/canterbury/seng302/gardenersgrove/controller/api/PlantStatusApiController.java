package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PlantStatusApiController {
    private final PlantService plantService;

    public PlantStatusApiController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PutMapping("/plants/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePlantStatus(@PathVariable("id") Long id, @RequestParam("status") BasePlant.PlantStatus newStatus) {
        Optional<Plant> plant = plantService.getPlantById(id);
        if (plant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Plant existingPlant = plant.get();
        if (newStatus == BasePlant.PlantStatus.HARVESTED) {
            existingPlant.setHarvestedDate(LocalDate.now());
        } else {
            existingPlant.setHarvestedDate(null);
        }
        existingPlant.setStatus(newStatus);

        plantService.save(existingPlant);
        Map<String, Object> response = new HashMap<>();
        response.put("status", newStatus.name());
        if (existingPlant.getHarvestedDate() != null) {
            response.put("harvestedDate", existingPlant.getHarvestedDate());
        }

        return ResponseEntity.ok().body(response);
    }
}
