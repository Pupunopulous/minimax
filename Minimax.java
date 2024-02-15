// Author: Rahi Krishna / rk4748
import java.io.*;
import java.util.*;
import java.lang.*;

public class Minimax {

    // Defines a node in the tree
    public static class Node {
        // Alphanumeric name of the node
        private final String data;

        // Children of the node
        private final List<Node> children;

        // Value of the node (only for leaf nodes)
        private int value;
        public Node(String data) {
            this.data = data;
            this.value = Integer.MIN_VALUE;
            this.children = new ArrayList<>();;
        }

        // Getters and setters
        public void setValue(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
        public String getData() {
            return this.data;
        }
        public List<Node> getChildren() {
            return children;
        }

        // Utility function to add a child to a pre-existing node
        public void addChild(Node child) {
            children.add(child);
        }
    }

    // Builds a tree with the given root and children
    public static Node buildTree(String rootKey, Map<String, List<String>> relations, Map<String, String> variables) {
        Node root = new Node(rootKey);
        // Build tree from the input maps
        if (variables.containsKey(rootKey))
            root.setValue(Integer.parseInt(variables.get(rootKey)));
        if (relations.containsKey(rootKey) && !relations.get(rootKey).isEmpty()) {
            for (String childKey : relations.get(rootKey)) {
                Node child = buildTree(childKey, relations, variables);
                root.addChild(child);
            }
        }
        return root;
    }

    // List to store all verbose evaluation messages
    private static final List<String> verboseMessages = new ArrayList<>();

    // Evaluates the minimax solution of the tree
    public static int evaluateTree(Node root, Node parent, Node originalRoot, boolean isMaxPlayer, int range, boolean verbose) {
        if (root.getChildren().isEmpty()) {
            // Base case: leaf nodes return assigned values
            if (root.getValue() != Integer.MIN_VALUE)
                return root.getValue();
            // Leaf node not found error
            System.out.println("child node \"" + root.getData() + "\" of \"" + parent.getData() + "\" not found");
            System.exit(0);
        }
        Node chosenChild = null;
        int chosenValue = isMaxPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Node child : root.getChildren()) {
            // If current level is min, next level is max and vice-versa
            int eval = evaluateTree(child, root, originalRoot, !isMaxPlayer, range, verbose);
            if ((isMaxPlayer && eval > chosenValue) || (!isMaxPlayer && eval < chosenValue)) {
                chosenChild = child;
                chosenValue = eval;
            }
            // Max/min cutoff
            if (isMaxPlayer && eval >= range || !isMaxPlayer && eval <= -range) break;
        }
        // Check for "verbose" flag
        if (verbose || root == originalRoot) {
            String playerType = isMaxPlayer ? "max" : "min";
            String message = playerType + "(" + root.getData() + ") chooses " + chosenChild.getData() + " for " + chosenValue;
            verboseMessages.add(message);
        }
        return chosenValue;
    }

