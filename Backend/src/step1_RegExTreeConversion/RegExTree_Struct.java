package src.step1_RegExTreeConversion;

import java.util.ArrayList;

/**
 * This class represents a node in a tree structure specifically designed to parse
 * and represent regular expressions. Each node can either be a leaf node (representing
 * a single character or symbol in the regex) or an internal node (with subtrees that represent
 * operators like concatenation, alternation, etc.).
 *
 * <p>The class includes methods for manipulating and retrieving information about
 * the tree's structure, such as root nodes, subtrees, and string representation of
 * the regular expression.</p>
 */
public class RegExTree_Struct {

    /**
     * The root of the current node. This typically represents a character or
     * an operator in a regular expression (e.g., '*', '|', or '.').
     */
    protected int root;

    /**
     * A list of child nodes (subtrees) of this node. This allows for the representation
     * of more complex regular expressions.
     */
    public ArrayList<RegExTree_Struct> subTrees;

    /**
     * Constructs a new RegExTree node with a root value and a list of subtrees.
     *
     * @param root The integer value representing the root of the tree (usually a character
     *             or an operator).
     * @param subTrees The list of subtrees or child nodes.
     */
    public RegExTree_Struct(int root, ArrayList<RegExTree_Struct> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    /**
     * Constructs a new leaf node (a node without any subtrees).
     *
     * @param root The integer value representing the root of the tree (usually a character
     *             or an operator).
     */
    public RegExTree_Struct(int root) {
        this.root = root;
        this.subTrees = new ArrayList<RegExTree_Struct>();
    }

    /**
     * Returns a string representation of the tree in a parenthesized format.
     *
     * <p>For example, if the tree represents the expression 'a|b', the output
     * might be 'a,(b)' where '|' is the root operator.</p>
     *
     * @return A string representation of the regular expression tree.
     */
    @Override
    public String toString() {
        if (subTrees.isEmpty())
            return rootToString();
        StringBuilder result = new StringBuilder(rootToString() + "(" + subTrees.getFirst().toString());
        for (int i = 1; i < subTrees.size(); i++)
            result.append(",").append(subTrees.get(i).toString());
        return result + ")";
    }

    /**
     * Recursively finds and returns the leftmost leaf node of the tree.
     *
     * @return The leftmost leaf node, or null if there are no subtrees.
     */
    public RegExTree_Struct getLeaf() {
        if (subTrees.isEmpty())
            return this;
        return subTrees.getFirst().getLeaf();
    }

    /**
     * Returns the root value of this node.
     *
     * @return The integer value representing the root of the node.
     */
    public int getRoot() {
        return root;
    }

    /**
     * Sets the root value of this node.
     *
     * @param root The integer value to set as the root of this node.
     */
    public void setRoot(int root) {
        this.root = root;
    }

    /**
     * Returns the list of subtrees (child nodes) of this node.
     *
     * @return An {@code ArrayList} containing the subtrees of this node.
     */
    public ArrayList<RegExTree_Struct> getSubTree() {
        return subTrees;
    }

    /**
     * Sets the list of subtrees (child nodes) for this node.
     *
     * @param subTrees The {@code ArrayList} of subtrees to set.
     */
    public void setSubTree(ArrayList<RegExTree_Struct> subTrees) {
        this.subTrees = subTrees;
    }

    /**
     * Returns the right subtree (second child) of this node, if it exists.
     *
     * @return The right subtree, or null if there is no second child.
     */
    public RegExTree_Struct getRightTree() {
        if (subTrees.size() <= 1)
            return null;
        return subTrees.get(1);
    }

    /**
     * Returns the left subtree (first child) of this node, if it exists.
     *
     * @return The left subtree, or null if there are no subtrees.
     */
    public RegExTree_Struct getLeftTree() {
        if (subTrees.isEmpty())
            return null;
        return subTrees.getFirst();
    }

    /**
     * Converts the root integer value to its corresponding string representation.
     *
     * <p>For example, it maps the root values representing the regex operators '*' and '|'
     * to their string equivalents, and characters to their string form.</p>
     *
     * @return The string representation of the root node.
     */
    private String rootToString() {
        if (root == RegExTree_from_RegEx_Parser.CONCAT)
            return ".";
        if (root == RegExTree_from_RegEx_Parser.ETOILE)
            return "*";
        if (root == RegExTree_from_RegEx_Parser.ALTERN)
            return "|";
        if (root == RegExTree_from_RegEx_Parser.DOT)
            return ".";
        return Character.toString((char) root);
    }

    /**
     * Determines if this node is a leaf node (i.e., it has no subtrees).
     *
     * @return {@code true} if this node is a leaf, {@code false} otherwise.
     */
    public boolean isLeaf() {
        return subTrees.isEmpty();
    }

    /**
     * Determines if this node's root matches a specific character.
     *
     * @param r The character to compare with the root value.
     * @return {@code true} if the root matches the given character, {@code false} otherwise.
     */
    public boolean isRoot(char r) {
        return root == (int) r;
    }
}