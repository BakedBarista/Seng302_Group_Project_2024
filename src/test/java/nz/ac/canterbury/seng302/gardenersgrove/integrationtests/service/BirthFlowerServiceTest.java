package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class BirthFlowerServiceTest {
    @Autowired
    BirthFlowerService flowerService;

    @ParameterizedTest
    @CsvSource({
            "1, Carnation;Snowdrop",
            "2, Violet;Iris;Primrose",
            "3, Daffodil;Jonquil",
            "4, Daisy;Sweet Pea",
            "5, Lily of the Valley;Hawthorn",
            "6, Rose;Honeysuckle",
            "7, Larkspur;Water Lily",
            "8, Gladiolus;Poppy",
            "9, Aster;Morning Glory",
            "10, Marigold;Cosmos",
            "11, Chrysanthemum;Peony",
            "12, Holly;Poinsettia;Narcissus"})
    void whenLoadConfig_thenContainsCorrectData(Integer month, String encodedList) {
        Map<Integer, List<String>> result = flowerService.loadConfig();

        List<String> list = List.of(encodedList.split(";"));
        Assertions.assertEquals(list.size(), result.get(month).size());
        for(String string: list){
            Assertions.assertTrue(result.get(month).contains(string));
        }
    }

    @Test
    void givenBirthMonthProvide_thenReturnBirthMonthFlowers() {
        int month = 1;
        List<String> result = flowerService.getFlowersByMonth(LocalDate.of(2000,month,1));
        Assertions.assertEquals(result.size(), 2);
        Assertions.assertTrue(result.contains("Carnation"));
        Assertions.assertTrue(result.contains("Snowdrop"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10,11,12})
    void whenGetFlowersByMonth_thenReturnAtLeastTwo(int month) {
        List<String> result = flowerService.getFlowersByMonth(LocalDate.of(2000,month,1));
        Assertions.assertTrue(result.size() >= 2);
    }

}