    // Evaluates the minimax alpha-beta pruned solution of the tree
    public static int evaluateTree(Node root, Node parent, Node originalRoot, boolean isMaxPlayer, int alpha, int beta, int range, boolean verbose) {
        if (root.getChildren().isEmpty()) {
            // Base case: leaf nodes return assigned values
            if (root.getValue() != Integer.MIN_VALUE)
                return root.getValue();
            // Leaf node not found error
            System.out.println("child node \"" + root.getData() + "\" of \"" + parent.getData() + "\" not found");
            System.exit(0);
        }
        Node chosenChild = null;
        boolean isPruned = false;
        int chosenValue = isMaxPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Node child : root.getChildren()) {
            // If current level is min, next level is max and vice-versa
            int eval = evaluateTree(child, root, originalRoot, !isMaxPlayer, alpha, beta, range, verbose);
            if (isMaxPlayer) {
                if (eval > chosenValue) {
                    chosenChild = child;
                    chosenValue = eval;
                }
                alpha = Math.max(alpha, eval);
                // Max-cutoff
                if (eval >= range) {
                    break;
                }
            } else {
                if (eval < chosenValue) {
                    chosenChild = child;
                    chosenValue = eval;
                }
                beta = Math.min(beta, eval);
                // Min-cutoff
                if (eval <= -range) {
                    break;
                }
            }
            // Alpha-beta pruning
            if (beta <= alpha) {
                isPruned = true;
                break;
            }
        }
        // Check for "verbose" flag and print the non-pruned evaluated nodes
        if ((!isPruned && verbose) || root == originalRoot) {
            String playerType = isMaxPlayer ? "max" : "min";
            String message = playerType + "(" + root.getData() + ") chooses " + chosenChild.getData() + " for " + chosenValue;
            verboseMessages.add(message);
        }
        return chosenValue;
    }

    // Finding the root(s) of the tree
    public static List<String> findRoots(Map<String, List<String>> relations) {
        Set<String> allNodes = new HashSet<>();
        Set<String> childNodes = new HashSet<>();

        // Populate 'allNodes' and 'childNodes' sets
        for (Map.Entry<String, List<String>> entry : relations.entrySet()) {
            String parent = entry.getKey();
            List<String> children = entry.getValue();
            allNodes.add(parent);
            childNodes.addAll(children);
        }
        List<String> roots = new ArrayList<>();
        // Iterate through all nodes to find roots
        for (String node : allNodes) {
            if (!childNodes.contains(node)) {
                roots.add(node);
            }
        }
        return roots;
    }

    // Checking for loops inside the tree (directed-cyclic)
    public static boolean hasLoop(String node, Map<String, List<String>> relations, Set<String> visitedNodes) {
        // Check if the node is already visited
        if (visitedNodes.contains(node)) return true; // Loop detected
        visitedNodes.add(node);
        List<String> children = relations.getOrDefault(node, new ArrayList<>());
        for (String child : children) {
            if (hasLoop(child, relations, visitedNodes)) {
                return true;
            }
        }
        // Remove the node from the visited set after traversal
        visitedNodes.remove(node);
        return false;
    }

    public static void main (String[] args) {
        // Initializing command-line variables
        boolean verbose = false,
                prune = false,
                isMaxPlayer = false;
        int range = Integer.MAX_VALUE;
        String fileName = null;

        // Taking inline inputs
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-v" -> verbose = true;
                case "-ab" -> prune = true;
                case "-range" -> {
                    i++;
                    try {
                        range = Integer.parseInt(args[i]);
                    } catch (RuntimeException e) {
                        // If range is in wrong format
                        System.out.println("Incorrect range argument passed.");
                        System.exit(0);
                    }
                }
                case "max" -> isMaxPlayer = true;
                case "min" -> isMaxPlayer = false;
                default -> {
                    // Regex for alphanumeric filename (including underscores)
                    if (args[i].matches("[a-zA-Z0-9_]+\\.txt")) fileName = args[i];
                    else {
                        System.out.println("One or more incorrect arguments were passed.");
                        System.exit(0);
                    }
                }
            }
        }

        try {
            // Initializing variables and parent-child relation maps
            Map<String, String> variables = new HashMap<>();
            Map<String, List<String>> relations = new HashMap<>();

            // Open the specified input file
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            // Variables to store the root node and check for multiple roots
            boolean isFirstIteration = true;
            String rootNode = null;

            // Read each line in the file
            while ((line = reader.readLine()) != null) {
                // Trim whitespace
                line = line.trim();
                // Ignore comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) continue;

                // If line has "=", it is a variable (leaf value) assignment
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    String variableName = parts[0].trim();
                    String variableValue = parts[1].trim();
                    // Terminate program if leaf values are out of range bounds
                    int value = Integer.parseInt(variableValue);
                    if (value > Math.abs(range) || value < -Math.abs(range)) {
                        System.out.println("Input values are out of range.");
                        System.exit(0);
                    }
                    // Add variable to the variable map
                    variables.put(variableName, variableValue);
                }

                // If line has ":", it is a parent-child assignment
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    String parent = parts[0].trim();
                    List<String> children = Arrays.asList(
                            parts[1].trim()
                            .replaceAll("\\s", "")
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .split(",")
                    );
                    relations.put(parent, children);
                }
            }

            // Find the root of the tree from given input
            List<String> roots = findRoots(relations);
            int numberOfRoots = roots.size();
            if (numberOfRoots == 1) rootNode = roots.get(0);
            // Multiple root nodes
            else if (numberOfRoots > 1) {
                System.out.print("multiple roots: ");
                for (int i = 0; i < numberOfRoots; i++) {
                    System.out.print("\"" + roots.get(i) + "\"");
                    if (i == numberOfRoots - 2) System.out.print(" and ");
                    else if (i < numberOfRoots - 1) System.out.print(", ");
                }
                System.out.println();
                System.exit(0);
            } else {
                System.out.println("Invalid input. No root found.");
                System.exit(0);
            }

            // Check if the tree has a loop
            if (hasLoop(rootNode, relations, new HashSet<>())) {
                System.out.println("Invalid input. Tree contains a cycle.");
                System.exit(0);
            }

            // Build tree using the first variable
            Node root = buildTree(rootNode, relations, variables);

            // If user requires pruning
            if (prune) evaluateTree(root, new Node("dummy"), root, isMaxPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE, Math.abs(range), verbose);
            // If user does not require pruning
            else evaluateTree(root, new Node("dummy"), root, isMaxPlayer, Math.abs(range), verbose);

            // Print all evaluation messages
            for (String s: verboseMessages) System.out.println(s);
            reader.close();

        // Catching input file and directory errors
        } catch (IOException e) {
            System.out.println("Input file does not exist in this directory.");
        } catch (NullPointerException e) {
            System.out.println("No input file specified.");
        }

    }
}
