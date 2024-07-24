package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.util.stream.Stream;

public class PlantServiceTest {
    private static PlantService plantService;

    @BeforeAll
    static void setup() {
        plantService = new PlantService(
                Mockito.mock(PlantRepository.class),
                Mockito.mock(GardenRepository.class)
        );
    }

    static Stream<Arguments> provideInvalidFiles() {
        return Stream.of(
                Arguments.of(new MockMultipartFile("file", "invalid.gif", "image/gif", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.html", "text/html", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.txt", "text/plain", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[10 * 2024 * 1024]))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFiles")
    void givenImageIsInvalid_WhenValidateImage_ReturnTrue(MockMultipartFile file) {
        boolean result = plantService.validateImage(file);

        Assertions.assertFalse(result);
    }

    static Stream<Arguments> provideValidFiles() {
        return Stream.of(
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.svg", "image/svg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.jpg", "image/jpg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.jpeg", "image/jpeg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[10 * 1024 * 1024 - 1]))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidFiles")
    void givenImageIsValid_WhenValidateImage_ReturnTrue(MockMultipartFile file) {
        boolean result = plantService.validateImage(file);

        Assertions.assertTrue(result);
    }
}