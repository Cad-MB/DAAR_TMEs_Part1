package src.kmp;

public class KMPAlgorithm {

    /**
     * Constructs the retention table (also known as the partial match table) for the KMPAlgorithm algorithm.
     * This table is used to skip unnecessary comparisons when a mismatch occurs.
     *
     * @param pattern The pattern for which the retention table is to be constructed.
     * @return An array representing the retention table. The length of this array is pattern.length() + 1.
     */
    public static int[] buildRetentionTable(String pattern) {
        int n = pattern.length();
        int[] retention = new int[n + 1]; // Retention table of size n+1
        int j = -1; // Initialize j to -1

        // Set the first position of the table to -1
        retention[0] = -1;

        // Loop to construct the retention table
        for (int i = 1; i <= n; i++) {
            // Adjust j while there is a mismatch
            while (j >= 0 && pattern.charAt(j) != pattern.charAt(i - 1)) {
                j = retention[j];
            }
            j++;

            // If the next characters match, copy the value from retention[j]
            if (i < n && pattern.charAt(i) == pattern.charAt(j)) {
                retention[i] = retention[j];
            } else {
                retention[i] = j;
            }
        }

        return retention; // Return the retention table
    }
}
