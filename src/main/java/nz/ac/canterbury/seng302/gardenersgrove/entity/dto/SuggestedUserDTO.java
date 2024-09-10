package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

public class SuggestedUserDTO {
    private Long id;
    private String fullName;
    private String description;

    private String favouriteGardenHtml;

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

    public String getDescription() {
        return description;
    }

    public String getFavouriteGardenHtml() {
        return favouriteGardenHtml;
    }

    public void setFavouriteGardenHtml(String favouriteGardenHtml) {
        this.favouriteGardenHtml = favouriteGardenHtml;
    }
}
