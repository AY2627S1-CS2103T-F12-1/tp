package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDENT_ID_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
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
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String REQUIRED_FIELDS_BOB = NAME_DESC_BOB + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB;
    private static final String VALID_LABEL = "Discrete Math Tutorial";
    private static final String LABEL_DESC_TUTORIAL = " l/" + VALID_LABEL;
    private static final String INVALID_LABEL_DESC = " l/Tutorial/1";

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_missingRequiredParameters_reportsMissingPrefix() {
        assertParseFailure(parser, STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, "Missing required parameter: n/.");
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB, "Missing required parameter: i/.");
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB, "Missing required parameter: e/.");
        assertParseFailure(parser, "", "Missing required parameter: n/.");
        assertParseFailure(parser, " r/Optional", "Missing required parameter: n/.");
    }

    @Test
    public void parse_emptyRequiredParameters_reportsEmptyPrefix() {
        assertParseFailure(parser, " n/ \t" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                "Parameter n/ cannot be empty.");
        assertParseFailure(parser, NAME_DESC_BOB + " i/ \t" + EMAIL_DESC_BOB,
                "Parameter i/ cannot be empty.");
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + " e/ \t",
                "Parameter e/ cannot be empty.");
    }

    @Test
    public void parse_unknownPrefixes_reportsUnknownPrefix() {
        assertParseFailure(parser, "p/123" + REQUIRED_FIELDS_BOB, "Unknown parameter: p/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " x/hello", "Unknown parameter: x/.");
        assertParseFailure(parser, " n/Bob x/hello" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB,
                "Unknown parameter: x/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/Quiz x/hello", "Unknown parameter: x/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + "\tunknown/value", "Unknown parameter: unknown/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " N/John", "Unknown parameter: N/.");
    }

    @Test
    public void parse_tabsAndReorderedParameters_success() {
        Person expected = new PersonBuilder(BOB).withTags().withRemark("Quiz 1: 8/10.").build();
        String input = "r/Quiz 1: 8/10.\te/" + VALID_EMAIL_BOB + "\ti/" + VALID_STUDENT_ID_BOB
                + "\tn/" + VALID_NAME_BOB;
        assertParseSuccess(parser, input, new AddCommand(expected));
    }

    @Test
    public void parse_remarkLiteralSlashesAndSpacing_success() {
        String remark = "Quiz: 8/10.  See https://example.com/a/b; use /r literally.";
        Person expected = new PersonBuilder(BOB).withTags().withRemark(remark).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + remark, new AddCommand(expected));
    }

    @Test
    public void parse_embeddedLineBreaks_failure() {
        String expected = AddCommand.MESSAGE_INVALID_FORMAT;
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First\nSecond", expected);
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First\rSecond", expected);
    }

    @Test
    public void parse_multipleErrors_reportsStructureBeforeValues() {
        assertParseFailure(parser, " n/ n/John", "Parameter n/ must be specified only once.");
        assertParseFailure(parser, " n/ e/", "Missing required parameter: i/.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " n/Again x/value", "Unknown parameter: x/.");
    }

    @Test
    public void parse_normalisedNameAndEmail_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Anne-Marie O’Neill")
                .withEmail("Quiz+Sam@example.com").withTags().build();
        assertParseSuccess(parser, " n/  Anne-Marie   O’Neill  " + STUDENT_ID_DESC_BOB
                + " e/  Quiz+Sam@EXAMPLE.COM  ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_invalidNameAndEmailUnderNewRules_failure() {
        assertParseFailure(parser, " n/John 2" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + " e/bob@localhost",
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkLengthBoundary_validatesLimit() {
        Person expectedPerson = new PersonBuilder(BOB).withTags()
                .withRemark("x".repeat(Remark.MAX_LENGTH)).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH),
                new AddCommand(expectedPerson));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH + 1),
                Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkPresent_success() {
        String remark = "Needs help with recursion: follow up next week!";
        Person expectedPerson = new PersonBuilder(BOB).withRemark(remark).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + remark + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, " r/" + remark + REQUIRED_FIELDS_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_labelPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().withLabels(VALID_LABEL).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + LABEL_DESC_TUTORIAL, new AddCommand(expectedPerson));
        assertParseSuccess(parser, LABEL_DESC_TUTORIAL + REQUIRED_FIELDS_BOB, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_emptyLabel_failure() {
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " l/", "Parameter l/ cannot be empty.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " l/   ", "Parameter l/ cannot be empty.");
    }

    @Test
    public void parse_repeatedLabel_failure() {
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + LABEL_DESC_TUTORIAL + " l/CS2103T Tutorial",
                "Parameter l/ must be specified only once.");
    }

    @Test
    public void parse_invalidLabel_failure() {
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_LABEL_DESC, Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkEmptyOrMissing_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().withRemark("").build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB, new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/", new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/   ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedRemark_failure() {
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First r/Second",
                "Parameter r/ must be specified only once.");
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/ r/Second",
                "Parameter r/ must be specified only once.");
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + REQUIRED_FIELDS_BOB + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, NAME_DESC_BOB + " " + PREFIX_STUDENT_ID
                + VALID_STUDENT_ID_BOB.toLowerCase() + EMAIL_DESC_BOB + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
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
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + STUDENT_ID_DESC_AMY + EMAIL_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
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
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_STUDENT_ID_DESC + EMAIL_DESC_BOB,
                StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + REQUIRED_FIELDS_BOB,
                AddCommand.MESSAGE_INVALID_FORMAT);
    }
}
