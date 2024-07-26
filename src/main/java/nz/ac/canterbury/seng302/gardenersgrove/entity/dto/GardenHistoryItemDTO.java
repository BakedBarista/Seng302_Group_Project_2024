package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import java.time.LocalDate;

/**
 * DTO for storing action and date of a plant history item
 */
public class GardenHistoryItemDTO implements Comparable<GardenHistoryItemDTO> {

    private LocalDate date;

    private Action action;

    public GardenHistoryItemDTO(LocalDate date, Action action) {
        this.date = date;
        this.action = action;
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
