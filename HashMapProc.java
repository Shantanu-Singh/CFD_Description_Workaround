import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class HashMapProc {

    private static List<String> searchKeys = new ArrayList<>();
    private static Map<String, String> searchReplacePairs = new HashMap<>();
    private static final String[] DELIMITERS = {" ", "&", "-", "\\+", "/", "\\(", "\\)", "\\.", "\\*", "’", ",", "="};

    public static void main(String[] args) {

        String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/BaseProduct.csv";
        String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/xl2csv.csv";
        String outputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/outputhashmap.txt";

        // Read search replace pairs
        readSearchReplacePairsFromCSV(sartfile);

        // Sort keys by length in descending order
        searchKeys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        // Read ODS file
        Map<Integer, String> inputMap = readODSFromCSV(baseproductfile);

        // Process input
        TreeMap<Integer, String> outputMap = new TreeMap<>();
        for (Map.Entry<Integer, String> entry : inputMap.entrySet()) {
            outputMap.put(entry.getKey(), processInput(entry.getValue().toLowerCase()));
        }

        // Write output to txt file
        try {
            writeOutputToTxt(outputhashmap, outputMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readSearchReplacePairsFromCSV(String filename) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                searchReplacePairs.put(values[0].toLowerCase(), values[1]);
                searchKeys.add(values[0].toLowerCase());
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
                String[] values = line.split("\\|");
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
                writer.write(entry.getKey() + "|" + capitalizeFully(entry.getValue(), DELIMITERS) + "\n");
            }
        }
    }

    private static String processInput(String input) {
        for (String key : searchKeys) {
            input = input.replaceAll(key, searchReplacePairs.get(key));
        }
        return input;
    }

    private static String capitalizeFully(String input, String[] delimiters) {
        for (String delimiter : delimiters) {
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
