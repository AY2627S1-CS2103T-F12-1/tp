package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;

/**
 * Contains utility methods for converting command parameter values into model objects.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_DATE = "Date must use a valid YYYY-MM-DD date.";
    public static final String DATE_FORMAT_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @param oneBasedIndex The non-null text representing a one-based index.
     * @return The parsed index.
     * @throws NullPointerException if {@code oneBasedIndex} is null.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespace is trimmed; repeated internal spaces are collapsed by {@code Name}.
     *
     * @param name The non-null name text.
     * @return The validated, normalised name with capitalisation preserved.
     * @throws NullPointerException if {@code name} is null.
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String studentId} into a {@code StudentId}.
     * Leading and trailing whitespaces will be trimmed.
     * Student ID letters are converted to uppercase by {@code StudentId}.
     *
     * @param studentId The non-null student ID text.
     * @return The validated, uppercase student ID with surrounding whitespace removed.
     * @throws NullPointerException if {@code studentId} is null.
     * @throws ParseException if the given {@code studentId} is invalid.
     */
    public static StudentId parseStudentId(String studentId) throws ParseException {
        requireNonNull(studentId);
        String trimmedStudentId = studentId.trim();
        if (!StudentId.isValidStudentId(trimmedStudentId)) {
            throw new ParseException(StudentId.MESSAGE_CONSTRAINTS);
        }
        return new StudentId(trimmedStudentId);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     * The domain is converted to lowercase by {@code Email}; local-part capitalisation is preserved.
     *
     * @param email The non-null email text.
     * @return The validated email with surrounding whitespace removed and its domain in lowercase.
     * @throws NullPointerException if {@code email} is null.
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String label} into a {@code Label}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @param label The non-null label text.
     * @return The parsed label with surrounding whitespace removed.
     * @throws NullPointerException if {@code label} is null.
     * @throws ParseException if the given {@code label} is invalid.
     */
    public static Label parseLabel(String label) throws ParseException {
        requireNonNull(label);
        String trimmedLabel = label.trim();
        if (!Label.isValidLabelName(trimmedLabel)) {
            throw new ParseException(Label.MESSAGE_CONSTRAINTS);
        }
        return new Label(trimmedLabel);
    }

    /**
     * Parses a {@code String remark} into a {@code Remark}.
     * Removes leading and trailing whitespace using {@link String#trim()}, preserving internal spacing and case.
     * Empty and whitespace-only input produces an empty remark. The length limit applies after trimming.
     *
     * @param remark The non-null remark text.
     * @return The parsed remark with surrounding whitespace removed.
     * @throws NullPointerException if {@code remark} is null.
     * @throws ParseException if the trimmed remark exceeds {@link Remark#MAX_LENGTH} Unicode code points.
     */
    public static Remark parseRemark(String remark) throws ParseException {
        requireNonNull(remark);
        String trimmedRemark = remark.trim();
        if (!Remark.isValidRemark(trimmedRemark)) {
            throw new ParseException(Remark.MESSAGE_CONSTRAINTS);
        }
        return new Remark(trimmedRemark);
    }

    /**
     * Parses a calendar date in strict {@code YYYY-MM-DD} format.
     *
     * @param date The non-null date text.
     * @return The parsed date.
     * @throws NullPointerException if {@code date} is null.
     * @throws ParseException if the date is malformed or does not exist.
     */
    public static LocalDate parseDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        if (!trimmedDate.matches(DATE_FORMAT_REGEX)) {
            throw new ParseException(MESSAGE_INVALID_DATE);
        }
        try {
            return LocalDate.parse(trimmedDate);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE);
        }
    }
}
