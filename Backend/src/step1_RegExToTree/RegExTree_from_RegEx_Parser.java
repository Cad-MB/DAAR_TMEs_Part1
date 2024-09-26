package src.step1_RegExToTree;

import java.util.ArrayList;
import java.lang.Exception;

/**
 * The {@code RegExTree_from_RegEx_Parser} class is responsible for parsing regular expressions
 * and constructing a syntax tree representation of the regular expression.
 * It includes methods to handle regex syntax, such as concatenation, alternation,
 * and the Kleene star operation.
 *
 * <p>This class provides functionality to parse a regex string and convert it
 * into a tree structure represented by {@code RegExTree_Struct} objects. It also
 * includes macros for commonly used regex operators.</p>
 */
public class RegExTree_from_RegEx_Parser {

  // MACROS FOR REGEX OPERATIONS
  /**
   * Macro representing concatenation in the regular expression syntax tree.
   */
  public static final int CONCAT = 0xC04CA7;

  /**
   * Macro representing the Kleene star (repetition) in the regular expression syntax tree.
   */
  public static final int ETOILE = 0xE7011E;

  /**
   * Macro representing alternation (OR operator) in the regular expression syntax tree.
   */
  public static final int ALTERN = 0xA17E54;

  /**
   * Macro for protecting expressions enclosed in parentheses.
   */
  public static final int PROTECTION = 0xBADDAD;

  /**
   * Macro representing an opening parenthesis in the regular expression.
   */
  public static final int PARENTHESEOUVRANT = 0x16641664;

  /**
   * Macro representing a closing parenthesis in the regular expression.
   */
  public static final int PARENTHESEFERMANT = 0x51515151;

  /**
   * Macro representing the '.' (dot) character, which can be used to match any character.
   */
  public static final int DOT = 0xD07;

  // REGEX STRING
  private static String regEx;

  /**
   * Default constructor. The class is mostly used in a static context, so no
   * initialization is needed here.
   */
  public RegExTree_from_RegEx_Parser() {
  }

