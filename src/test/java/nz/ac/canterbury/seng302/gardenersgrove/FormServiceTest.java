package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.FormResult;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FormService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(FormService.class)
public class FormServiceTest {

    @Test
    public void simpleTest() {
        FormService formService = new FormService(new FormRepository() {
            @Override
            public Optional<FormResult> findById(long id) {
                return Optional.empty();
            }

            @Override
            public List<FormResult> findAll() {
                return null;
            }

            @Override
            public <S extends FormResult> S save(S entity) {
                // assume there is some modification at the service layer that we check here instead of just the same values
                Assertions.assertEquals(entity.getName(), "John");
                Assertions.assertEquals(entity.getLanguage(), "Python");
                return entity;
            }

            @Override
            public <S extends FormResult> Iterable<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public Optional<FormResult> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public Iterable<FormResult> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(FormResult entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends FormResult> entities) {

            }

            @Override
            public void deleteAll() {

            }
        });
        formService.addFormResult(new FormResult("John", "Python"));
    }

//    @Autowired
//    private FormService formService;

    @Autowired
    private FormRepository formRepository;

    @Test
    public void simpleTest2() {
        FormService formService = new FormService(formRepository);
        FormResult result = formService.addFormResult(new FormResult("John", "Python"));
        Assertions.assertEquals(result.getName(), "John");
        Assertions.assertEquals(result.getLanguage(), "Python");
    }
}
