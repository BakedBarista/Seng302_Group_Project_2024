package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.UniqueConstraint;

/**
 * A tag on one or more gardens.
 */
@Entity
public class Tag {
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
}
