package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import java.time.LocalDate;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;

/**
 * DTO for storing action and date of a plant history item
 */
public class GardenHistoryItemDTO implements Comparable<GardenHistoryItemDTO> {

    private Plant plant;

    private LocalDate date;

    private Action action;

    public GardenHistoryItemDTO(Plant plant, LocalDate date, Action action) {
        this.plant = plant;
        this.date = date;
        this.action = action;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public int compareTo(GardenHistoryItemDTO other) {
        return this.date.compareTo(other.date);
    }

    public enum Action {
        PLANTED,
        //TODO
        HARVESTED,
    }
}
