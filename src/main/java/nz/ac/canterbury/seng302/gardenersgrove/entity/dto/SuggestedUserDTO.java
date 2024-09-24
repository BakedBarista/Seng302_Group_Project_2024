package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

public class SuggestedUserDTO {
    private Long id;
    private String fullName;
    private int compatibility;
    private String description;

    private String favouriteGardenHtml;
    private String favouritePlantsHtml;
    public SuggestedUserDTO() {
    }
    public SuggestedUserDTO(GardenUser user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.description = user.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setCompatibility(int compatibility) {
        this.compatibility = compatibility;
    }

    public int getCompatibility() {
        return compatibility;
    }

    public String getDescription() {
        return description;
    }

    public String getFavouriteGardenHtml() {
        return favouriteGardenHtml;
    }

    public void setFavouriteGardenHtml(String favouriteGardenHtml) {
        this.favouriteGardenHtml = favouriteGardenHtml;
    }

    public String getFavouritePlantsHtml() {
        return favouritePlantsHtml;
    }

    public void setFavouritePlantsHtml(String favouritePlantsHtml) {
        this.favouritePlantsHtml = favouritePlantsHtml;
    }
}
