package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.entity.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantInfoDTOTest {
    private static Validator validator;
    private PlantInfoDTO plantInfoDTO;

    @BeforeAll
    static void setUpAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {
        plantInfoDTO = new PlantInfoDTO("Tomato","A red fruit vegetable","Q123","https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg");
    }

    @Test
    void whenLabelIsValid_thenNoErrors() {
        plantInfoDTO.setLabel("Apple");

        Set<ConstraintViolation<PlantInfoDTO>> errors = validator.validate(plantInfoDTO);

        assertTrue(errors.isEmpty());
    }

    @Test
    void whenDescriptionIsValid_thenNoErrors() {
        plantInfoDTO.setDescription("A sweet fruit");

        Set<ConstraintViolation<PlantInfoDTO>> errors = validator.validate(plantInfoDTO);

        assertTrue(errors.isEmpty());
    }

    @Test
    void whenPlantIDIsValid_thenNoErrors() {
        plantInfoDTO.setId("Q345");

        Set<ConstraintViolation<PlantInfoDTO>> errors = validator.validate(plantInfoDTO);

        assertTrue(errors.isEmpty());
    }

    @Test
    void whenImagePathSet_thenNoErrors() {
        plantInfoDTO.setImage("https://commons.wikimedia.org/wiki/Special:FilePath/Apple.jpg");

        Set<ConstraintViolation<PlantInfoDTO>> errors = validator.validate(plantInfoDTO);

        assertTrue(errors.isEmpty());
    }
}
