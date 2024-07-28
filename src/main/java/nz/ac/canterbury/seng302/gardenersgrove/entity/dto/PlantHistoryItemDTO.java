package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.*;

/**
 * Data transfer object for the plant history form
 */
public class PlantHistoryItemDTO {


    @Size(max = 512, message = INVALID_GARDEN_DESCRIPTION)
    @Pattern(regexp = DESCRIPTION_REGEX , message = INVALID_GARDEN_DESCRIPTION)
    private String description;

    private LocalDate timestamp;


    public PlantHistoryItemDTO(String description, LocalDate timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    public PlantHistoryItemDTO () {
    }

    public void setDescription() {
        this.description = description;
    }

    
    public String getDescription() {
        return description;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }


}   
