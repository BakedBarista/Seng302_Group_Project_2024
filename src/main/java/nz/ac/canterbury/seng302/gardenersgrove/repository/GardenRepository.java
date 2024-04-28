package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT g FROM Garden g WHERE ((g.name ILIKE %?1%) OR EXISTS (SELECT p FROM Plant p WHERE p.garden = g AND p.name ILIKE %?1%)) AND g.isPublic")
    List<Garden> findAllThatContainQuery(String query);

    @Query("SELECT g FROM Garden g WHERE ((g.name ILIKE %?1%) OR EXISTS (SELECT p FROM Plant p WHERE p.garden = g AND p.name ILIKE %?1%)) AND g.isPublic")
    Page<Garden> findPageThatContainsQuery(String query, Pageable pageable);
}
