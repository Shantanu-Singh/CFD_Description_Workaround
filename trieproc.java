import java.io.*;
import java.util.*;

public class trieproc {

    private static Trie trie = new Trie();
    // private static final String[] DELIMITERS = {" ", "&", "-", "+", "/", "(", ")", ".", "*", "’", ",", "="};
    private static final String[] DELIMITERS = {" ", "&", "-", "\\+", "/", "\\(", "\\)", "\\.", "\\*", "’", ",", "="};


    public static void main(String[] args) {

        String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/BaseProduct.csv";
        String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/xl2csv.csv";
        // String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/sart.csv";
        String trieoutputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/trieoutputhashmap.txt";
        // Construct Trie
        readSearchReplacePairsFromCSV(sartfile);

        // Read ODS file
        Map<Integer, String> inputMap = readODSFromCSV(baseproductfile);

        // Process input
        TreeMap<Integer, String> outputMap = new TreeMap<>();
        for (Map.Entry<Integer, String> entry : inputMap.entrySet()) {
            outputMap.put(entry.getKey(), processInput(entry.getValue()));
        }

        // Write output to txt file
        try {
            writeOutputToTxt(trieoutputhashmap, outputMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readSearchReplacePairsFromCSV(String filename) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // br.readLine(); // Ignore the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // remember to escape the | character
                trie.put(values[0], values[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }

    private static Map<Integer, String> readODSFromCSV(String filename) {
        Map<Integer, String> inputMap = new HashMap<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // Ignore the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\|"); // remember to escape the | character
                inputMap.put(Integer.parseInt(values[0]), values[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputMap;
    }

    private static void writeOutputToTxt(String filename, Map<Integer, String> outputMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, String> entry : outputMap.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue() + "\n");
            }
        }
    }

    private static String processInput(String input) {
        List<String> output = new ArrayList<>();
        while(!input.isEmpty()) {
            String[] tokens = input.split(" ");
            String token = tokens[0];
            String longestKey = trie.getLongestKey(token);
            if (longestKey != null) {
                output.add(trie.get(longestKey));
                input = input.replace(longestKey, "").trim();
            } else {
                output.add(token);
                input = input.replace(token, "").trim();
            }
        }
        String intermediateOutput = String.join(" ", output);
        return capitalizeFully(intermediateOutput, DELIMITERS);
    }

    private static String capitalizeFully(String input, String[] delimiters) {
        for (String delimiter : delimiters) {
            // System.out.println(input);
            String[] words = input.split(delimiter);
            for (int i = 0; i < words.length; i++) {
                if (!words[i].isEmpty() && Character.isLetter(words[i].charAt(0))) {
                    words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
                }
            }
            input = String.join(delimiter, words);
        }
        return input;
    }
    
}

class Trie {
    private Map<Character, Trie> children;
    private String value;

    public Trie() {
        children = new HashMap<>();
        value = null;
    }

    public void put(String key, String value) {
        Trie node = this;
        for (char c : key.toCharArray()) {
            node.children.putIfAbsent(c, new Trie());
            node = node.children.get(c);
        }
        node.value = value;
    }

    public String get(String key) {
        Trie node = this;
        for (char c : key.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node.value;
    }

    public String getLongestKey(String input) {
        // Implementation depends on specific requirements for key matching.
        // For now, let's assume it simply checks for direct prefix match.
        Trie node = this;
        String longestKey = null;
        for (char c : input.toCharArray()) {
            node = node.children.get(c);
            if (node == null) break;
            if (node.value != null) longestKey = input.substring(0, input.indexOf(c) + 1);
        }
        return longestKey;
    }
}
