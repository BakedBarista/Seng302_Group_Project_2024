package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidDate;

public class PlantHarvestedDateDTO {
    @ValidDate
    private String harvestedDate;


    public PlantHarvestedDateDTO(String harvestedDate) {
        this.harvestedDate = harvestedDate;
    }

    public PlantHarvestedDateDTO() {
    }

    public String getHarvestedDate() {
        return harvestedDate;
    }

    public void setHarvestedDate(String harvestedDate) {
        this.harvestedDate = harvestedDate;
    }
}
