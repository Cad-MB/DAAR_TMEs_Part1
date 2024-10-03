package src.kmp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class KMPAlgorithm {

    // ANSI escape code for green text
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

    private String pattern;
    private String searchText;
    private ArrayList<String> patternCharacters;
    private ArrayList<Integer> lpsTable;

    public KMPAlgorithm(String pattern, String searchText) {
        this.pattern = pattern;
        this.searchText = searchText;
    }

    public void generatePatternCharacters() {
        this.patternCharacters = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            this.patternCharacters.add("" + pattern.charAt(i));
        }
    }

    public ArrayList<Integer> getLpsTable() {
        return this.lpsTable;
    }

    public ArrayList<String> getPrefixSublist(int index) {
        ArrayList<String> prefix = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            prefix.add(this.patternCharacters.get(i));
        }
        return prefix;
    }

    public String extractSuffix(String inputString, int length) {
        return inputString.substring(inputString.length() - length);
    }

    public String listToString(ArrayList<String> stringList) {
        StringBuilder result = new StringBuilder();
        for (String s : stringList) {
            result.append(s);
        }
        return result.toString();
    }

    public boolean isPrefix(String inputString, String suffix) {
        return inputString.startsWith(suffix);
    }

    public int longestPrefixSuffixLength(ArrayList<String> stringList) {
        String concatenatedString = listToString(stringList);
        for (int i = concatenatedString.length() - 1; i > 0; i--) {
            String suffix = extractSuffix(concatenatedString, i);
            if (isPrefix(concatenatedString, suffix)) {
                return suffix.length();
            }
        }
        return 0;
    }

    public void generateLpsTable() {
        this.lpsTable = new ArrayList<>();
        this.lpsTable.add(-1);

        for (int i = 1; i < this.pattern.length(); i++) {
            ArrayList<String> prefix = getPrefixSublist(i);
            if (prefix.size() == 1) {
                this.lpsTable.add(0);
            } else {
                int longestPrefixSuffix = longestPrefixSuffixLength(prefix);
                this.lpsTable.add(longestPrefixSuffix);
            }
        }

        for (int i = 1; i < this.patternCharacters.size(); i++) {
            if ((this.patternCharacters.get(i).equals(this.patternCharacters.get(0)))
                    && (this.lpsTable.get(i) == 0)) {
                this.lpsTable.set(i, -1);
            }
        }

        for (int i = 0; i < this.patternCharacters.size(); i++) {
            if ((this.lpsTable.get(i) != -1)
                    && (this.patternCharacters.get(i).equals(this.patternCharacters.get(this.lpsTable.get(i))))) {
                this.lpsTable.set(i, this.lpsTable.get(this.lpsTable.get(i)));
            }
        }
        this.lpsTable.add(0);
    }

    public String[] splitTextIntoLines() {
        return this.searchText.split("\n");
    }

    public String searchInSuffixes(String line) {
        int i = 0;
        while ((i < line.length()) && ((line.length() - i) >= patternCharacters.size())) {
            int matchCount = 0;
            for (int j = 0; j < patternCharacters.size(); j++) {
                if (patternCharacters.get(j).equals("" + line.charAt(j + i))) {
                    matchCount++;
                }
            }
            if (matchCount == patternCharacters.size()) {
                return line;
            } else {
                i += matchCount - lpsTable.get(matchCount);
            }
        }
        return null;
    }

    public ArrayList<String> searchPatternInText() {
        String[] lines = splitTextIntoLines();
        ArrayList<String> matchingLines = new ArrayList<>();
        for (String line : lines) {
            String result = searchInSuffixes(line);
            if (result != null) {
                matchingLines.add(result);
            }
        }
        return matchingLines;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the pattern to search for:");
        String pattern = scanner.nextLine();

        String filePath = "Backend/resources/texts/56667-0.txt";
        StringBuilder fileContent = new StringBuilder();

        try {
            Scanner fileScanner = new Scanner(new File(filePath));
            while (fileScanner.hasNextLine()) {
                fileContent.append(fileScanner.nextLine()).append("\n");
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found at " + filePath);
            return;
        }

        KMPAlgorithm kmpMatcher = new KMPAlgorithm(pattern, fileContent.toString());

        kmpMatcher.generatePatternCharacters();
        kmpMatcher.generateLpsTable();

        ArrayList<String> matchingLines = kmpMatcher.searchPatternInText();

        System.out.println("Search Results:");
        for (String line : matchingLines) {
            // Highlighting the found pattern
            String highlightedLine = line.replace(pattern, GREEN + pattern + RESET);
            System.out.println(highlightedLine);
        }
        System.out.println(matchingLines.size() + " lines found.");
    }
}
