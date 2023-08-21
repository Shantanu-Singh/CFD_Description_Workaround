import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SartProc4 {

    private static Map<String, String> sartterms_singleword = new HashMap<>();

    public static void main(String[] args) {

        // Create Path Variables
        // String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/BaseProduct.csv";
        // String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/xl2csv.csv";
        // String outputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/replacedoutputhashmap.txt";
        
        //TODO: Test Files
        String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_BaseProduct.csv";
        String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_xl2csv.csv";
        String outputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_replacedoutputhashmap.txt";

        //Trie of Sart and BaseProduct
        TrieClass trieSart = new TrieClass();
        TrieClass trieBaseProduct = new TrieClass();

        Map<Integer, String> baseproduct = readBaseProductFile(baseproductfile);
        System.out.println("Reading Base Product file Complete");

        Map<String, String> sart = readSartFile(sartfile);
        System.out.println("Reading sart file Complete");

        //Sart Split
        Map<String, String> sartMultiword = new HashMap<>();
        LinkedHashMap<String, String> sartMultiwordSorted = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : sart.entrySet()) {
            //insert sart to Trie
            trieSart.insert(entry.getKey());
            //Other checks for splitting the sart on single word and multi word.
            if (entry.getKey().contains(" ")) {
                sartMultiword.put(entry.getKey(), entry.getValue());
            } else {
                sartterms_singleword.put(entry.getKey(), entry.getValue());
            }
        }

        //Reverse sort the MultiWordSART
        sartMultiwordSorted = sortByKeyLength(sartMultiword);

        // TODO: SART Process

        long startTime = System.currentTimeMillis();
        System.out.println("Starting Search and Replace (SART)...");
        TreeMap<Integer, String> baseProductUpdated = new TreeMap<>();
        // TreeMap<Integer, String> baseProductMultiWordUpdated = new TreeMap<>();
        HashMap<Integer, Map<String, Integer>> baseProductIndexes = new HashMap<>();

        //TODO: Test
        HashMap<String, String> sart2 = sartMultiwordSorted;
        HashMap<String, String> baseProductMultiWordUpdated = expandDescription(baseproduct, sart2);
        //Iter 4
        for (Map.Entry<Integer, String> bpEntry : baseproduct.entrySet()) {
            
            Integer bpEntryKey = bpEntry.getKey();
            String bpEntryValue = bpEntry.getValue();
            String updatedValue = null;
            trieBaseProduct.insert(bpEntry.getValue());
            StringBuilder sb = new StringBuilder();

            Integer startIndex, currIndex, endIndex = 0;
            endIndex = bpEntryValue.length() - 1;

            //Start Multiword SART
            for (Map.Entry<String, String> stEntry : sartMultiwordSorted.entrySet()) {
                String stEntryKey = stEntry.getKey();
                String stEntryValue = stEntry.getValue();
                //Input description from BaseProduct should contain the key and should be a word or an exact match.
                if (bpEntryValue.length() >= stEntryKey.length()) {
                    //Check if a match is present at start and ends with space. (Can be checked via Trie as well)
                    if (bpEntryValue.startsWith(stEntryKey, 0) && bpEntryValue.contains(stEntryKey + ' ')){
                        System.out.println(stEntryKey);

                    }
                }
            }


            //Start Single word SART
            if (baseProductIndexes.containsKey(bpEntryKey)) {
                Map<String, Integer> retrievedMap = baseProductIndexes.get(bpEntryKey);
                String[] substrings = splitString(baseproduct.get(bpEntryKey), retrievedMap.get("start"), retrievedMap.get("end")+1);
                String updatedString1 = processBaseProductValue(substrings[0]);
                if (retrievedMap.get("start") != 0){
                    updatedString1 += ' ';
                }
                
                String updatedString2 = baseProductMultiWordUpdated.get(bpEntryKey);
                String updatedString3 = processBaseProductValue(substrings[2]);
                updatedValue = updatedString1 + updatedString2 + ' ' + updatedString3;
                // System.out.println("test");
                // updatedValue = processBaseProductValue(baseProductUpdated.get(bpEntryKey));
            } else {
                updatedValue = processBaseProductValue(bpEntryValue);
            }
            baseProductUpdated.put(bpEntryKey, updatedValue);
        }

        //Log Runtime
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime)/1000;
        System.out.println("SART completed in - " + duration + " seconds.");
        

        // Writing to a file
        writeFile(baseProductUpdated, outputhashmap);
        System.out.println("\u001B[32m" + "Process Complete !");
        CompareHashMaps.compareFiles();
    }




    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str : arr) {
            if (str.length() != 0) {
                if ("(C)".equals(str) || str.matches(".*\\d+(X)$")) {
                    sb.append(str).append(" ");
                } else if (str.matches(".*\\d+(ML|CL|KG|CM|PK|GB|XL|MG|MM)$")) {
                    sb.append(str.substring(0, str.length() - 2))
                            .append(Character.toUpperCase(str.charAt(str.length() - 2)))
                            .append(Character.toLowerCase(str.charAt(str.length() - 1))).append(" ");
                } else if (str.matches(".*(\\d+H/\\d+|\\d+(G|C|L|M|W|H|D))$") || str.contains("&") || str.contains("H+M")) {
                    sb.append(str).append(" ");
                } else if (str.matches(".+-.+")) {
                    String[] hyphenSeparatedWords = str.split("-");
                    for (String word : hyphenSeparatedWords) {
                        if (word.length() > 0) {
                            sb.append(Character.toUpperCase(word.charAt(0)))
                                    .append(word.substring(1).toLowerCase()).append("-");
                        }
                    }
                    if (sb.charAt(sb.length() - 1) == '-') {
                        sb.setLength(sb.length() - 1); // remove the last "-"
                    }
                    sb.append(" ");
                } else if (str.contains("/")) {
                    String[] hyphenSeparatedWords = str.split("/");
                    for (String word : hyphenSeparatedWords) {
                        if (word.length() > 0) {
                            sb.append(Character.toUpperCase(word.charAt(0)))
                                    .append(word.substring(1).toLowerCase()).append("/");
                        }
                    }
                    if (sb.charAt(sb.length() - 1) == '/') {
                        sb.setLength(sb.length() - 1); // remove the last "-"
                    }
                    sb.append(" ");
                } else if (str.matches("^\\d+INCH$")) {
                    sb.append(str.substring(0, str.length() - 4))
                      .append("Inch").append(" ");
                } else if (str.matches("^\\d*MG/\\d*ML$")) {
                    String[] parts = str.split("/");
                    String firstPart = parts[0].replace("MG", "Mg");
                    String secondPart = parts[1].replace("ML", "Ml");
                    sb.append(firstPart).append("/").append(secondPart).append(" ");
                } else if (str.matches("^\\d+PACK$")) {
                    sb.append(str.substring(0, str.length() - 4))
                      .append("Pack").append(" ");
                } else {
                    //For strings which don't match any of the above conditions, we do the below logic for Title Case conversion
                    sb.append(Character.toUpperCase(str.charAt(0)))
                            .append(str.substring(1).toLowerCase()).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }

    public static void writeFile(Map<Integer, String> baseProductUpdated, String outputhashmap){
        // Writing to a file
        try {
            FileWriter fileWriter = new FileWriter(outputhashmap);
            baseProductUpdated.forEach((key, value) -> {
                try {
                    fileWriter.write(key + "|" + value + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, String> readBaseProductFile(String baseproductfile){
        String line;
        Map<Integer, String> baseproduct = new HashMap<>();

        // Read the Base Product file
        try (BufferedReader br = new BufferedReader(new FileReader(baseproductfile))) {

            // Ignore the header
            br.readLine();

            while ((line = br.readLine()) != null) {
                //Escaping the pipe | character while splitting the bseproduct line
                String[] values = line.split("\\|");
                baseproduct.put(Integer.parseInt(values[0]), values[2]);
            }
        } catch (IOException e) {}
        return baseproduct;
    }

    public static Map<String, String> readSartFile(String sartfile){
        String line2;
        Map<String, String> sart = new HashMap<>();

        // Read the Sart file
        try (BufferedReader br = new BufferedReader(new FileReader(sartfile))) {

            // Ignore the header
            br.readLine();

            while ((line2 = br.readLine()) != null) {
                String[] values = line2.split("\\,");
                sart.put(values[0], values[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sart;
    }

    public static String processBaseProductValue(String value) {
        String[] words = value.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String replacement = sartterms_singleword.get(words[i]);
            if (replacement != null && !words[i].matches("(?i).*\\d+(ML|G|L)$")) {
                words[i] = replacement;
            }
        }
        return toTitleCase(String.join(" ", words));

        //Prashanth's method
        // return capitalizeFully(String.join(" ", words), ' ', '&', '-', '+', '/', '(', ')', '.', '*', '’', ',', '=');
    }

    public static List<String> findSerialCombinations(String input) {
        String[] words = input.split(" ");  // Split the string into individual words
        List<String> combinations = new ArrayList<>();
        
        for (int len = 1; len <= words.length; len++) {  // len represents the number of words in the combination
            for (int i = 0; i <= words.length - len; i++) {
                StringBuilder combo = new StringBuilder();
                for (int j = 0; j < len; j++) {
                    combo.append(words[i + j]).append(" ");  // Add the words in the serial order
                }
                combinations.add(combo.toString().trim());  // Remove the trailing space and add to the list
            }
        }
        
        return combinations;
    }

    public static LinkedHashMap<String, String> sortByKeyLength(Map<String, String> sartMultiword) {
        // Convert the map's entries to a list
        List<Map.Entry<String, String>> list = new LinkedList<>(sartMultiword.entrySet());

        // Sort the list by descending order of key length
        list.sort(new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return Integer.compare(o2.getKey().length(), o1.getKey().length());
            }
        });

        // Create a new linked hash map and add the sorted entries
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static HashMap<String, Integer> findSubstringIndices(String mainStr, String subStr) {
        
        int startIndex = mainStr.indexOf(subStr);
        HashMap<String, Integer> indicesMap = new HashMap<>();
        if (startIndex != -1) {
            int endIndex = startIndex + subStr.length() - 1;
            indicesMap.put("start", startIndex);
            indicesMap.put("end", endIndex);
            // if (startIndex < endIndex) {
            //     indicesMap.put("start", startIndex);
            //     indicesMap.put("end", endIndex);
            // }
            // result.put(stringID, indicesMap);
        }
        return indicesMap;

        
    }

    public static String[] splitString(String str, int index1, int index2) {
        String part1 = str.substring(0, index1);
        String part2 = str.substring(index1, index2);
        String part3 = str.substring(index2);
        
        return new String[]{part1, part2, part3};
    }

    public static String toTitleCase2(String input) {
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                words[i] = words[i].substring(0, 1).toUpperCase() + 
                           words[i].substring(1).toLowerCase();
            }
        }
        return String.join(" ", words);
    }

    //Prashanth helper methods below
    private static String capitalizeFully(String str, final char... delimiters) {

        if (str.isEmpty()) {
            return str;
        }
        str = str.toLowerCase();
        System.out.println(str);
        return capitalize(str, delimiters);

    }

    private static String capitalize(final String str, final char... delimiters) {

        if (str.isEmpty()) {
            return str;
        }
        final Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];

        int outOffset = 0;
        boolean capitalizeNext = true;

        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (Character.isLetter(codePoint)) {
                final int titleCaseCodePoint = capitalizeNext ? Character.toTitleCase(codePoint) : codePoint;
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    private static Set<Integer> generateDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[] {' '}, 0));
            }
            return delimiterHashSet;
        }
 
        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }

    public static HashMap<String, String> expandDescription(Map<Integer, String> baseproduct, HashMap<String, String> sart) {
        HashMap<String, String> expandedDescription = new HashMap<>();

        for (Integer key : baseproduct.keySet()) {
            String inputDescription = baseproduct.get(key);
            String expandedInputDescription = "";
            String currentMatch = "";

            for (String keySart : sart.keySet()) {
                if (keySart.length() <= inputDescription.length()) {
                    int index = 0;
                    while (index < inputDescription.length() && inputDescription.startsWith(keySart, index)) {
                        currentMatch = keySart;
                        index += keySart.length();
                    }

                    if (index > 0 && inputDescription.charAt(index - 1) == ' ') {
                        expandedInputDescription += sart.get(keySart) + " ";
                        inputDescription = inputDescription.substring(index);
                    }
                }
            }

            expandedInputDescription += inputDescription;
            expandedDescription.put(key.toString(), expandedInputDescription);
        }

        return expandedDescription;
    }
}