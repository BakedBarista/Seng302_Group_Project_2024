package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class FavouritePlantDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private byte[] image;

    public FavouritePlantDTO(Long id, String name, byte[] image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "FavouritePlantDTO{name='" + name + "'}";
    }
}