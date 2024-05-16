package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

// skeleton code provided by https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941.
@Service
public class ProfanityService {
    static Map<String, String[]> words = new HashMap<>();
    static Logger logger = LoggerFactory.getLogger(ProfanityService.class);

    static int largestWordLength = 0;

    public static void loadConfigs() {
        Resource resource = new ClassPathResource("static/badWordList.txt");
        logger.info("{}",resource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line = "";
            while((line = reader.readLine()) != null) {
                String[] content = null;
                try {
                    content = line.split("'\n'");
                    for (String a: content) {
                        logger.info("{}", a);
                    }
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
                    logger.info("{}",e);
                }

            }
            logger.info("{}",words);
        } catch (IOException e) {
            logger.info("{}",e);
        }
    }


    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input
     * @return
     */
     
    public static ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        // don't forget to remove leetspeak, probably want to move this to its own function and use regex if you want to use this 
        
        input = input.replace("1","i");
        input = input.replace("!","i");
        input = input.replace("3","e");
        input = input.replace("4","a");
        input = input.replace("@","a");
        input = input.replace("5","s");
        input = input.replace("7","t");
        input = input.replace("0","o");
        input = input.replace("9","g");
        

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // iterate over each letter in the word
        for(int start = 0; start < input.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached. 
            for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                String wordToCheck = input.substring(start, start + offset);
                if(words.containsKey(wordToCheck)) {
                    // for example, if you want to say the word bass, that should be possible.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for(int s = 0; s < ignoreCheck.length; s++ ) {
                        if(input.contains(ignoreCheck[s])) {
                            ignore = true;
                            break;
                        }
                    }
                    if(!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }


        for(String s: badWords) {
            System.out.println(s + " qualified as a bad word");
        }
        return badWords;

    }
}
