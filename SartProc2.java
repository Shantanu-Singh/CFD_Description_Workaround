import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SartProc2 {

    private static Map<String, String> sartterms_singleword = new HashMap<>();

    public static void main(String[] args) {

        // Create Path Variables
        String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/BaseProduct.csv";
        String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/xl2csv.csv";
        String outputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/replacedoutputhashmap.txt";
        
        //TODO: Test Files
        // String baseproductfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_BaseProduct.csv";
        // String sartfile = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_xl2csv.csv";
        // String outputhashmap = "/Users/IN22902705/Documents/CFD_Description_Workaround/Workdpsace/Tst_replacedoutputhashmap.txt";


        Map<Integer, String> baseproduct = readBaseProductFile(baseproductfile);
        System.out.println("Reading Base Product file Complete");

        Map<String, String> sart = readSartFile(sartfile);
        System.out.println("Reading sart file Complete");

        //Sart Split
        Map<String, String> sartterms_multiword = new HashMap<>();

        for (Map.Entry<String, String> entry : sart.entrySet()) {
            if (entry.getKey().contains(" ")) {
                sartterms_multiword.put(entry.getKey(), entry.getValue());
            } else {
                sartterms_singleword.put(entry.getKey(), entry.getValue());
            }
        }

        // TODO: SART Process

        System.out.println("Starting SART...");
        TreeMap<Integer, String> baseProductUpdated = new TreeMap<>();
        HashMap<Integer, Integer> baseProductIndex = new HashMap<>();

        //Iter 4
        for (Map.Entry<Integer, String> bpEntry : baseproduct.entrySet()) {
            for (Map.Entry<String, String> stEntry : sartterms_multiword.entrySet()) {
                String key = stEntry.getKey();
                if ((bpEntry.getValue().equals(key) || (bpEntry.getValue().contains(key + " "))) && bpEntry.getValue().contains(key)) {
                    Integer charIndex = stEntry.getKey().length() - 1;
                    baseProductUpdated.put(bpEntry.getKey(), bpEntry.getValue().replace(key, stEntry.getValue()));
                    baseProductIndex.put(bpEntry.getKey(), charIndex);
                }
            }
        }

        System.out.println("multivalue sart completed");
        

        for (Integer key : baseproduct.keySet()) {
            String value = baseproduct.get(key);
            String updatedValue;
            if (baseProductUpdated.containsKey(key)) {
                updatedValue = processBaseProductValue(baseProductUpdated.get(key));
            } else {
                updatedValue = processBaseProductValue(value);
            }
            baseProductUpdated.put(key, updatedValue);
        }

        System.out.println("singlevalue sart completed");

        

        // Iter 3
        // baseproduct.forEach((key, value) -> {
        //     for (String sartKey : sartterms_multiword.keySet()) {
        //         String sartValue = sartterms_multiword.get(sartKey);
        //         value = value.replaceAll("\\b" + Pattern.quote(sartKey) + "\\b", sartValue);
        //     }
        //     String updatedValue = toTitleCase(value);
        //     baseProductUpdated.put(key, updatedValue);
        // });

        //Iter 4
        // for (Map.Entry<Integer, String> bpEntry : baseproduct.entrySet()) {
        //     for (Map.Entry<String, String> stEntry : sartterms_multiword.entrySet()) {
        //         String key = stEntry.getKey();
        //         if ((bpEntry.getValue().equals(key) || (bpEntry.getValue().contains(key + " "))) && bpEntry.getValue().contains(key)) {
        //             baseProductUpdated.put(bpEntry.getKey(), toTitleCase(bpEntry.getValue().replace(key, stEntry.getValue())));
        //         }
        //     }
        // }
        

        // Writing to a file
        writeFile(baseProductUpdated, outputhashmap);
        System.out.println("\u001B[32m" + "Process Complete !");
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str : arr) {
            if (str.length() != 0) {
                if ("(C)".equals(str) || str.matches(".*\\d+(X)$")) {
                    sb.append(str).append(" ");
                } else if (str.matches(".*\\d+(ML|CL|KG|CM|PK|GB)$")) {
                    sb.append(str.substring(0, str.length() - 2))
                            .append(Character.toUpperCase(str.charAt(str.length() - 2)))
                            .append(Character.toLowerCase(str.charAt(str.length() - 1))).append(" ");
                } else if (str.matches(".*\\d+(G|C|L|M|W|H)$") || str.contains("&")) {
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
                } else {
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
    }
}