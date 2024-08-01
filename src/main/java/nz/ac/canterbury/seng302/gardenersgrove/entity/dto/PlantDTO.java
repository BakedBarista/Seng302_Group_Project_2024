package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import nz.ac.canterbury.seng302.gardenersgrove.entity.BasePlant;
import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidDate;

import java.time.LocalDate;


/**
 * DTO for plants
 */
public class PlantDTO extends BasePlant {

    @ValidDate()
    private String plantedDate;

    public PlantDTO(String name, String count, String description, String plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public PlantDTO() {
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public LocalDate getParsedPlantedDate() {
        if (plantedDate != null && !plantedDate.isEmpty()) {
            return LocalDate.parse(plantedDate);
        } else {
            return null;
        }
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    @Override
    public String toString() {
        return "PlantDTO{" +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description ='" + this.description + '\'' +
                ", plantedDate ='" + this.plantedDate + '\'' +
                '}';
    }
}