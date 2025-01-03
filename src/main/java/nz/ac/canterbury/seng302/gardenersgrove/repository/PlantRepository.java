package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;


import java.util.List;
import java.util.Optional;

/**
 * Plant repository accessor
 */

@Repository
public interface PlantRepository extends CrudRepository<Plant, Long> {
    Optional<Plant> findById(long id);

    List<Plant> findAll();

    List<Plant> findByGardenId(long gardenId);

    Optional<Plant> findByName(String plantName);


    @Query("SELECT DISTINCT p FROM Plant p WHERE p.garden IN (SELECT g FROM Garden g WHERE g.owner = :owner AND g.isPublic) AND (lower(p.name) LIKE '%' || lower(:searchTerm) || '%' OR lower(p.description) LIKE '%' || lower(:searchTerm) || '%')")
    List<Plant> findPlantsFromSearch(@Param("owner") GardenUser owner, @Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Garden g LEFT JOIN g.plants p WHERE g.owner = :gardenUser")
    List<Plant> findByGardenUser(GardenUser gardenUser);

}
