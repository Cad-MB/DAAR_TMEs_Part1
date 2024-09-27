package test.step1;

import org.junit.jupiter.api.Test;
import src.step1_RegExTreeConversion.RegExTree_Struct;
import src.step1_RegExTreeConversion.RegExTree_from_RegEx_Parser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegExTree_structTest {

    @Test
    void testToString() {
        RegExTree_Struct leaf = new RegExTree_Struct((int)'a'); // 'a'
        assertEquals("a", leaf.toString());

        RegExTree_Struct left = new RegExTree_Struct((int)'a'); // 'a'
        RegExTree_Struct right = new RegExTree_Struct((int)'b'); // 'b'
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree_Struct root = new RegExTree_Struct(RegExTree_from_RegEx_Parser.CONCAT, subTrees);

        assertEquals(".(a,b)", root.toString());
    }

    @Test
    void getLeaf() {
        RegExTree_Struct leaf = new RegExTree_Struct((int)'a'); // 'a'
        assertEquals(leaf, leaf.getLeaf());  // A leaf node should return itself

        RegExTree_Struct tree = new RegExTree_Struct(RegExTree_from_RegEx_Parser.CONCAT, new ArrayList<>(List.of(leaf)));
        assertEquals(leaf, tree.getLeaf());  // The leftmost leaf of the tree should be the original leaf node
    }

    @Test
    void getRoot() {
        RegExTree_Struct tree = new RegExTree_Struct((int)'a'); // 'a'
        assertEquals((int)'a', tree.getRoot());
    }

    @Test
    void setRoot() {
        RegExTree_Struct tree = new RegExTree_Struct((int)'a'); // 'a'
        tree.setRoot((int)'b'); // 'b'
        assertEquals((int)'b', tree.getRoot());
    }

    @Test
    void getSubTree() {
        RegExTree_Struct left = new RegExTree_Struct((int)'a'); // 'a'
        RegExTree_Struct right = new RegExTree_Struct((int)'b'); // 'b'
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree_Struct root = new RegExTree_Struct(RegExTree_from_RegEx_Parser.CONCAT, subTrees);

        assertEquals(2, root.getSubTree().size());
        assertEquals(left, root.getSubTree().get(0));
        assertEquals(right, root.getSubTree().get(1));
    }

    @Test
    void setSubTree() {
        RegExTree_Struct tree = new RegExTree_Struct((int)'a'); // 'a'
        RegExTree_Struct newSubTree = new RegExTree_Struct((int)'b'); // 'b'
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(newSubTree);
        tree.setSubTree(subTrees);

        assertEquals(1, tree.getSubTree().size());
        assertEquals(newSubTree, tree.getSubTree().get(0));
    }

    @Test
    void getRightTree() {
        RegExTree_Struct left = new RegExTree_Struct((int)'a'); // 'a'
        RegExTree_Struct right = new RegExTree_Struct((int)'b'); // 'b'
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree_Struct root = new RegExTree_Struct(RegExTree_from_RegEx_Parser.CONCAT, subTrees);

        assertEquals(right, root.getRightTree());
    }

    @Test
    void getLeftTree() {
        RegExTree_Struct left = new RegExTree_Struct((int)'a'); // 'a'
        RegExTree_Struct right = new RegExTree_Struct((int)'b'); // 'b'
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(left);
        subTrees.add(right);
        RegExTree_Struct root = new RegExTree_Struct(RegExTree_from_RegEx_Parser.CONCAT, subTrees);

        assertEquals(left, root.getLeftTree());
    }

    @Test
    void isLeaf() {
        // Create a leaf node
        RegExTree_Struct leaf = new RegExTree_Struct((int)'a'); // 'a'
        assertTrue(leaf.isLeaf());

        // Create a non-leaf node with the leaf as a child
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(leaf);  // Add the leaf as a subtree
        RegExTree_Struct nonLeaf = new RegExTree_Struct((int)'a', subTrees);
        assertFalse(nonLeaf.isLeaf());  // Since it has a subtree, it is not a leaf
    }

    @Test
    void isRoot() {
        RegExTree_Struct tree = new RegExTree_Struct((int)'a'); // 'a'
        assertTrue(tree.isRoot('a'));
        assertFalse(tree.isRoot('b'));
    }
}
