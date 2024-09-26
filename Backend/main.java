import java.io.File;

public class main {
    private static AutomateFiniNonDeterministe NDFA;

    // parsing the regex then converting it to NDFA
    public static void buildMyNDFA(String regEx) {
        System.out.println("regex est: " + regEx);
        RegExTree parsedRegex = RegEx.parse(regEx);

        // construction de l'automate
        NDFA = AutomateFiniNonDeterministe.depuisArbreRegExVersAFN(parsedRegex);
    }

    public static void main(String[] args) {
        String regex = "a|bc*";
        buildMyNDFA(regex);

        File folder = new File("DAAR_projet1/Backend/tests/NDFA");

        if (!folder.exists()) {
            folder.mkdirs(); // Create subfolder if it doesn't exist
        }

        String filename = "DAAR_projet1/Backend/tests/NDFA/" + regex + ".json";
        NDFA.toJsonFile(filename);

        System.out.println("dot created.");
    }
}
