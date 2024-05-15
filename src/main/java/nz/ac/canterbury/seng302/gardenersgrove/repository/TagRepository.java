package nz.ac.canterbury.seng302.gardenersgrove.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

/**
 * Tag repository accessor
 */

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    List<Tag> findAll();
}
