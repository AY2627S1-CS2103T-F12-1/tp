package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Email} matches any of the keywords given.
 */
public class EmailContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Constructs an {@code EmailContainsKeywordsPredicate} with an immutable snapshot of the given keywords.
     *
     * @param keywords Keywords to match against a person's email.
     * @throws NullPointerException if the list or any keyword is null.
     */
    public EmailContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsIgnoreCase(person.getEmail().value, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EmailContainsKeywordsPredicate otherEmailContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherEmailContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
