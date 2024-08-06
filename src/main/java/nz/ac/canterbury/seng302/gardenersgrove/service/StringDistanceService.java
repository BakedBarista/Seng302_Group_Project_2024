package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for getting similar strings to a given string
 */
@Service
public class StringDistanceService {

    /**
     * Take a list of strings and return a list of strings that are similar to the input string (at least half similar)
     * @param strings list of strings to compare against
     * @param input input string
     * @return list of strings that are similar to input
     */
    public List<String> getSimilarStrings(List<String> strings, String input) {
        List<String> closeStrings = new ArrayList<>();

        for (String string : strings) {
            if (input.contains(string)) {
                closeStrings.add(string);
            } else if (string.contains(input)) {
                closeStrings.add(string);
            } else if (findDistance(input, string) <= (string.length() / 2)) {
                closeStrings.add(string);
            }
        }

        return closeStrings;
    }

    /**
     * Take two strings and find the distance between them.
     * This is taken from www.geeksforgeeks.org
     * <a href="https://www.geeksforgeeks.org/levenshtein-distance-between-two-strings-in-java-using-recursion/">
     *     ...
     * </a>
     * @param string string
     * @param other other string
     * @return distance between the two strings
     */
    public static int findDistance(String string, String other) {
        return findRecursiveDistance(string, string.length(), other, other.length());
    }

    /**
     * Recursively find the distance of the two strings
     * This is taken from www.geeksforgeeks.org
     * <a href="https://www.geeksforgeeks.org/levenshtein-distance-between-two-strings-in-java-using-recursion/">
     *     ...
     * </a>
     * @param string string
     * @param stringLength string length
     * @param other other string
     * @param otherLength other length
     * @return distance of strings
     */
    private static int findRecursiveDistance(String string, int stringLength, String other, int otherLength) {
        if (stringLength == 0) {
            return otherLength;
        }
        if (otherLength == 0) {
            return stringLength;
        }

        if (string.charAt(stringLength - 1) == other.charAt(otherLength - 1)) {
            return findRecursiveDistance(string, stringLength - 1, other, otherLength - 1);
        }

        int insertionCost = findRecursiveDistance(string, stringLength, other, otherLength -1);
        int deletionCost = findRecursiveDistance(string, stringLength - 1, other, otherLength);
        int substitutionCost = findRecursiveDistance(string, stringLength - 1, other, otherLength -1);

        return 1 + Math.min(Math.min(insertionCost, deletionCost), substitutionCost);
    }
}
