package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// skeleton code provided by https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941.
@Service
public class ProfranityService {
    static Map<String, String[]> words = new HashMap<>();
    static Logger logger = LoggerFactory.getLogger(ProfranityService.class);

    static int largestWordLength = 0;
    
    public ProfranityService(){
    }

    public static void loadConfigs() {

        String contentString = "arse\narsehole\nass\nasses\nassface\nassfaces\nasshole\nassholes\nbastard\nbastards\nbeaner\nbellend\nbint\nbitch\nbitches\nbitchy\nblowjob\nblump\nblumpkin\nbollocks\nbollox\nboner\nbukkake\nbullshit\nbunghole\nbuttcheeks\nbutthole\nbuttpirate\nbuttplug\ncarpetmuncher\nchinc\nchink\nchoad\nchode\ncirclejerk\nclit\nclunge\ncock\ncocksucker\ncocksuckers\ncocksucking\ncoochie\ncoochy\ncoon\ncooter\ncornhole\ncum\ncunnie\ncunt\ncunts\ndago\ndic\ndick\ndickhead\ndickheads\ndik\ndike\ndildo\ndoochbag\ndoosh\ndouche\ndouchebag\ndumbass\ndumbasses\ndyke\nfag\nfagget\nfaggit\nfaggot\nfaggots\nfagtard\nfanny\nfeck\nfelch\nfeltch\nfigging\nfingerbang\nfrotting\nfuc\nfuck\nfucked\nfuckedup\nfucker\nfuckers\nfucking\nfuckoff\nfucks\nfuckup\nfudgepacker\nfuk\nfukker\nfukkers\nfuq\ngangbang\ngash\ngoddamn\ngoddamnit\ngokkun\ngooch\ngook\nguido\nheeb\nhonkey\nhooker\njackass\njackasses\njackoff\njap\njerkoff\njigaboo\njiggerboo\njizz\njunglebunny\nkike\nknobbing\nkooch\nkootch\nkraut\nkyke\nlesbo\nlezzie\nmilf\nminge\nmotherfucker\nmotherfuckers\nmotherfucking\nmuff\nmuffdiver\nmuffdiving\nmunging\nmunter\nngga\nniga\nnigga\nnigger\nniggers\nniglet\nnigr\npaki\npanooch\npecker\npeckerhead\npillock\npiss\npissed\npollock\npoon\npoonani\npoonany\npoontang\nporchmonkey\nprick\npunani\npunanny\npunany\npussie\npussies\npussy\nputa\nputo\nquim\nraghead\nruski\nschlong\nscrote\nshag\nshit\nshite\nshithead\nshitheads\nshits\nshittier\nshittiest\nshitting\nshitty\nskank\nskeet\nslag\nslanteye\nslut\nsmartass\nsmartasses\nsmeg\nsnatch\nspic\nspick\nsplooge\nspooge\nteabagging\ntit\ntities\ntits\ntitties\ntitty\ntosser\ntowelhead\ntwat\nvibrator\nwank\nwanker\nwetback\nwhore\nwiseass\nwiseasses\nwop\n";

        String[] content = contentString.split("\n");
        for(String line: content){
            try {
                content = line.split(",");
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
                words.put(word.replace(" ", ""), ignoreInCombinationWithWords);

            } catch(Exception e) {
                logger.info("{}", e.toString());
            }

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
            System.out.println(s + " qualified as a bad word in a username");
        }
        return badWords;

    }
}
