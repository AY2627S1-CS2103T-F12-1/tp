package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code StudentId} matches any of the keywords given.
 */
public class StudentIdContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Constructs a {@code StudentIdContainsKeywordsPredicate} with an immutable snapshot of the given keywords.
     *
     * @param keywords Keywords to match against a person's student ID.
     * @throws NullPointerException if the list or any keyword is null.
     */
    public StudentIdContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsIgnoreCase(person.getStudentId().value, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentIdContainsKeywordsPredicate otherStudentIdContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherStudentIdContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
