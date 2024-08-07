package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Plant history repository accessor
 */
@Repository
public interface PlantHistoryRepository extends CrudRepository<PlantHistoryItem, Long> {
    List<PlantHistoryItem> findByPlantId(long plantId);
    List<PlantHistoryItem> findByPlantIdAndTimestamp(long plantId, LocalDate timestamp);

}
