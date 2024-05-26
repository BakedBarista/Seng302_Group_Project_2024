package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.entity.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;

public class RegisterDTOTest {
    private static Validator validator;
    private RegisterDTO registerDTO;

    @BeforeAll
    static void setUpAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setEmail("jdo71@uclive.ac.nz");
    }

    @Test
    void whenFirstNameIsSimple_thenNoErrors() {
        registerDTO.setFname("John");

        Set<ConstraintViolation<RegisterDTO>> errors = validator.validate(registerDTO);

        assertTrue(errors.isEmpty());
    }

    @Test
    void whenFirstNameHyphenatedAndMultipleWords_thenNoErrors() {
        registerDTO.setFname("John-John John");

        Set<ConstraintViolation<RegisterDTO>> errors = validator.validate(registerDTO);

        assertTrue(errors.isEmpty());
    }

    @Test
    void whenFirstNameIsJustHyphen_thenErrors() {
        registerDTO.setFname("-");

        Set<ConstraintViolation<RegisterDTO>> errors = validator.validate(registerDTO);

        assertTrue(!errors.isEmpty());
    }

    @Test
    void whenFirstNameEmpty_thenErrors() {
        registerDTO.setFname("");

        Set<ConstraintViolation<RegisterDTO>> errors = validator.validate(registerDTO);

        assertTrue(!errors.isEmpty());
    }
}
