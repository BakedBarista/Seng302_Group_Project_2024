package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenFormResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GardenFormResult repository accessor
 */

@Repository
public interface GardenFormRepository extends CrudRepository<GardenFormResult, Long> {
    Optional<GardenFormResult> findById(long id);
    List<GardenFormResult> findAll();
}
