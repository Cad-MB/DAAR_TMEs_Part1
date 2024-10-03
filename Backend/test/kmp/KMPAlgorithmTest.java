package test.kmp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import src.kmp.KMPAlgorithm;

class KMPAlgorithmTest {

    @Test
    void buildRetentionTable() {
        // Test 1: F = [m,a,m,a,m,i,a]
        String pattern1 = "mamamia";
        Integer[] expected1 = {-1, 0, -1, 0, -1, 3, 0, 0};
        KMPAlgorithm kmpMatcher = new KMPAlgorithm(pattern1, "");

        kmpMatcher.generatePatternCharacters();
        kmpMatcher.generateLpsTable();

        Assertions.assertArrayEquals(expected1, kmpMatcher.getLpsTable().toArray(),
                "The retention table for pattern 'mamamia' should match the expected output.");

        // Test 2: F = [c,h,i,c,h,a]
        String pattern2 = "chicha";
        Integer[] expected2 = {-1, 0, 0, -1, 0, 2, 0};

        KMPAlgorithm kmpMatcher2 = new KMPAlgorithm(pattern2, "");

        kmpMatcher2.generatePatternCharacters();
        kmpMatcher2.generateLpsTable();

        Assertions.assertArrayEquals(expected2, kmpMatcher2.getLpsTable().toArray(),
                "The retention table for pattern 'chicha' should match the expected output.");
//
//        // Test 3: F = [S,a,r,g,o,n]
        String pattern3 = "Sargon";
        Integer[] expected3 = {-1, 0, 0, 0, 0, 0, 0};

        KMPAlgorithm kmpMatcher3 = new KMPAlgorithm(pattern3, "");

        kmpMatcher3.generatePatternCharacters();
        kmpMatcher3.generateLpsTable();

        Assertions.assertArrayEquals(expected3, kmpMatcher3.getLpsTable().toArray(),
                "The retention table for pattern 'Sargon' should match the expected output.");
//
        // Test 3: F = [a,b,c,a,b,c,a,b,c,d]
        String pattern4 = "abcabcabcd";
        Integer[] expected4 = {-1, 0, 0, -1, 0, 0, -1, 0, 0, 6, 0};
        KMPAlgorithm kmpMatcher4 = new KMPAlgorithm(pattern4, "");

        kmpMatcher4.generatePatternCharacters();
        kmpMatcher4.generateLpsTable();

        Assertions.assertArrayEquals(expected4, kmpMatcher4.getLpsTable().toArray(),
                "The retention table for pattern 'Sargon' should match the expected output.");
    }
}
