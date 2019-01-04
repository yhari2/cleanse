import javafx.util.Pair;

import java.io.*;
import java.util.*;

/**
 *
 * By: Yasasvi Hari
 *
 * TODO: Implement multiword tools by using mappings in preprocessing phase. Add more tools into allTools dict by hand.
 */
public class Main {
    // constants
    /**
     *
     * Complete tool list is taken from: https://helpx.adobe.com/photoshop-elements/using/tools.html
     */
    private final static String COMPLETE_TOOL_LIST = "/Users/YasasviHari/Desktop/cleanse/src/main/resources/res/complete_tool_list.csv";
    private final static String MULTIWORD_LIST = "/Users/YasasviHari/Desktop/cleanse/src/main/resources/res/multiword_tool.csv";
    private final static String OUTPUT_PATH = "/Users/YasasviHari/Desktop/cleanse/src/main/resources/outputs/";

    /**
     *
     * Maps the name of each task (represented by the file name) to a set containing all the tools in the map
     */
    private static Set<String> allTools = new HashSet<>();
    private static Map<String, String> multiwordTools = new HashMap<>();
    private static Map<String, String> reverseMap = new HashMap<>();
    // private static Scraper scraper = new Scraper();

    /**
     *
     * reads the tool list from csv file
     *
     * TESTED: Works!
     */
    private static void readCompleteToolList() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(COMPLETE_TOOL_LIST)));
            String line;

            while((line = reader.readLine()) != null) {
                String[] tools = line.split(",");

                for(String tool : tools) {
                    StringBuilder builder = new StringBuilder();
                    for(char c : tool.toCharArray()) {
                        if(c != ' ') builder.append(c);
                    }

                    allTools.add(builder.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readMultiWordTools() {
        try(BufferedReader reader = new BufferedReader(new FileReader(new File(MULTIWORD_LIST)))) {
            String line;

            while((line = reader.readLine()) != null) {
                String[] records = line.split(",");

                for(String tool : records) {
                    char[] toolChars = tool.trim().toCharArray();
                    boolean seenSpace = false;
                    StringBuilder builder = new StringBuilder();
                    String firstWord = "";

                    for(char c : toolChars) {
                        if(!seenSpace && c == ' ') {
                            firstWord = builder.toString();
                            seenSpace = true;
                        } else if(c != ' '){
                            builder.append(c);
                        }
                    }

                    multiwordTools.put(firstWord, builder.toString());
                    reverseMap.put(builder.toString(), tool.trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // from: https://www.baeldung.com/java-check-string-number
    private static boolean isNumeric(String s) {
        try {
            double d = Double.parseDouble(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private static String removePunctuation(String word) {
        String punc = ".,></&%!@#$%^&*()?:[]{}|+_-';~`";
        char[] wordArr = word.toCharArray();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < wordArr.length; i++) {
            char c = wordArr[i];
            if(punc.indexOf(c) == -1 || !(c == 's' && i > 0 && wordArr[i-1] == '\'')) {
                // not punctuation character and not an s after an apostrophe
                builder.append(c);
            }
        }

        return builder.toString();
    }

    private static Set<String> preprocessData(String filePath) {
        Set<String> words = new HashSet<>();

        String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
        Set<String> stopWordSet = new HashSet<>(Arrays.asList(stopwords));

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] record = line.split(" ");

                for(String word : record) {
                    boolean addCurrWord = true;
                    word = word.toLowerCase();

                    if(multiwordTools.containsKey(word)) {
                        String toolName = multiwordTools.get(word);
                        StringBuilder builder = new StringBuilder();

                        for(char c : toolName.toCharArray()) {
                            if(c != ' ') {
                                builder.append(c);
                            }
                        }

                        word = builder.toString();
                    }

                    word = removePunctuation(word);

                    if(stopWordSet.contains(word) || word.isEmpty() || isNumeric(word)) {
                        addCurrWord = false;
                    }

                    if(addCurrWord) words.add(word);
                }
            }

            return words;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param filePath = NonNull absolute filepath
     */
    private static void processData(String filePath) {
        try {
            Set<String> preprocessedData = preprocessData(filePath);
            preprocessedData.retainAll(allTools);

            String outputFile = formatOutputFile(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));

            for(String tool : preprocessedData) {
                if(reverseMap.containsKey(tool)) {
                    writer.write(reverseMap.get(tool) + "\n");
                } else {
                    writer.write(tool + "\n");
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatOutputFile(String input) {
        StringBuilder builder = new StringBuilder();

        for(int i = input.length() - 1; i > 0; i--) {
            if(input.charAt(i) == '/') {
                break;
            }

            builder.append(input.charAt(i));
        }

        String file = builder.reverse().toString();
        file = file.substring(0, file.length() - 4); // remove .txt
        file = file + "_output";
        return OUTPUT_PATH + file + ".txt";
    }

    /**
     * This method assumes that the arguments passed in are absolute filepaths to downloaded tutorials. Running main will
     * process each of these arguments in parallel.
     *
     * @param args = list of filePaths of scraped tutorials
     */
    public static void main(String[] args) {
        readCompleteToolList();
        readMultiWordTools();

        for(String arg : args) {
            new Thread(() -> processData(arg)).start();
        }
    }
}
