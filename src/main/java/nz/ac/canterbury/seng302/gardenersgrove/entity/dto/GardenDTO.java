package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidGardenSizeString;
import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidEuropeanDecimal;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BaseGarden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.INVALID_GARDEN_SIZE_MESSAGE;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.MAX_GARDEN_SIZE_LENGTH_MESSAGE;


public class GardenDTO extends BaseGarden {

    @ValidEuropeanDecimal(message = INVALID_GARDEN_SIZE_MESSAGE)
    @Size(max = 50, message = MAX_GARDEN_SIZE_LENGTH_MESSAGE)
    @ValidGardenSizeString()
    @Column(nullable = true)
    private String size;

    private String submissionToken;

    
    public GardenDTO() {
        super();
    }

    /**
     * construct GardenDTO object with Garden
     * checks that garden is not null before doing anything
     * @param garden Garden object to copy
     */
    public GardenDTO(Garden garden) {
        super(garden);
        if (garden != null) {
            this.size = String.valueOf(garden.getSize());
        }
    }

    /**
     * construct GardenDTO object with params
     * @param name name of the garden of the garden
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
    public GardenDTO(String name, String streetNumber, String streetName, String suburb, String city, String country,
                     String postCode, Double lat, Double lon, String description, String gardenSize, String submissionToken, byte[] gardenImage, 
                     String gardenImageContentType) {
        super(name, streetNumber, streetName, suburb, city, country, postCode, lat, lon, description, gardenImage, gardenImageContentType);
        this.size = gardenSize;
        this.submissionToken = submissionToken;
        this.gardenImage = gardenImage;
        this.gardenImageContentType = gardenImageContentType;
    }

    public Garden toGarden() {
        Double size = null;
        if (this.size == null || this.size.trim().isEmpty()) {
            this.size = null;
        } else {
            try {
                size = Double.parseDouble(this.size);
            } catch (NumberFormatException e) {
                this.size = null;
            }
        }

        return new Garden(this, size);
    }

    public String getSize() {
        return size;
    }

    /**
     * handles setting size, replacing comma with period
     * @param size
     */
    public void setSize(String size) {
        this.size = size.replace(',', '.');
    }

    public String getSubmissionToken() {
        return submissionToken;
    }

    public void setSubmissionToken(String submissionToken) {
        this.submissionToken = submissionToken;
    }
}