  /**
   * Parses a given regular expression string and converts it into a syntax tree.
   * This method is primarily used for testing purposes.
   *
   * @param regEx The regular expression to parse.
   * @return A {@code RegExTree_Struct} representing the syntax tree of the parsed regex.
   */
  public static RegExTree_Struct parse(String regEx) {
    ArrayList<RegExTree_Struct> result = new ArrayList<>();
    for (int i = 0; i < regEx.length(); i++)
      result.add(new RegExTree_Struct(charToRoot(regEx.charAt(i)), new ArrayList<>()));
    try {
      return parse(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Parses the {@code regEx} string and converts it into a syntax tree.
   * This method processes the expression using different regex operators like parentheses,
   * Kleene star, concatenation, and alternation.
   *
   * @return A {@code RegExTree_Struct} representing the syntax tree of the parsed regex.
   * @throws Exception If there is a syntax error in the regex.
   */
  private static RegExTree_Struct parse() throws Exception {
    // Debug: can be used for debugging
      RegExTree_Struct example = exampleAhoUllman();

      ArrayList<RegExTree_Struct> result = new ArrayList<>();
    for (int i = 0; i < regEx.length(); i++)
      result.add(new RegExTree_Struct(charToRoot(regEx.charAt(i)), new ArrayList<>()));

    return parse(result);
  }

  /**
   * Converts a character to its corresponding regex operation or keeps it as a literal.
   *
   * @param c The character to convert.
   * @return The integer representing the corresponding regex operation or the character's ASCII value.
   */
  private static int charToRoot(char c) {
    if (c == '.')
      return DOT;
    if (c == '*')
      return ETOILE;
    if (c == '|')
      return ALTERN;
    if (c == '(')
      return PARENTHESEOUVRANT;
    if (c == ')')
      return PARENTHESEFERMANT;
    return (int) c;
  }

  /**
   * Processes the regex tree and builds the final structure.
   * Handles parentheses, Kleene star, concatenation, and alternation operators.
   *
   * @param result The list of regex tree nodes to process.
   * @return The root of the processed syntax tree.
   * @throws Exception If there is a syntax error during processing.
   */
  private static RegExTree_Struct parse(ArrayList<RegExTree_Struct> result) throws Exception {
    while (containParenthese(result))
      result = processParenthese(result);
    while (containEtoile(result))
      result = processEtoile(result);
    while (containConcat(result))
      result = processConcat(result);
    while (containAltern(result))
      result = processAltern(result);

    if (result.size() > 1)
      throw new Exception();

    return removeProtection(result.getFirst());
  }

  /**
   * Checks if the list of regex trees contains parentheses.
   *
   * @param trees The list of regex trees to check.
   * @return {@code true} if parentheses are found, otherwise {@code false}.
   */
  private static boolean containParenthese(ArrayList<RegExTree_Struct> trees) {
    for (RegExTree_Struct t : trees)
      if (t.root == PARENTHESEFERMANT || t.root == PARENTHESEOUVRANT)
        return true;
    return false;
  }

  /**
   * Processes the regex trees to handle parentheses and create subtrees for protected expressions.
   *
   * @param trees The list of regex trees to process.
   * @return A new list of trees with parentheses processed.
   * @throws Exception If there is a syntax error.
   */
  private static ArrayList<RegExTree_Struct> processParenthese(ArrayList<RegExTree_Struct> trees) throws Exception {
    ArrayList<RegExTree_Struct> result = new ArrayList<RegExTree_Struct>();
    boolean found = false;
    for (RegExTree_Struct t : trees) {
      if (!found && t.root == PARENTHESEFERMANT) {
        boolean done = false;
        ArrayList<RegExTree_Struct> content = new ArrayList<RegExTree_Struct>();
        while (!done && !result.isEmpty())
          if (result.getLast().root == PARENTHESEOUVRANT) {
            done = true;
            result.removeLast();
          } else
            content.addFirst(result.removeLast());
        if (!done)
          throw new Exception();
        found = true;
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<RegExTree_Struct>();
        subTrees.add(parse(content));
        result.add(new RegExTree_Struct(PROTECTION, subTrees));
      } else {
        result.add(t);
      }
    }
    if (!found)
      throw new Exception();
    return result;
  }

  /**
   * Checks if the list of regex trees contains a Kleene star operation.
   *
   * @param trees The list of regex trees to check.
   * @return {@code true} if a Kleene star is found, otherwise {@code false}.
   */
  private static boolean containEtoile(ArrayList<RegExTree_Struct> trees) {
    for (RegExTree_Struct t : trees)
      if (t.root == ETOILE && t.subTrees.isEmpty())
        return true;
    return false;
  }

  /**
   * Processes the regex trees to handle the Kleene star operation and updates the tree structure.
   *
   * @param trees The list of regex trees to process.
   * @return A new list of trees with the Kleene star operation processed.
   * @throws Exception If there is a syntax error.
   */
  private static ArrayList<RegExTree_Struct> processEtoile(ArrayList<RegExTree_Struct> trees) throws Exception {
    ArrayList<RegExTree_Struct> result = new ArrayList<>();
    boolean found = false;
    for (RegExTree_Struct t : trees) {
      if (!found && t.root == ETOILE && t.subTrees.isEmpty()) {
        if (result.isEmpty()) {
          throw new Exception("Invalid regex: '*' cannot be applied without a valid operand.");
        }
        found = true;
        RegExTree_Struct last = result.removeLast();
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
        subTrees.add(last);
        result.add(new RegExTree_Struct(ETOILE, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  /**
   * Checks if the list of regex trees contains a concatenation operation.
   *
   * @param trees The list of regex trees to check.
   * @return {@code true} if a concatenation is found, otherwise {@code false}.
   */
  private static boolean containConcat(ArrayList<RegExTree_Struct> trees) {
    boolean firstFound = false;
    for (RegExTree_Struct t : trees) {
      if (!firstFound && t.root != ALTERN) {
        firstFound = true;
        continue;
      }
      if (firstFound)
        if (t.root != ALTERN)
          return true;
        else
          firstFound = false;
    }
    return false;
  }

  /**
   * Processes the regex trees to handle concatenation and updates the tree structure.
   *
   * @param trees The list of regex trees to process.
   * @return A new list of trees with the concatenation operation processed.
   * @throws Exception If there is a syntax error.
   */
  private static ArrayList<RegExTree_Struct> processConcat(ArrayList<RegExTree_Struct> trees) throws Exception {
    ArrayList<RegExTree_Struct> result = new ArrayList<RegExTree_Struct>();
    boolean found = false;
    boolean firstFound = false;
    for (RegExTree_Struct t : trees) {
      if (!found && !firstFound && t.root != ALTERN) {
        firstFound = true;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root == ALTERN) {
        firstFound = false;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root != ALTERN) {
        found = true;
        RegExTree_Struct last = result.removeLast();
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<RegExTree_Struct>();
        subTrees.add(last);
        subTrees.add(t);
        result.add(new RegExTree_Struct(CONCAT, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  /**
   * Checks if the list of regex trees contains an alternation (OR) operation.
   *
   * @param trees The list of regex trees to check.
   * @return {@code true} if an alternation is found, otherwise {@code false}.
   */
  private static boolean containAltern(ArrayList<RegExTree_Struct> trees) {
    for (RegExTree_Struct t : trees)
      if (t.root == ALTERN && t.subTrees.isEmpty())
        return true;
    return false;
  }

  /**
   * Processes the regex trees to handle alternation (OR) and updates the tree structure.
   *
   * @param trees The list of regex trees to process.
   * @return A new list of trees with the alternation operation processed.
   * @throws Exception If there is a syntax error.
   */
  private static ArrayList<RegExTree_Struct> processAltern(ArrayList<RegExTree_Struct> trees) throws Exception {
    ArrayList<RegExTree_Struct> result = new ArrayList<RegExTree_Struct>();
    boolean found = false;
    RegExTree_Struct gauche = null;
    boolean done = false;
    for (RegExTree_Struct t : trees) {
      if (!found && t.root == ALTERN && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        gauche = result.removeLast();
        continue;
      }
      if (found && !done) {
        if (gauche == null)
          throw new Exception();
        done = true;
        ArrayList<RegExTree_Struct> subTrees = new ArrayList<RegExTree_Struct>();
        subTrees.add(gauche);
        subTrees.add(t);
        result.add(new RegExTree_Struct(ALTERN, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  /**
   * Removes protection nodes from the syntax tree. Protection nodes are used internally
   * to handle parentheses and should not appear in the final syntax tree.
   *
   * @param tree The syntax tree to process.
   * @return The tree with protection nodes removed.
   * @throws Exception If an invalid protection node is found.
   */
  private static RegExTree_Struct removeProtection(RegExTree_Struct tree) throws Exception {
    if (tree.root == PROTECTION && tree.subTrees.size() != 1)
      throw new Exception();
    if (tree.subTrees.isEmpty())
      return tree;
    if (tree.root == PROTECTION)
      return removeProtection(tree.subTrees.getFirst());

    ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
    for (RegExTree_Struct t : tree.subTrees)
      subTrees.add(removeProtection(t));
    return new RegExTree_Struct(tree.root, subTrees);
  }

  /**
   * Example from Aho-Ullman book (Chap.10 Example 10.25) that builds a sample regex tree
   * for the expression 'a|bc*'.
   *
   * @return A {@code RegExTree_Struct} representing the example syntax tree.
   */
  private static RegExTree_Struct exampleAhoUllman() {
    RegExTree_Struct a = new RegExTree_Struct((int) 'a', new ArrayList<>());
    RegExTree_Struct b = new RegExTree_Struct((int) 'b', new ArrayList<>());
    RegExTree_Struct c = new RegExTree_Struct((int) 'c', new ArrayList<>());
    ArrayList<RegExTree_Struct> subTrees = new ArrayList<>();
    subTrees.add(c);
    RegExTree_Struct cEtoile = new RegExTree_Struct(ETOILE, subTrees);
    subTrees = new ArrayList<>();
    subTrees.add(b);
    subTrees.add(cEtoile);
    RegExTree_Struct dotBCEtoile = new RegExTree_Struct(CONCAT, subTrees);
    subTrees = new ArrayList<>();
    subTrees.add(a);
    subTrees.add(dotBCEtoile);
    return new RegExTree_Struct(ALTERN, subTrees);
  }
}
