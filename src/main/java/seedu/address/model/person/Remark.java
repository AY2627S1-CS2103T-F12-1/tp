package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a person's optional remark of at most 4000 Unicode code points, including empty text.
 */
public class Remark {
    public static final int MAX_LENGTH = 4000;
    public static final String MESSAGE_CONSTRAINTS = "Remark must not exceed 4000 characters.";

    public final String value;

    /**
     * Constructs a {@code Remark} from non-null text of at most {@link #MAX_LENGTH} Unicode code points.
     *
     * @param remark The remark text.
     * @throws IllegalArgumentException if the text exceeds the length limit.
     * @throws NullPointerException if {@code remark} is null.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        checkArgument(isValidRemark(remark), MESSAGE_CONSTRAINTS);
        value = remark;
    }

    /**
     * Returns whether the non-null text contains at most {@link #MAX_LENGTH} Unicode code points.
     *
     * @param test The string to check.
     * @return True if the text does not exceed the maximum code point limit.
     * @throws NullPointerException if {@code test} is null.
     */
    public static boolean isValidRemark(String test) {
        requireNonNull(test);
        return test.codePointCount(0, test.length()) <= MAX_LENGTH;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
