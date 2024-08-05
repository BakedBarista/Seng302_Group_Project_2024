package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHarvestedDateDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;

import nz.ac.canterbury.seng302.gardenersgrove.service.ThymeLeafDateFormatter;


import nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling API requests related to updating and setting plant status
 */
@RestController
@RequestMapping("/api")
public class PlantStatusApiController {
    private final PlantService plantService;
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    private static final String ERRORS = "errors";

    private final ThymeLeafDateFormatter dateFormatter = new ThymeLeafDateFormatter();


    public PlantStatusApiController(PlantService plantService) {
        this.plantService = plantService;
    }

    /**
     * Update the status of a plant and sets it's harvested date
     * @param id plant id
     * @param newStatus new status
     * @return response entity
     */
    @PutMapping("/plants/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePlantStatus(@PathVariable("id") Long id, @RequestParam("status") BasePlant.PlantStatus newStatus) {
        Optional<Plant> plant = plantService.getPlantById(id);
        if (plant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Plant existingPlant = plant.get();
        existingPlant.setStatus(newStatus);

        plantService.save(existingPlant);
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, newStatus.name());

        response.put("harvestedDate", existingPlant.getHarvestedDate());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Update the harvested date of a plant
     * @param id plant id
     * @param request request body
     * @param result binding result
     * @return response entity
     */
    @PostMapping("/plants/{id}/harvest-date")
    public ResponseEntity<Map<String, Object>> updateHarvestedDate(@PathVariable("id") Long id,
                                                                   @RequestBody @Valid PlantHarvestedDateDTO request,
                                                                   BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put(STATUS, ERROR);
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            response.put(ERRORS, errors);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Plant> plant = plantService.getPlantById(id);
        if (plant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Plant existingPlant = plant.get();
        String harvestedDateString = request.getHarvestedDate();


        try {
            LocalDate harvestedDate = LocalDate.parse(harvestedDateString);
            if (existingPlant.getPlantedDate() !=null && existingPlant.getPlantedDate().isBefore(harvestedDate)) {
                existingPlant.setHarvestedDate(harvestedDate);
            } else if(existingPlant.getPlantedDate() == null) {
                existingPlant.setHarvestedDate(harvestedDate);
            }
            else {
                Map<String, Object> response = new HashMap<>();
                response.put(STATUS, ERROR);
                response.put(ERRORS, List.of("Harvested date must be after planted date"));
                return ResponseEntity.badRequest().body(response);
            }


        } catch (DateTimeParseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(STATUS,ERROR);
            response.put(ERRORS, List.of("Invalid date format"));
            return ResponseEntity.badRequest().body(response);
        }

        plantService.save(existingPlant);

        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, "success");
        response.put("harvestedDate", dateFormatter.format(existingPlant.getHarvestedDate(), DateTimeFormats.NZ_FORMAT_DATE));

        return ResponseEntity.ok().body(response);
    }


}
