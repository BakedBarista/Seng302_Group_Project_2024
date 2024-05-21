package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

// skeleton code provided by https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941.
@Service
public class ProfanityService {
    private Map<String, String[]> words = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(ProfanityService.class);

    private int largestWordLength = 0;

    /**
     * Loads the dictionary from the text file containing a list of bad words
     */
    @PostConstruct
    public void loadConfigs() {
        Resource resource = new ClassPathResource("static/badWordList.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line = "";
            while((line = reader.readLine()) != null) {
                String[] content = null;
                try {
                    content = line.split("\\n");
                    if(content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignoreInCombinationWithWords = new String[]{};
                    if(content.length > 1) {
                        ignoreInCombinationWithWords = content[1].split("_");
                    }

                    if(word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignoreInCombinationWithWords);

                } catch(Exception e) {
                    logger.info("{}",e.toString());
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

    public ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }
        input = replaceLeetSpeak(input);
        ArrayList<String> badWords = new ArrayList<>();
        String[] tokens = input.split("\\s+|\\p{Punct}");

        // iterate over each letter in the word
        for (String token : tokens) {
            for (int start = 0; start < token.length(); start++) {
                for (int offset = 1; offset <= token.length() - start && offset <= largestWordLength; offset++) {
                    String wordToCheck = token.substring(start, start + offset);
                    //Avoid false positive
                    if (words.containsKey(wordToCheck)) {
                        String[] ignoreCheck = words.get(wordToCheck);
                        boolean ignore = false;
                        for (String ignoreWord : ignoreCheck) {
                            if (input.contains(ignoreWord)) {
                                ignore = true;
                                break;
                            }
                        }
                        if (!ignore && isStandaloneWord(input, start, start + offset)) {
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


        return input;
    }
}
