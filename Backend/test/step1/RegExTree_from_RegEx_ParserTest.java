package test.step1;

import org.junit.jupiter.api.Test;
import src.step1_RegExTreeConversion.RegExTree_Struct;
import src.step1_RegExTreeConversion.RegExTree_from_RegEx_Parser;

import static org.junit.jupiter.api.Assertions.*;

class RegExTree_from_RegEx_ParserTest {

    /**
     * Test parsing a valid regular expression with alternation ('|').
     */
    @Test
    void testParseValidAlternation() throws Exception {
        String regex = "a|b";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: alternation 'a|b'
        assertEquals("|(a,b)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with concatenation.
     */
    @Test
    void testParseValidConcatenation() throws Exception {
        String regex = "ab";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: concatenation 'a.b'
        assertEquals(".(a,b)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with Kleene star ('*').
     */
    @Test
    void testParseValidKleeneStar() throws Exception {
        String regex = "a*";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: Kleene star 'a*'
        assertEquals("*(a)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with parentheses and alternation.
     */
    @Test
    void testParseWithParenthesesAndAlternation() throws Exception {
        String regex = "(a|b)";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: alternation inside parentheses '(a|b)'
        assertEquals("|(a,b)", result.toString());
    }

    /**
     * Test parsing a valid complex regular expression with alternation, concatenation, and Kleene star.
     */
    @Test
    void testParseComplexRegex() throws Exception {
        String regex = "a|bc*";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: alternation 'a|(b.c*)'
        assertEquals("|(a,.(b,*(c)))", result.toString());
    }

    /**
     * Test parsing an invalid regular expression with a misplaced Kleene star.
     */
    @Test
    void testParseInvalidRegexMisplacedStar() {
        String regex = "a|*b";  // Invalid regex: * cannot be placed before b

        // Expect an exception due to invalid regex syntax
        Exception exception = assertThrows(Exception.class, () -> {
            RegExTree_from_RegEx_Parser.parse(regex);
        });

        // Validate the exception message
        assertEquals("Invalid regex: '*' cannot be applied without a valid operand.", exception.getMessage());
    }


    /**
     * Test parsing an empty regular expression.
     */
    @Test
    void testParseEmptyRegex() {
        String regex = "";  // Empty regex

        // Expect an exception due to empty regex
        assertThrows(Exception.class, () -> {
            RegExTree_from_RegEx_Parser.parse(regex);
        });
    }

    /**
     * Test parsing a regular expression with nested parentheses and Kleene star.
     */
    @Test
    void testParseNestedParentheses() throws Exception {
        String regex = "(a|(b|c)*d)";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: concatenation of 'a|(b|c)*d'
        assertEquals("|(a,.(*(|(b,c)),d))", result.toString());
    }

    /**
     * Test parsing a regular expression with multiple concatenations and Kleene star.
     */
    @Test
    void testParseMultipleConcatenations() throws Exception {
        String regex = "ab*c";
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: concatenation 'a.b*.c'
        assertEquals(".(.(a,*(b)),c)", result.toString());
    }

    /**
     * Test parsing a valid regular expression with escaped characters.
     */
    @Test
    void testParseEscapedCharacters() throws Exception {
        String regex = "a\\.b";  // Escaped dot means literal '.' character
        RegExTree_Struct result = RegExTree_from_RegEx_Parser.parse(regex);

        // Expected tree: concatenation 'a.\.b'
        assertEquals(".(.(.(a,\\),.),b)", result.toString());
    }
}
