package test.step5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import src.step5_KMP.KMP_algo;

import static org.junit.jupiter.api.Assertions.*;

class KMPAlgoTest {

    @Test
    void buildRetentionTable() {
        // Test 1: F = [m,a,m,a,m,i,a]
        String pattern1 = "mamamia";
        int[] expected1 = {-1, 0, -1, 0, -1, 3, 0, 0};
        Assertions.assertArrayEquals(expected1, KMP_algo.buildRetentionTable(pattern1),
                "The retention table for pattern 'mamamia' should match the expected output.");

        // Test 2: F = [c,h,i,c,h,a]
        String pattern2 = "chicha";
        int[] expected2 = {-1, 0, 0, -1, 0, 2, 0};
        assertArrayEquals(expected2, KMP_algo.buildRetentionTable(pattern2),
                "The retention table for pattern 'chicha' should match the expected output.");

        // Test 3: F = [S,a,r,g,o,n]
        String pattern3 = "Sargon";
        int[] expected3 = {-1, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected3, KMP_algo.buildRetentionTable(pattern3),
                "The retention table for pattern 'Sargon' should match the expected output.");
    }
}
