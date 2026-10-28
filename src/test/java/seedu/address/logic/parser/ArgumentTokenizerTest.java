package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentTokenizerTest {
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");

    @Test
    public void tokenize_unicodeSpaces_recognisesPrefixesAndNormalisesValues() {
        // EP: Unicode spaces and Hangul fillers separate prefixes, pad values, and occur inside values.
        for (String space : List.of("\u00a0", "\u2007", "\u2009", "\u202f", "\u3000",
                "\u115f", "\u1160", "\u3164", "\uffa0")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(space + "p/first" + space + "value"
                    + space + "-t" + space + "second" + space, pSlash, dashT);
            assertEquals("", arguments.getPreamble());
            assertValues(arguments, pSlash, "first value");
            assertValues(arguments, dashT, "second");
            assertEquals(new Prefix("x/"), ArgumentTokenizer.findUnknownPrefix(
                    "p/value" + space + "x/unknown", Set.of(pSlash)).orElseThrow());
        }
    }

    @Test
    public void tokenize_noRecognizedPrefixes_returnsTrimmedPreamble() {
        // EP: empty/blank input; ordinary text; unsupported prefixes; no configured prefixes.
        // BVA: zero input characters and zero recognized prefixes.
        for (String input : List.of("", " \t ", "  ordinary text  ", " x/value ", " P/value ")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(input, pSlash);
            assertEquals(input.trim(), arguments.getPreamble());
            assertTrue(arguments.getValue(pSlash).isEmpty());
            assertEquals(List.of(), arguments.getAllValues(pSlash));
        }
        assertEquals("p/value", ArgumentTokenizer.tokenize(" p/value ").getPreamble());
    }

    @Test
    public void tokenize_prefixBoundaries_extractsOnlyStandalonePrefixes() {
        // EP: prefix at start or after whitespace is recognized; joined prefixes remain literal text.
        // BVA: prefix at index zero; one separator character versus none.
        for (String separator : List.of("", " ", "\t", "\n", "\r", "\u2003")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(separator + "p/value", pSlash);
            assertEquals("", arguments.getPreamble());
            assertValues(arguments, pSlash, "value");
        }
        ArgumentMultimap joined = ArgumentTokenizer.tokenize("joinedp/value p/actualp/literal", pSlash);
        assertEquals("joinedp/value", joined.getPreamble());
        assertValues(joined, pSlash, "actualp/literal");
    }

    @Test
    public void tokenize_multiplePrefixes_preservesInputOrderAndInternalWhitespace() {
        // EP: several prefix types in an order different from their configuration; nonempty preamble.
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" before -t first  value p/ second\tvalue -t third ",
                pSlash, dashT);
        assertEquals("before", arguments.getPreamble());
        assertValues(arguments, dashT, "first  value", "third");
        assertValues(arguments, pSlash, "second\tvalue");
    }

    @Test
    public void tokenize_emptyAndRepeatedValues_preservesAllAndReturnsLast() {
        // EP: absent values differ from present empty values; repetitions accumulate in input order.
        // BVA: zero-length value at end of input and between adjacent recognized prefixes.
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize("p/ -t p/one p/", pSlash, dashT);
        assertValues(arguments, pSlash, "", "one", "");
        assertValues(arguments, dashT, "");
        assertValues(ArgumentTokenizer.tokenize("p/   ", pSlash), pSlash, "");
        assertValues(ArgumentTokenizer.tokenize("p/a", pSlash), pSlash, "a");
    }

    @Test
    public void tokenize_unconfiguredPrefixInValue_remainsLiteral() {
        // EP: tokenizer extracts only configured prefixes; it does not reject unknown syntax.
        assertValues(ArgumentTokenizer.tokenize("p/8/10 x/unknown https://example.com/a/b", pSlash),
                pSlash, "8/10 x/unknown https://example.com/a/b");
    }

    @Test
    public void tokenize_repeatedCalls_areIndependent() {
        // EP: tokenizing again must not retain or overwrite values from an earlier result.
        ArgumentMultimap first = ArgumentTokenizer.tokenize("p/first", pSlash);
        ArgumentMultimap second = ArgumentTokenizer.tokenize("", pSlash);
        assertValues(first, pSlash, "first");
        assertTrue(second.getValue(pSlash).isEmpty());
    }

    @Test
    public void findUnknownPrefix_unsupportedTokens_returnsFirstInInputOrder() {
        // EP: unsupported alphabetic tokens, including multi-letter and case-mismatched prefixes.
        assertEquals(new Prefix("extra/"), ArgumentTokenizer.findUnknownPrefix(
                "p/value\textra/first y/second", Set.of(pSlash)).orElseThrow());
        assertEquals(new Prefix("P/"), ArgumentTokenizer.findUnknownPrefix("P/value", Set.of(pSlash)).orElseThrow());
        assertEquals(pSlash, ArgumentTokenizer.findUnknownPrefix("p/value", Set.of()).orElseThrow());
    }

    @Test
    public void findUnknownPrefix_supportedAndLiteralSlashText_returnsEmpty() {
        // EP: supported tokens, joined tokens, numeric slash text, URLs, punctuation, and empty input.
        // BVA: zero input tokens; unknown prefix at index zero is covered in the preceding test.
        for (String input : List.of("", " \t ", "p/value", "joined1x/value", "8/10", "https://example.com/a/b",
                "example.com/x/value", "-t value")) {
            assertTrue(ArgumentTokenizer.findUnknownPrefix(input, Set.of(pSlash)).isEmpty(), input);
        }
    }

    @Test
    public void findUnknownPrefix_nullArguments_rejected() {
        // EP: null violates the non-null input contract.
        assertThrows(NullPointerException.class, () -> ArgumentTokenizer.findUnknownPrefix(null, Set.of(pSlash)));
        assertThrows(NullPointerException.class, () -> ArgumentTokenizer.findUnknownPrefix("p/value", null));
    }

    @Test
    public void multimap_returnedValues_areDefensiveCopies() {
        // EP: mutating returned values cannot mutate stored arguments, whether present or absent.
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize("p/first p/second", pSlash);
        arguments.getAllValues(pSlash).clear();
        arguments.getAllValues(dashT).add("injected");
        assertValues(arguments, pSlash, "first", "second");
        assertTrue(arguments.getValue(dashT).isEmpty());
    }

    @Test
    public void multimap_duplicateValidation_checksOnlyRequestedPrefixes() throws ParseException {
        // EP: absent/single requested prefixes pass; repeated unrequested prefixes do not fail validation.
        // BVA: zero, one, and two occurrences of a checked prefix.
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize("p/one p/two -t value", pSlash, dashT);
        arguments.verifyNoDuplicatePrefixesFor(dashT, new Prefix("x/"));
        ParseException exception = assertThrows(ParseException.class, () ->
                arguments.verifyNoDuplicatePrefixesFor(pSlash, pSlash));
        assertEquals(Messages.MESSAGE_DUPLICATE_FIELDS + "p/", exception.getMessage());
        arguments.put(dashT, "second");
        ParseException multiple = assertThrows(ParseException.class, () ->
                arguments.verifyNoDuplicatePrefixesFor(dashT, pSlash));
        assertTrue(multiple.getMessage().startsWith(Messages.MESSAGE_DUPLICATE_FIELDS));
        assertEquals(Set.of("p/", "-t"), Set.of(multiple.getMessage()
                .substring(Messages.MESSAGE_DUPLICATE_FIELDS.length()).split(" ")));
        assertValues(arguments, pSlash, "one", "two");
        assertValues(arguments, dashT, "value", "second");
    }

    @Test
    public void prefixEquality_comparesExactText() {
        // EP: identical reference/value, different value/type, and null.
        assertEquals(pSlash, pSlash);
        assertEquals(pSlash, new Prefix("p/"));
        assertEquals(pSlash.hashCode(), new Prefix("p/").hashCode());
        assertNotEquals(pSlash, new Prefix("P/"));
        assertNotEquals(pSlash, "p/");
        assertFalse(pSlash.equals(null));
    }

    private void assertValues(ArgumentMultimap arguments, Prefix prefix, String... expected) {
        assertEquals(List.of(expected), arguments.getAllValues(prefix));
        assertEquals(expected[expected.length - 1], arguments.getValue(prefix).orElseThrow());
    }
}
