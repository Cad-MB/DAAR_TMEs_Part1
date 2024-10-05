package test.regex;

import org.junit.jupiter.api.Test;
import src.regex.RegExTree;
import src.regex.RegExTreeParser;

import static org.junit.jupiter.api.Assertions.*;

class RegExParserTest {

    /**
     * Test parsing a valid regular expression with alternation ('|').
     */
    @Test
    void testParseValidAlternation() throws Exception {
        String regex = "a|b";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: alternation 'a|b'
        assertEquals("|(a,b)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with concatenation.
     */
    @Test
    void testParseValidConcatenation() throws Exception {
        String regex = "ab";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: concatenation 'a.b'
        assertEquals(".(a,b)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with Kleene star ('*').
     */
    @Test
    void testParseValidKleeneStar() throws Exception {
        String regex = "a*";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: Kleene star 'a*'
        assertEquals("*(a)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with parentheses and alternation.
     */
    @Test
    void testParseWithParenthesesAndAlternation() throws Exception {
        String regex = "(a|b)";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: alternation inside parentheses '(a|b)'
        assertEquals("|(a,b)", result.toString());
    }

    /**
     * Test parsing a valid complex regular expression with alternation, concatenation, and Kleene star.
     */
    @Test
    void testParseComplexRegex() throws Exception {
        String regex = "a|bc*";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: alternation 'a|(b.c*)'
        assertEquals("|(a,.(b,*(c)))", result.toString());
    }


    /**
     * Test parsing a regular expression with nested parentheses and Kleene star.
     */
    @Test
    void testParseNestedParentheses() throws Exception {
        String regex = "(a|(b|c)*d)";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: concatenation of 'a|(b|c)*d'
        assertEquals("|(a,.(*(|(b,c)),d))", result.toString());
    }

    /**
     * Test parsing a regular expression with multiple concatenations and Kleene star.
     */
    @Test
    void testParseMultipleConcatenations() throws Exception {
        String regex = "ab*c";
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: concatenation 'a.b*.c'
        assertEquals(".(.(a,*(b)),c)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with escaped characters.
     */
    @Test
    void testParseEscapedCharacters() throws Exception {
        String regex = "a\\.b";  // Escaped dot means literal '.' character
        RegExTree result = RegExTreeParser.parse(regex);

        // Expected tree: concatenation 'a.\.b'
        assertEquals(".(.(.(a,\\),.),b)", result.toString());
    }
}
