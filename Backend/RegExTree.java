//UTILITARY CLASS 

import java.util.ArrayList;

public class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;

    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    // CONSTRUCTOR FOR LEAF NODES
    public RegExTree(int root) {
        this.root = root;
        this.subTrees = new ArrayList<RegExTree>();
    }

    // FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty())
            return rootToString();
        String result = rootToString() + "(" + subTrees.get(0).toString();
        for (int i = 1; i < subTrees.size(); i++)
            result += "," + subTrees.get(i).toString();
        return result + ")";
    }

    // function to get the leaf node of the tree
    public RegExTree getLeaf() {
        if (subTrees.size() == 0)
            return null;
        return subTrees.get(0).getLeaf();
    }

    // function to get the root node of the tree
    public int getRoot() {
        return root;
    }

    // function to set the root node of the tree
    public void setRoot(int root) {
        this.root = root;
    }

    // function to get the subtree of the tree
    public ArrayList<RegExTree> getSubTree() {
        return subTrees;
    }

    // function to set the subtree of the tree
    public void setSubTree(ArrayList<RegExTree> subTrees) {
        this.subTrees = subTrees;
    }

    // function to get the right subtree of the tree
    public RegExTree getRightTree() {
        if (subTrees.size() <= 1)
            return null;
        return subTrees.get(1);
    }

    // function to get the left subtree of the tree
    public RegExTree getLeftTree() {
        if (subTrees.size() == 0)
            return null;
        return subTrees.get(0);
    }

    private String rootToString() {
        if (root == RegEx.CONCAT)
            return ".";
        if (root == RegEx.ETOILE)
            return "*";
        if (root == RegEx.ALTERN)
            return "|";
        if (root == RegEx.DOT)
            return ".";
        return Character.toString((char) root);
    }

    public boolean isLeaf() {
        return subTrees.isEmpty();
    }

    public boolean isRoot(char r) {
        return root == (int) r;
    }
}
