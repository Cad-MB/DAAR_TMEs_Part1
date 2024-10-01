package test.regex;

import org.junit.jupiter.api.Test;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegExTreeTest {

    @Test
    void testToString() {
        RegExTree leaf = new RegExTree((int)'a'); // 'a'
        assertEquals("a", leaf.toString());

        RegExTree left = new RegExTree((int)'a'); // 'a'
        RegExTree right = new RegExTree((int)'b'); // 'b'
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree root = new RegExTree(RegExTreeParser.CONCAT, subTrees);

        assertEquals(".(a,b)", root.toString());
    }

    @Test
    void getLeaf() {
        RegExTree leaf = new RegExTree((int)'a'); // 'a'
        assertEquals(leaf, leaf.getLeaf());  // A leaf node should return itself

        RegExTree tree = new RegExTree(RegExTreeParser.CONCAT, new ArrayList<>(List.of(leaf)));
        assertEquals(leaf, tree.getLeaf());  // The leftmost leaf of the tree should be the original leaf node
    }

    @Test
    void getRoot() {
        RegExTree tree = new RegExTree((int)'a'); // 'a'
        assertEquals((int)'a', tree.getRoot());
    }

    @Test
    void setRoot() {
        RegExTree tree = new RegExTree((int)'a'); // 'a'
        tree.setRoot((int)'b'); // 'b'
        assertEquals((int)'b', tree.getRoot());
    }

    @Test
    void getSubTree() {
        RegExTree left = new RegExTree((int)'a'); // 'a'
        RegExTree right = new RegExTree((int)'b'); // 'b'
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree root = new RegExTree(RegExTreeParser.CONCAT, subTrees);

        assertEquals(2, root.getSubTree().size());
        assertEquals(left, root.getSubTree().get(0));
        assertEquals(right, root.getSubTree().get(1));
    }

    @Test
    void setSubTree() {
        RegExTree tree = new RegExTree((int)'a'); // 'a'
        RegExTree newSubTree = new RegExTree((int)'b'); // 'b'
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(newSubTree);
        tree.setSubTree(subTrees);

        assertEquals(1, tree.getSubTree().size());
        assertEquals(newSubTree, tree.getSubTree().get(0));
    }

    @Test
    void getRightTree() {
        RegExTree left = new RegExTree((int)'a'); // 'a'
        RegExTree right = new RegExTree((int)'b'); // 'b'
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree root = new RegExTree(RegExTreeParser.CONCAT, subTrees);

        assertEquals(right, root.getRightTree());
    }

    @Test
    void getLeftTree() {
        RegExTree left = new RegExTree((int)'a'); // 'a'
        RegExTree right = new RegExTree((int)'b'); // 'b'
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree root = new RegExTree(RegExTreeParser.CONCAT, subTrees);

        assertEquals(left, root.getLeftTree());
    }

    @Test
    void isLeaf() {
        // Create a leaf node
        RegExTree leaf = new RegExTree((int)'a'); // 'a'
        assertTrue(leaf.isLeaf());

        // Create a non-leaf node with the leaf as a child
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(leaf);  // Add the leaf as a subtree
        RegExTree nonLeaf = new RegExTree((int)'a', subTrees);
        assertFalse(nonLeaf.isLeaf());  // Since it has a subtree, it is not a leaf
    }

    @Test
    void isRoot() {
        RegExTree tree = new RegExTree((int)'a'); // 'a'
        assertTrue(tree.isRoot('a'));
        assertFalse(tree.isRoot('b'));
    }
}
