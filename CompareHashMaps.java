import java.io.*;
import java.util.*;

public class CompareHashMaps {
    public static void main(String[] args) {
        // HashMap<Integer, String> A = new HashMap<>();
        // HashMap<Integer, String> B = new HashMap<>();

        HashMap<Integer, String> A = loadHashMapFromFile("/Users/IN22902705/Documents/CFD_Description_Workaround/sonetto_hashmap.csv");
        HashMap<Integer, String> B = loadHashMapFromFile("/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/replacedoutputhashmap.txt");

        TreeMap<Integer, String> C = new TreeMap<>();
        int matchedCount = 0;

        for (Integer key : A.keySet()) {
            if (!B.containsKey(key)) {
                C.put(key, "not present in hashmap B");
            } else {
                if (!A.get(key).equals(B.get(key))) {
                    C.put(key, "Not Matched");
                } else {
                    C.put(key, "Matched");
                    matchedCount++;
                }
            }
        }

        for (Integer key : B.keySet()) {
            if (!A.containsKey(key)) {
                C.put(key, "not present in hashmap A");
            }
        }

        double matchedPercentage = ((double) matchedCount / C.size()) * 100;

        System.out.println("\n\n\nTotal Entries: " + C.size());
        System.out.println("Number of Matched Entries: " + matchedCount);
        System.out.println("Number of UNMATCHED Entries: " + (C.size()-matchedCount));
        System.out.println("Percentage of Matched Entries: " 
        + "\u001B[32m" + matchedPercentage + "%" + "\n");

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("comparehashmapoutput.txt"));
            for (Map.Entry<Integer, String> entry : C.entrySet()) {
                out.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Exception occurred while writing to file");
        }
    }

    private static HashMap<Integer, String> loadHashMapFromFile(String filename) {
        HashMap<Integer, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    map.put(Integer.parseInt(parts[0].trim()), parts[1].trim());
                } else {
                    System.out.println("Invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Exception occurred while reading file: " + filename);
        }
        return map;
    }

    public static void compareFiles(){
        HashMap<Integer, String> A = loadHashMapFromFile("/Users/IN22902705/Documents/CFD_Description_Workaround/sonetto_hashmap.csv");
        HashMap<Integer, String> B = loadHashMapFromFile("/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/replacedoutputhashmap.txt");

        // Initialize your HashMaps A and B here...

        TreeMap<Integer, String> C = new TreeMap<>();
        int matchedCount = 0;

        for (Integer key : A.keySet()) {
            if (!B.containsKey(key)) {
                C.put(key, "not present in hashmap B");
            } else {
                if (!A.get(key).equals(B.get(key))) {
                    C.put(key, "Not Matched");
                } else {
                    C.put(key, "Matched");
                    matchedCount++;
                }
            }
        }

        for (Integer key : B.keySet()) {
            if (!A.containsKey(key)) {
                C.put(key, "not present in hashmap A");
            }
        }

        double matchedPercentage = ((double) matchedCount / C.size()) * 100;

        System.out.println("\n\n\nTotal Entries: " + C.size());
        System.out.println("Number of Matched Entries: " + matchedCount);
        System.out.println("Number of UNMATCHED Entries: " + (C.size()-matchedCount));
        System.out.println("Percentage of Matched Entries: " 
        + "\u001B[32m" + matchedPercentage + "%" + "\n");

        // Writing HashMap C to a text file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("comparehashmapoutput.txt"));
            for (Map.Entry<Integer, String> entry : C.entrySet()) {
                out.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Exception occurred while writing to file");
        }
    }
}
