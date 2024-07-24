package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

// skeleton code provided by https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941.
@Service
public class ProfanityService {
    private Set<String> words = new HashSet<>();
    Logger logger = LoggerFactory.getLogger(ProfanityService.class);

    private int largestWordLength = 0;

    /**
     * Loads the dictionary from the text file containing a list of bad words
     */
    @PostConstruct
    public void loadConfigs() {
        Resource resource = new ClassPathResource("badWordList.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line;
            while((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.length() > largestWordLength) {
                    largestWordLength = word.length();
                }
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            logger.info("{}",e.toString());
        }
    }


    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input The input string to be checked
     * @return A list of bad words found
     */

    public List<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }
        input = input.toLowerCase();
        input = replaceLeetSpeak(input);

        List<String> badWords = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^\\s\\p{Punct}]+");
        Matcher matcher = pattern.matcher(input);

        // iterate over each letter in the word
        for (MatchResult match : matcher.results().toList()) {
            String token = match.group();
            int start = match.start();
            for (int offset = 0; offset < token.length(); offset++) {
                for (int length = 1; length <= token.length() - offset && length <= largestWordLength; length++) {
                    String wordToCheck = token.substring(offset, offset + length);
                    if (words.contains(wordToCheck)) {
                        // Avoid false positive
                        if (isStandaloneWord(input, start + offset, start + offset + length)) {
                            badWords.add(wordToCheck);
                        }
                    }
                }
            }
        }


        for(String s: badWords) {
            logger.info("{} was a bad word!",s);
        }
        return badWords;

    }

    /**
     * Checks whether a string is a standalone word or not
     * @param input input string
     * @param start starting of string
     * @param end end of string
     * @return
     */
    //Helper function to check if a word is standalone by checking before start and after end of the word
    public boolean isStandaloneWord(String input, int start, int end) {
        boolean isStartValid = start == 0 || !Character.isLetterOrDigit(input.charAt(start - 1));
        boolean isEndValid = end == input.length() || !Character.isLetterOrDigit(input.charAt(end));
        return isStartValid && isEndValid;
    }

    private String replaceLeetSpeak(String input) {
        input = input.replace("1","i");
        input = input.replace("!","i");
        input = input.replace("3","e");
        input = input.replace("4","a");
        input = input.replace("@","a");
        input = input.replace("5","s");
        input = input.replace("7","t");
        input = input.replace("0","o");
        input = input.replace("9","g");
        input = input.replace("à","a");
        input = input.replace("á","a");
        input = input.replace("â","a");
        input = input.replace("ã","a");
        input = input.replace("ä","a");
        input = input.replace("å","a");
        input = input.replace("è","e");
        input = input.replace("é","e");
        input = input.replace("ê","e");
        input = input.replace("ë","e");
        input = input.replace("Æ","e");
        input = input.replace("ì","i");
        input = input.replace("í","i");
        input = input.replace("î","i");
        input = input.replace("ï","i");
        input = input.replace("ò","o");
        input = input.replace("ó","o");
        input = input.replace("ô","o");
        input = input.replace("õ","o");
        input = input.replace("ö","o");
        input = input.replace("ù","u");
        input = input.replace("ú","u");
        input = input.replace("û","u");
        input = input.replace("ü","u");
        input = input.replace("À","a");
        input = input.replace("Á","a");
        input = input.replace("Â","a");
        input = input.replace("Ã","a");
        input = input.replace("Ä","a");
        input = input.replace("È","e");
        input = input.replace("É","e");
        input = input.replace("Ê","e");
        input = input.replace("Ë","e");
        input = input.replace("Ì","i");
        input = input.replace("Í","i");
        input = input.replace("Î","i");
        input = input.replace("Ï","i");
        input = input.replace("Ò","o");
        input = input.replace("Ó","o");
        input = input.replace("Ô","o");
        input = input.replace("Õ","o");
        input = input.replace("Ö","o");
        input = input.replace("Ù","u");
        input = input.replace("Ú","u");
        input = input.replace("Û","u");
        input = input.replace("Ü","u");
        input = input.replace("$","s");


        return input;
    }
}
