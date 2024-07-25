package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.GARDEN_REGEX;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.POSITIVE_WHOLE_NUMBER_REGEX;

/**
 * Acts as a skeleton for Plant and PlantDTO, which share a lot of the same fields and methods
 */
@MappedSuperclass
public abstract class BasePlant {

    @NotBlank(message = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Pattern(regexp = GARDEN_REGEX, message = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Column(nullable = false)
    protected String name;

    @Pattern(regexp = POSITIVE_WHOLE_NUMBER_REGEX, message = "Plant count must be a positive whole number")
    @Column(nullable = false)
    protected String count;

    @Size(min = 0, max = 512, message = "Plant description must be less than 512 characters")
    @Column(nullable = false, length = 512)
    protected String description;


    @ManyToOne
    @JoinColumn
    protected Garden garden;

    @Enumerated(EnumType.STRING)
    private BasePlant.PlantStatus status;

    // Getters and setters

    public String getName() {
        return name;
    }

    public enum PlantStatus {
        NOT_GROWING,
        CURRENTLY_GROWING,
        HARVESTED,
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsHarvested(PlantStatus status) {
         this.status = status;
    }

    public PlantStatus getIsHarvested() {
        return status;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    /**
     * Removes extraneous information (such as weird decimals) from the plant object.
     *
     * Should be called before persisting changes to the DB.
     */
    public void normalize() {
        if (this.count != null && this.count.contains(".")) {
            this.count = this.count.substring(0, this.count.indexOf("."));
        }
    }
}