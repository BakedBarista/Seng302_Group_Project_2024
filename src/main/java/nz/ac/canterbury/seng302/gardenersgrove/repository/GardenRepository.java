package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GardenFormResult repository accessor
 */

@Repository
public interface GardenRepository extends CrudRepository<Garden, Long> {
    Optional<Garden> findById(long id);
    List<Garden> findAll();

    Page<Garden> findByIsPublicTrue(Pageable pageable);

    List<Garden> findByOwnerId(Long owner_id);

    @Query("SELECT p FROM Garden p WHERE p.owner = ?1 AND p.isPublic = TRUE")
        List<Garden> findUserPublicGarden(GardenUser owner_id);

    @Query("SELECT p FROM Garden p WHERE p.owner =?1 and p.isPublic=FALSE")
    List<Garden> findUserPrivateGarden(GardenUser owner_id);
}
