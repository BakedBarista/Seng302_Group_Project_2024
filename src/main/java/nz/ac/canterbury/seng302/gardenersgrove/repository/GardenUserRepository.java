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
    Optional<GardenUser> findById(long id);
    List<GardenUser> findAll();

    @Query("SELECT u FROM GardenUser u WHERE u.email = ?1")
    Optional<GardenUser> findByEmail(String email);
}
