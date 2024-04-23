package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GardenUser repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface GardenUserRepository extends CrudRepository<GardenUser, Long> {
    /**
     * Retrieves a GardenUser by its ID.
     *
     * @param id The ID of the GardenUser to retrieve
     * @return An Optional containing the GardenUser if found, or empty if not found
     */
    Optional<GardenUser> findById(long id);

    /**
     * Retrieves all GardenUsers.
     *
     * @return A list of all GardenUsers
     */
    List<GardenUser> findAll();

    /**
     * Retrieves a GardenUser by its email address.
     *
     * @param email The email address of the GardenUser to retrieve
     * @return An Optional containing the GardenUser if found, or empty if not found
     */
    @Query("SELECT u FROM GardenUser u WHERE u.email = ?1")
    Optional<GardenUser> findByEmail(String email);

    @Query("SELECT p FROM GardenUser p WHERE LOWER(p.fname)= LOWER(?1) or LOWER(p.email)= LOWER(?1)")
    List<GardenUser> findBySearch(String name);
}
