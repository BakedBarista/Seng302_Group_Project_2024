package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.*;

/**
 * Data transfer object for the plant history form
 */
public class PlantHistoryItemDTO{


    private String image;

    @Size(max = 512, message = INVALID_GARDEN_DESCRIPTION)
    @Pattern(regexp = DESCRIPTION_REGEX , message = INVALID_GARDEN_DESCRIPTION)
    private String description;
    
    public void setDescription() {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}   
