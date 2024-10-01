package test.kmp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import src.kmp.KMPAlgorithm;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class KMPAlgorithmTest {

    @Test
    void buildRetentionTable() {
        // Test 1: F = [m,a,m,a,m,i,a]
        String pattern1 = "mamamia";
        int[] expected1 = {-1, 0, -1, 0, -1, 3, 0, 0};
        Assertions.assertArrayEquals(expected1, KMPAlgorithm.buildRetentionTable(pattern1),
                "The retention table for pattern 'mamamia' should match the expected output.");

        // Test 2: F = [c,h,i,c,h,a]
        String pattern2 = "chicha";
        int[] expected2 = {-1, 0, 0, -1, 0, 2, 0};
        assertArrayEquals(expected2, KMPAlgorithm.buildRetentionTable(pattern2),
                "The retention table for pattern 'chicha' should match the expected output.");

        // Test 3: F = [S,a,r,g,o,n]
        String pattern3 = "Sargon";
        int[] expected3 = {-1, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected3, KMPAlgorithm.buildRetentionTable(pattern3),
                "The retention table for pattern 'Sargon' should match the expected output.");

        // Test 3: F = [a,b,c,a,b,c,a,b,c,d]
        String pattern4 = "abcabcabcd";
        int[] expected4 = {-1, 0, 0, -1, 0, 0, -1, 0, 0, 3, 0};
        System.out.println(Arrays.toString(KMPAlgorithm.buildRetentionTable(pattern4)));
        assertArrayEquals(expected4, KMPAlgorithm.buildRetentionTable(pattern4),
                "The retention table for pattern 'Sargon' should match the expected output.");
    }
}
