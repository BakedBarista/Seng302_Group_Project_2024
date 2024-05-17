package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

/**
 * A tag on one or more gardens.
 */
@Entity
public class Tag implements Comparable<Tag> {
    @Id
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Garden> gardens;

    /**
     * Default constructor for JPA.
     */
    public Tag() {
    }

    /**
     * Create a new tag with the given name.
     *
     * @param name The name of the tag.
     */
    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Tag other) {
        int result = name.compareToIgnoreCase(other.name);
        if (result == 0) {
            // If they are equal case-insensitively, compare case-sensitively
            result = name.compareTo(other.name);
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return this.compareTo((Tag) other) == 0;
    }
}
