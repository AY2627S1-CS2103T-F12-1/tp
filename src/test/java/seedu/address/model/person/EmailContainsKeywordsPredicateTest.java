package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class EmailContainsKeywordsPredicateTest {

    @Test
    public void constructor_mutableKeywords_takesDefensiveCopy() {
        // EP: Mutating the caller's list after construction must not change the predicate's search criteria.
        List<String> keywords = new ArrayList<>(List.of("alice"));
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(keywords);
        keywords.clear();
        keywords.add("bob");

        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));
        assertFalse(predicate.test(new PersonBuilder().withEmail("bob@example.com").build()));
    }

    @Test
    public void constructor_nullKeywordsOrElement_throwsNullPointerException() {
        // EP: A null list or a list containing a null keyword violates the constructor's input contract.
        assertThrows(NullPointerException.class, () -> new EmailContainsKeywordsPredicate(null));
        List<String> keywords = new ArrayList<>();
        keywords.add(null);
        assertThrows(NullPointerException.class, () -> new EmailContainsKeywordsPredicate(keywords));
    }

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        EmailContainsKeywordsPredicate firstPredicate = new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        EmailContainsKeywordsPredicate secondPredicate = new EmailContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        EmailContainsKeywordsPredicate firstPredicateCopy =
                new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different email keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_emailContainsKeywords_returnsTrue() {
        // EP: partial-email matches, one or several keywords, OR matching, and case variation.
        // One keyword
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(List.of("alice"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Multiple keywords
        predicate = new EmailContainsKeywordsPredicate(List.of("alice", "example"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Only one matching keyword
        predicate = new EmailContainsKeywordsPredicate(List.of("bob", "example"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Mixed-case keywords
        predicate = new EmailContainsKeywordsPredicate(List.of("aLIce", "eXAMPLE"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));
    }

    @Test
    public void test_emailDoesNotContainKeywords_returnsFalse() {
        // EP: no keywords, no selected-field match, and keywords matching other fields only.
        // Zero keywords
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Non-matching keyword
        predicate = new EmailContainsKeywordsPredicate(List.of("carol"));
        assertFalse(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Keywords match name and student ID, but do not match email
        predicate = new EmailContainsKeywordsPredicate(List.of("Alice", "A1234567B"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withStudentId("A1234567B").withEmail("bob@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(keywords);

        String expected = EmailContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
