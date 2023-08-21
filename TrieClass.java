import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean endOfWord;

    public TrieNode() {
        children = new HashMap<>();
        endOfWord = false;
    }
}

public class TrieClass {
    private TrieNode root;

    public TrieClass() {
        root = new TrieNode();
    }

    // Insert a word into the Trie
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.endOfWord = true;
    }

    // Search for a word in the Trie
    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false; // Character not found
            }
            current = node;
        }
        return current.endOfWord;
    }

    public static void main(String[] args) {
        HashMap<String, String> sart = new HashMap<>();
        sart.put("CNTY", "fruit");
        sart.put("app&le", "altered fruit");
        sart.put("apple+tree", "source of apples");

        TrieClass trie = new TrieClass();
        for (String key : sart.keySet()) {
            trie.insert(key);
        }

        System.out.println(trie.search("apple"));      // true
        System.out.println(trie.search("app&le"));     // true
        System.out.println(trie.search("apple+tree")); // true
        System.out.println(trie.search("appl"));       // false
    }
}