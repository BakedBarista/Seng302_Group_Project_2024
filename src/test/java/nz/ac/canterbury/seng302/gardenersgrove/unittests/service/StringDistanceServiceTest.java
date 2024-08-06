package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.StringDistanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

public class StringDistanceServiceTest {

    private static StringDistanceService stringDistanceService;

    @BeforeAll
    static void setup() {
        stringDistanceService = new StringDistanceService();
    }

    @ParameterizedTest()
    @ValueSource(strings = {"banana", "bana", "nana", "TREE"})
    void givenInputSubstringsAStringInStringsList_whenIGetSimilarStrings_thenTheMatchedStringIsReturned(String input) {
        List<String> strings = new ArrayList<>(List.of("BANANA tree"));

        List<String> result = stringDistanceService.getSimilarStrings(strings, input);

        Assertions.assertEquals(1, result.size());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"banana tree", "apple tree", "peach tree", "tree of lemons"})
    void givenInputIsSubstringByStringList_whenIGetSimilarStrings_thenTheMatchedStringIsReturned(String input) {
        List<String> strings = new ArrayList<>(List.of("banana", "apple", "peach", "lemon"));

        List<String> result = stringDistanceService.getSimilarStrings(strings, input);

        Assertions.assertEquals(1, result.size());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", "        "})
    @NullSource
    void givenInputIsTooInvalid_thenReturnEmptyList(String input) {
        List<String> strings = new ArrayList<>(List.of("banana", "apple", "peach", "lemon"));

        List<String> result = stringDistanceService.getSimilarStrings(strings, input);

        Assertions.assertTrue(result.isEmpty());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"banarna", "opple", "peechs", "lemoon", "Bell-peppa"})
    void givenInputIsCloseMatchToString_whenIGetSimilarStrings_thenTheSimilarStringIsReturned(String input) {
        List<String> strings = new ArrayList<>(List.of("banana", "apple", "peach", "lemon", "bell pepper"));

        List<String> result = stringDistanceService.getSimilarStrings(strings, input);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void givenTwoWordsWithThreeInsertions_whenIFindTheDistance_thenTheirDistanceIsReturned() {
        int distance = stringDistanceService.findDistance("xAAAxx", "AAA");

        Assertions.assertEquals(3, distance);
    }

    @Test
    void givenTwoWordsWithThreeSubstitutions_whenIFindTheDistance_thenTheirDistanceIsReturned() {
        int distance = stringDistanceService.findDistance("BCD", "xBCDxx");

        Assertions.assertEquals(3, distance);
    }

    @Test
    void givenTwoWordsWithThreeDeletions_whenIFindTheDistance_thenTheirDistanceIsReturned() {
        int distance = stringDistanceService.findDistance("AAA", "xAxAxA");

        Assertions.assertEquals(3, distance);
    }
}
