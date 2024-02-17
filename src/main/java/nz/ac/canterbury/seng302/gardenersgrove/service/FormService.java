package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FormResult;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class FormService {
    private FormRepository formRepository;

//    @Autowired
    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }
    /**
     * Gets all FormResults from persistence
     * @return all FormResults currently saved in persistence
     */
    public List<FormResult> getFormResults() {
        return formRepository.findAll();
    }

    /**
     * Adds a formResult to persistence
     * @param formResult object to persist
     * @return the saved formResult object
     */
    public FormResult addFormResult(FormResult formResult) {
        return formRepository.save(formResult);
    }
}
