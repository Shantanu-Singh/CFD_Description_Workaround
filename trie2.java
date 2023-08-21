import java.util.*;

class Node {
    String word;
    Node[] children = new Node[128];
}

public class trie2 {
    public static void main(String[] args) {
        HashMap<Integer, String> baseProduct = new HashMap<>();
        baseProduct.put(91803133, "CNTY FF JUMPER AW23 2PK V NECK GREY");
        baseProduct.put(91803156, "CNTY FF");
        baseProduct.put(91803157, "FF CNTY");

        HashMap<String, String> sart = new HashMap<>();
        sart.put("CNTY FF", "F&F");
        sart.put("FF", "F&F");
        sart.put("V", "VIRTUAL");

        HashMap<Integer, String> baseProductUpdated = new HashMap<>();

        Node root = new Node();

        // build trie with reversed keys
        for (String key : sart.keySet()) {
            Node node = root;
            for (int i = key.length() - 1; i >= 0; --i) {
                int idx = key.charAt(i);
                if (node.children[idx] == null) node.children[idx] = new Node();
                node = node.children[idx];
            }
            node.word = key;
        }

        // process base products
        for (Map.Entry<Integer, String> entry : baseProduct.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, j; i < entry.getValue().length(); i = j) {
                Node node = root;
                for (j = i; j < entry.getValue().length() && node.children[entry.getValue().charAt(j)] != null; ++j)
                    node = node.children[entry.getValue().charAt(j)];

                if (node.word != null)
                    sb.append(sart.get(node.word));
                else
                    sb.append(entry.getValue().charAt(i));
            }
            baseProductUpdated.put(entry.getKey(), sb.toString());
        }

        // print the updated base products
        for (Map.Entry<Integer, String> entry : baseProductUpdated.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
