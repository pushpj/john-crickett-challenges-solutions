import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BuildYourOwnCompressionTool {
    public static void main(String[] args) {
        String inputFileName = args[0];
        String encodedFileName = "encoded.bin"; // Output file for encoded text
        String decodedFileName = "decoded.txt"; // Output file for decoded text

        // Step 1: Read the text and determine character frequencies
        Map<Character, Integer> frequencyMap = calculateCharacterFrequencies(inputFileName);

        // Step 2: Build the Huffman tree
        Node root = buildHuffmanTree(frequencyMap);

        // Step 3: Generate the prefix-code table
        Map<Character, String> prefixCodeTable = generatePrefixCodeTable(root);

        // Step 4: Write the header section to the output file
        writeHeaderSection(encodedFileName, frequencyMap, root);

        // Step 5: Encode the text and write to the output file
        encodeText(inputFileName, encodedFileName, prefixCodeTable);

        // Step 6: Decode the encoded file
        Node huffmanTree = reconstructHuffmanTree(encodedFileName);

        // Step 7: Decode the encoded text and write to the output file
        decodeText(encodedFileName, decodedFileName, huffmanTree);
    }

    // Function to build the Huffman tree
    private static Node buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Create a leaf node for each character and add it to the priority queue
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }

        // Combine nodes until we have only one node left in the priority queue
        while (pq.size() > 1) {
            // Remove the two nodes with the lowest frequencies
            Node left = pq.poll();
            Node right = pq.poll();

            // Create a new internal node with frequency equal to the sum of the two nodes
            Node parent = new Node('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            // Add the new node back to the priority queue
            pq.offer(parent);
        }

        // Return the root of the Huffman tree
        return pq.poll();
    }

    // Function to generate the prefix-code table
    private static Map<Character, String> generatePrefixCodeTable(Node root) {
        Map<Character, String> prefixCodeTable = new HashMap<>();
        generatePrefixCodeTableHelper(root, "", prefixCodeTable);
        return prefixCodeTable;
    }

    private static void generatePrefixCodeTableHelper(Node node, String prefix, Map<Character, String> prefixCodeTable) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                // Leaf node, store its character and prefix code
                prefixCodeTable.put(node.character, prefix);
            }
            // Recursively traverse left and right sub-trees
            generatePrefixCodeTableHelper(node.left, prefix + "0", prefixCodeTable);
            generatePrefixCodeTableHelper(node.right, prefix + "1", prefixCodeTable);
        }
    }

    // Function to write the header section to the output file
    private static void writeHeaderSection(String fileName, Map<Character, Integer> frequencyMap, Node root) {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName, true))) {
            // Write the character frequencies
            for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
                outputStream.write(entry.getKey());
                outputStream.write(entry.getValue());
            }
            // Write a delimiter to separate character frequencies from the tree structure
            outputStream.write(';');
            // Write the Huffman tree structure
            writeTreeStructure(outputStream, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function to write the Huffman tree structure recursively
    private static void writeTreeStructure(BufferedOutputStream outputStream, Node node) throws IOException {
        if (node != null) {
            if (node.left == null && node.right == null) {
                // Leaf node, write '1' followed by the character
                outputStream.write(1);
                outputStream.write(node.character);
            } else {
                // Internal node, write '0' and recursively write left and right sub-trees
                outputStream.write(0);
                writeTreeStructure(outputStream, node.left);
                writeTreeStructure(outputStream, node.right);
            }
        }
    }

    // Function to encode the text using the prefix-code table and write to the output file
    private static void encodeText(String inputFileName, String outputFileName, Map<Character, String> prefixCodeTable) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFileName, true))) {
            int c;
            StringBuilder encodedText = new StringBuilder();
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                encodedText.append(prefixCodeTable.get(character));
            }
            // Write the encoded text to the output file
            writeEncodedText(outputStream, encodedText.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper function to write the encoded text to the output file
    private static void writeEncodedText(BufferedOutputStream outputStream, String encodedText) throws IOException {
        for (int i = 0; i < encodedText.length(); i += 8) {
            String byteString = encodedText.substring(i, Math.min(i + 8, encodedText.length()));
            byte b = (byte) Integer.parseInt(byteString, 2);
            outputStream.write(b);
        }
    }

    private static Node reconstructHuffmanTree(String fileName) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName))) {
            // Read character frequencies from the header section
            Map<Character, Integer> frequencyMap = new HashMap<>();
            int c;
            while ((c = inputStream.read()) != ';') {
                char character = (char) c;
                int frequency = inputStream.read();
                frequencyMap.put(character, frequency);
            }
            // Reconstruct the Huffman tree using character frequencies
            return buildHuffmanTree(frequencyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Function to decode the encoded text and write to the output file
    private static void decodeText(String inputFileName, String outputFileName, Node root) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFileName));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFileName, true))) {
            Node current = root;
            int c;
            while ((c = inputStream.read()) != -1) {
                // Convert byte to binary string
                String byteString = String.format("%8s", Integer.toBinaryString(c & 0xFF)).replace(' ', '0');
                // Traverse the Huffman tree to find the original character
                for (int i = 0; i < 8; i++) {
                    current = byteString.charAt(i) == '0' ? current.left : current.right;
                    if (current.left == null && current.right == null) {
                        outputStream.write(current.character);
                        current = root; // Reset to root for next character
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Character, Integer> calculateCharacterFrequencies(String fileName) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frequencyMap;
    }
}

class Node implements Comparable<Node> {
    char character;
    int frequency;
    Node left, right;

    Node(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }
}

/**
 * ps. have extensively used ChatGPT and other online resources to create this class
*/
