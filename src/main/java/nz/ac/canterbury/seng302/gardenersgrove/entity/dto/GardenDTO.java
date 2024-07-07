package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.persistence.Column;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidEuropeanDecimal;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BaseGarden;

public class GardenDTO extends BaseGarden {

    @ValidEuropeanDecimal(message = "Garden size must be a valid positive number (only allows numbers and a single period or comma)")
    @Column(nullable = true)
    private String size;

    public GardenDTO() {}

    /**
     * construct GardenDTO
     * @param gardenName name of the garden of the garden
     * @param streetNumber street number (can be string such as 12A, 12B)
     * @param streetName street name of the garden
     * @param suburb suburb of the garden
     * @param city city of the garden
     * @param country country of the garden
     * @param postCode postcode of the garden
     * @param lat latitude of the garden
     * @param lon longitude of the garden
     * @param description short description made by the owner
     * @param gardenSize size of the garden, can be a decimal
     */
    public GardenDTO(String gardenName, String streetNumber, String streetName, String suburb, String city, String country,
                  String postCode, double lat, double lon, String description, String gardenSize) {
        this.setName(gardenName);
        this.setStreetNumber(streetNumber);
        this.setStreetName(streetName);
        this.setSuburb(suburb);
        this.setCity(city);
        this.setCountry(country);
        this.setPostCode(postCode);
        this.setLat(lat);
        this.setLon(lon);
        this.setDescription(description);
        this.size = gardenSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size.replace(',', '.');
    }

    @Override
    public String toString() {
        return super.toString() + " additionally, size='" + size + "'";
    }
}
