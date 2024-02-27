package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for GardenFormResults
 */
@Service
public class GardenFormService {
    private GardenFormRepository formRepository;

    public GardenFormService(GardenFormRepository formRepository) {this.formRepository = formRepository;}

    /**
     * Gets all GardenFormResults from persistence
     * @return all GardenFormResults currently saved in persistence
     */
    public List<Garden> getFormResults() { return formRepository.findAll();}

    /**
     * Adds a gardenFormResult to persistence
     * @param gardenFormResult object to persist
     * @return the saved gardenFormResult object
     */
    public Garden addGardenFormResult(Garden gardenFormResult) { return formRepository.save(gardenFormResult);}

    /**
     * Get garden details by id
     * @param id garden id used to retrieve data
     * @return the object of given id
     */
    public Optional<Garden> getGarden(long id) {return formRepository.findById(id);}
}
