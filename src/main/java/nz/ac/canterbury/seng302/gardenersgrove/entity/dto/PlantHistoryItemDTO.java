package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.DESCRIPTION_REGEX;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.INVALID_PLANT_RECORD_DESCRIPTION;

/**
 * Data transfer object for the plant history form
 */
public class PlantHistoryItemDTO {


    @Size(max = 512, message = INVALID_PLANT_RECORD_DESCRIPTION)
    @Pattern(regexp = DESCRIPTION_REGEX , message = INVALID_PLANT_RECORD_DESCRIPTION)
    private String description;




    public PlantHistoryItemDTO(String description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    public String getDescription() {
        return description;
    }


}   
