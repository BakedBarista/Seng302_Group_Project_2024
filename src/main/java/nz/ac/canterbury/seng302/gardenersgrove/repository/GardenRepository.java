package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    Page<Garden> findByIsPublicTrueOrderByIdDesc(Pageable pageable);

    List<Garden> findByIsPublicTrueOrderByIdDesc();

    List<Garden> findByOwnerId(Long owner_id);

    @Query("SELECT g FROM Garden g WHERE ((g.name ILIKE %?1%) OR " +
            "EXISTS (SELECT p FROM Plant p WHERE p.garden = g AND p.name ILIKE %?1%)) AND g.isPublic ORDER BY g.id DESC")
    List<Garden> findAllThatContainQuery(String query);

    @Query("SELECT g FROM Garden g WHERE ((g.name ILIKE %?1%) OR " +
            "EXISTS (SELECT p FROM Plant p WHERE p.garden = g AND p.name ILIKE %?1%)) AND g.isPublic ORDER BY g.id DESC")
    Page<Garden> findPageThatContainsQuery(String query, Pageable pageable);

    @Query("SELECT g From Garden g WHERE g.owner = ?1 AND g.isPublic")
    List<Garden> findUserPublicGarden(GardenUser owner);

    @Query("SELECT g From Garden g WHERE g.owner = ?1 AND not g.isPublic")
    List<Garden> findUserPrivateGarden(GardenUser owner);

    @Query("SELECT DISTINCT g FROM Garden g JOIN g.tags t WHERE t.name IN :tagNames")
    Page<Garden> findByTagNames(List<String> tagNames, Pageable pageable);

    @Query("SELECT g FROM Garden g " +
            "LEFT JOIN g.tags t " +
            "WHERE (g.name ILIKE %:search% OR EXISTS (SELECT p FROM Plant p WHERE p.garden = g AND p.name ILIKE %:search%)) " +
            "AND g.isPublic = true AND t.name IN :tags " +
            "GROUP BY g.id " +
            "HAVING COUNT(DISTINCT t.name) = :tagCount")
    Page<Garden> findGardensBySearchAndTags(@Param("search") String search, @Param("tags") List<String> tags, @Param("tagCount") long tagCount, Pageable pageable);
}

