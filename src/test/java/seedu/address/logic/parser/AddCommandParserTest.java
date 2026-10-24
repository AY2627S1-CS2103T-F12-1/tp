package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDENT_ID_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String REQUIRED_FIELDS_BOB = NAME_DESC_BOB + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB;
    private static final String VALID_LABEL = "Discrete Math Tutorial";
    private static final String LABEL_DESC_TUTORIAL = " l/" + VALID_LABEL;
    private static final String INVALID_LABEL_DESC = " l/Tutorial/1";

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        // EP: null parser input
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingRequiredParameters_reportsMissingPrefix() {
        // EP: missing name, Student ID, or email
        assertParseFailure(parser, STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, "Missing required parameter: n/.");
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB, "Missing required parameter: i/.");
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB, "Missing required parameter: e/.");
        // BVA: zero parameters
        assertParseFailure(parser, "", "Missing required parameter: n/.");
        assertParseFailure(parser, " r/Optional", "Missing required parameter: n/.");
    }

    @Test
    public void parse_emptyRequiredParameters_reportsEmptyPrefix() {
        // EP: required prefix present with an empty value
        assertParseFailure(parser, " n/ \t" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                "Parameter n/ cannot be empty.");
        assertParseFailure(parser, NAME_DESC_BOB + " i/ \t" + EMAIL_DESC_BOB,
                "Parameter i/ cannot be empty.");
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + " e/ \t",
                "Parameter e/ cannot be empty.");
    }

    @Test
    public void parse_unknownPrefixes_reportsUnknownPrefix() {
        // EP: unknown prefixes
        assertParseFailure(parser, "p/123" + REQUIRED_FIELDS_BOB, "Unknown parameter: p/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " x/hello", "Unknown parameter: x/.");
        assertParseFailure(parser, " n/Bob x/hello" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                "Unknown parameter: x/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/Quiz x/hello", "Unknown parameter: x/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + "\tunknown/value", "Unknown parameter: unknown/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " N/John", "Unknown parameter: N/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " t/friends", "Unknown parameter: t/.");
    }

    @Test
    public void parse_tabsAndReorderedParameters_success() {
        // EP: tabs and reordered parameters
        Person expected = new PersonBuilder(BOB).withRemark("Quiz 1: 8/10.").build();
        String input = "r/Quiz 1: 8/10.\te/" + VALID_EMAIL_BOB + "\ti/" + VALID_STUDENT_ID_BOB
                + "\tn/" + VALID_NAME_BOB;
        assertParseSuccess(parser, input, new AddCommand(expected));
    }

    @Test
    public void parse_remarkLiteralSlashesAndSpacing_success() {
        // EP: remark literal slashes and spacing
        String remark = "Quiz: 8/10.  See https://example.com/a/b; use /r literally.";
        Person expected = new PersonBuilder(BOB).withRemark(remark).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + remark, new AddCommand(expected));
    }

    @Test
    public void parse_embeddedLineBreaks_failure() {
        // EP: embedded line breaks
        String expected = AddCommand.MESSAGE_INVALID_FORMAT;
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First\nSecond", expected);
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First\rSecond", expected);
    }

    @Test
    public void parse_multipleErrors_reportsStructureBeforeValues() {
        // EP: multiple errors with structural error precedence
        assertParseFailure(parser, " n/ n/John", "Parameter n/ must be specified only once.");
        assertParseFailure(parser, " n/ e/", "Missing required parameter: i/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " n/Again x/value", "Unknown parameter: x/.");
    }

    @Test
    public void parse_normalisedNameAndEmail_success() {
        // EP: normalised name and email
        Person expectedPerson = new PersonBuilder(BOB).withName("Anne-Marie O’Neill")
                .withEmail("Quiz+Sam@example.com").build();
        assertParseSuccess(parser, " n/  Anne-Marie   O’Neill  " + STUDENT_ID_DESC_BOB
                + " e/  Quiz+Sam@EXAMPLE.COM  ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_invalidNameAndEmailUnderNewRules_failure() {
        // EP: name contains a digit
        assertParseFailure(parser, " n/John 2" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        // EP: email lacks a dotted domain
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + " e/bob@localhost",
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkLengthBoundary_validatesLimit() {
        // EP: valid non-empty remark
        // BVA: one character below the maximum length
        Person shorterRemark = new PersonBuilder(BOB)
                .withRemark("x".repeat(Remark.MAX_LENGTH - 1)).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH - 1),
                new AddCommand(shorterRemark));

        // BVA: maximum allowed length
        Person expectedPerson = new PersonBuilder(BOB)
                .withRemark("x".repeat(Remark.MAX_LENGTH)).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH),
                new AddCommand(expectedPerson));

        // EP: remark exceeds the maximum length
        // BVA: one character above the maximum length
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH + 1),
                Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nameLengthBoundary_validatesLimit() {
        // EP: valid name at the maximum length
        // BVA: 100 characters
        String validName = "A".repeat(Name.MAX_LENGTH);
        Person expected = new PersonBuilder(BOB).withName(validName).build();
        assertParseSuccess(parser, " n/" + validName + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                new AddCommand(expected));

        // EP: name exceeds the maximum length
        // BVA: 101 characters
        assertParseFailure(parser, " n/" + validName + "A" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_studentIdLengthBoundary_validatesLimit() {
        // EP: invalid Student ID length
        // BVA: one character below and above the required length of nine
        assertParseFailure(parser, NAME_DESC_BOB + " i/A0123456" + EMAIL_DESC_BOB,
                StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + " i/A0123456BC" + EMAIL_DESC_BOB,
                StudentId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkPresent_success() {
        // EP: remark present
        String remark = "Needs help with recursion: follow up next week!";
        Person expectedPerson = new PersonBuilder(BOB).withRemark(remark).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + remark,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, " r/" + remark + REQUIRED_FIELDS_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_labelPresent_success() {
        // EP: label present
        Person expectedPerson = new PersonBuilder(BOB).withLabels(VALID_LABEL).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + LABEL_DESC_TUTORIAL, new AddCommand(expectedPerson));
        assertParseSuccess(parser, LABEL_DESC_TUTORIAL + REQUIRED_FIELDS_BOB, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_emptyLabel_failure() {
        // EP: empty label
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " l/", "Parameter l/ cannot be empty.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " l/   ", "Parameter l/ cannot be empty.");
    }

    @Test
    public void parse_repeatedLabel_failure() {
        // EP: repeated label
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + LABEL_DESC_TUTORIAL + " l/CS2103T Tutorial",
                "Parameter l/ must be specified only once.");
    }

    @Test
    public void parse_invalidLabel_failure() {
        // EP: invalid label
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_LABEL_DESC, Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_labelLengthBoundary_validatesLimit() {
        // EP: valid label at the maximum length
        // BVA: 60 Unicode code points
        String validLabel = "A".repeat(Label.MAX_NAME_LENGTH);
        Person expected = new PersonBuilder(BOB).withLabels(validLabel).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " l/" + validLabel, new AddCommand(expected));

        // EP: label exceeds the maximum length
        // BVA: 61 Unicode code points
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " l/" + validLabel + "A", Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkEmptyOrMissing_success() {
        // EP: optional remark missing or present with an empty value
        // BVA: zero characters after trimming
        Person expectedPerson = new PersonBuilder(BOB).withRemark("").build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB, new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/", new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/   ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedRemark_failure() {
        // EP: repeated remark
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First r/Second",
                "Parameter r/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/ r/Second",
                "Parameter r/ must be specified only once.");
    }

    @Test
    public void parse_requiredFieldsPresent_success() {
        // EP: required fields present
        Person expectedPerson = new PersonBuilder(BOB).build();
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + REQUIRED_FIELDS_BOB,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, NAME_DESC_BOB + " " + PREFIX_STUDENT_ID
                + VALID_STUDENT_ID_BOB.toLowerCase() + EMAIL_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedRequiredValue_failure() {
        // EP: repeated required value
        assertParseFailure(parser, NAME_DESC_AMY + REQUIRED_FIELDS_BOB,
                "Parameter n/ must be specified only once.");
        assertParseFailure(parser, STUDENT_ID_DESC_AMY + REQUIRED_FIELDS_BOB,
                "Parameter i/ must be specified only once.");
        assertParseFailure(parser, EMAIL_DESC_AMY + REQUIRED_FIELDS_BOB,
                "Parameter e/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + REQUIRED_FIELDS_BOB,
                "Parameter n/ must be specified only once.");
    }

    @Test
    public void parse_repeatedInvalidValue_failure() {
        // EP: repeated invalid value
        assertParseFailure(parser, INVALID_NAME_DESC + REQUIRED_FIELDS_BOB,
                "Parameter n/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_NAME_DESC,
                "Parameter n/ must be specified only once.");
        assertParseFailure(parser, INVALID_STUDENT_ID_DESC + REQUIRED_FIELDS_BOB,
                "Parameter i/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_STUDENT_ID_DESC,
                "Parameter i/ must be specified only once.");
        assertParseFailure(parser, INVALID_EMAIL_DESC + REQUIRED_FIELDS_BOB,
                "Parameter e/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_EMAIL_DESC,
                "Parameter e/ must be specified only once.");
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // EP: optional fields missing
        Person expectedPerson = new PersonBuilder(AMY).build();
        assertParseSuccess(parser, NAME_DESC_AMY + STUDENT_ID_DESC_AMY + EMAIL_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // EP: required field without its prefix
        String expectedMessage = AddCommand.MESSAGE_INVALID_FORMAT;
        assertParseFailure(parser, VALID_NAME_BOB + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, expectedMessage);
        assertParseFailure(parser, NAME_DESC_BOB + VALID_STUDENT_ID_BOB + EMAIL_DESC_BOB,
                "Missing required parameter: i/.");
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + VALID_EMAIL_BOB,
                "Missing required parameter: e/.");
        assertParseFailure(parser, VALID_NAME_BOB + VALID_STUDENT_ID_BOB + VALID_EMAIL_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // EP: invalid name, ID, email, or preamble
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_STUDENT_ID_DESC + EMAIL_DESC_BOB,
                StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + REQUIRED_FIELDS_BOB,
                AddCommand.MESSAGE_INVALID_FORMAT);
    }
}
