package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {
    private static final StudentId STUDENT_ID = new StudentId("A0123456B");
    private static final String TARGET = "i/" + STUDENT_ID;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        // EP: null parser input
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_tabSeparatedFields_success() {
        // EP: tab separated fields
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Amy Bee").withEmail("amy@example.com").build();
        assertParseSuccess(parser, "i/a0123456b\tn/Amy Bee\te/amy@example.com",
                new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_missingParts_failure() {
        // EP: missing ID, no edit fields, or empty ID
        // BVA: zero input characters
        assertParseFailure(parser, "", "Missing required parameter: i/.");
        assertParseFailure(parser, "n/Amy Bee", "Missing required parameter: i/.");
        assertParseFailure(parser, TARGET, EditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "i/ n/Amy Bee", "Parameter i/ cannot be empty.");
    }

    @Test
    public void parse_invalidStructure_failure() {
        // EP: preamble, repeated ID, invalid ID, or line break
        assertParseFailure(parser, "1 n/Amy Bee", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, TARGET + " n/Amy Bee i/B0123456C",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_STUDENT_ID));
        assertParseFailure(parser, "i/123 n/Amy Bee", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "i/A0123456B n/Amy Bee\ne/a@example.com", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "edit " + TARGET + " n/Amy Bee", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidField_failure() {
        // EP: invalid name or email value
        assertParseFailure(parser, TARGET + " n/Amy2", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " e/bad", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " n/Amy2 e/bad", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " e/", Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleFields_success() {
        // EP: multiple fields
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Amy Bee").withEmail("amy@example.com").withRemark("Needs follow-up").build();
        assertParseSuccess(parser, TARGET + " e/amy@example.com r/Needs follow-up n/Amy Bee",
                new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_emptyRemark_success() {
        // EP: empty remark
        // BVA: zero characters after trimming
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        assertParseSuccess(parser, TARGET + " r/   ", new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_addAndRemoveLabels_success() {
        // EP: add and remove labels
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withLabelsToAdd("Tutorial B", "Needs Follow-up")
                .withLabelsToRemove("Tutorial A").build();
        assertParseSuccess(parser, TARGET + " dl/Tutorial A l/Tutorial B l/Needs Follow-up",
                new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_onlyAddOrRemoveLabel_success() {
        // EP: label addition without a removal
        EditPersonDescriptor addDescriptor = new EditPersonDescriptorBuilder()
                .withLabelsToAdd("Tutorial A").build();
        assertParseSuccess(parser, TARGET + " l/Tutorial A", new EditCommand(STUDENT_ID, addDescriptor));

        // EP: label removal without an addition
        EditPersonDescriptor removeDescriptor = new EditPersonDescriptorBuilder()
                .withLabelsToRemove("Tutorial A").build();
        assertParseSuccess(parser, TARGET + " dl/Tutorial A", new EditCommand(STUDENT_ID, removeDescriptor));
    }

    @Test
    public void parse_clearLabelsAndRemark_success() {
        // EP: clear labels and remark
        // BVA: zero label and remark value characters
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withClearedLabels().withRemark("").build();
        assertParseSuccess(parser, TARGET + " l/ r/", new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_invalidLabelChanges_failure() {
        // EP: empty, conflicting, repeated, or invalid label operation
        // BVA: zero characters for the label to remove
        assertParseFailure(parser, TARGET + " dl/", "Parameter dl/ cannot be empty.");
        assertParseFailure(parser, TARGET + " l/ dl/Tutorial A",
                "An empty l/ cannot be combined with other label changes.");
        assertParseFailure(parser, TARGET + " l/ l/Tutorial B",
                "An empty l/ cannot be combined with other label changes.");
        assertParseFailure(parser, TARGET + " l/Tutorial A l/tutorial a",
                "Parameter l/ repeats label \"tutorial a\".");
        assertParseFailure(parser, TARGET + " dl/Tutorial A dl/tutorial a",
                "Parameter dl/ repeats label \"tutorial a\".");
        assertParseFailure(parser, TARGET + " l/Bad!", Label.MESSAGE_CONSTRAINTS);

        // EP: invalid label supplied for removal
        assertParseFailure(parser, TARGET + " dl/Bad!", Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedField_failure() {
        // EP: repeated field
        assertParseFailure(parser, TARGET + " n/Amy Bee n/Bob Bee",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, TARGET + " r/first r/second",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_REMARK));
        assertParseFailure(parser, TARGET + " e/amy@example.com e/bob@example.com",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_EMAIL));
    }

    @Test
    public void parse_unknownPrefix_failure() {
        // EP: unknown prefix
        assertParseFailure(parser, TARGET + " r/Needs help x/value", "Unknown parameter: x/.");
        assertParseFailure(parser, TARGET + " t/friends", "Unknown parameter: t/.");
    }

    @Test
    public void parse_unknownPrefixAndInvalidId_reportsUnknownPrefixFirst() {
        // EP: structural and value errors occur together
        assertParseFailure(parser, "i/123 n/Amy2 x/value", "Unknown parameter: x/.");
    }

    @Test
    public void parse_remarkLengthBoundary_validatesLimit() {
        // EP: valid non-empty remark
        // BVA: maximum allowed length
        String validRemark = "x".repeat(Remark.MAX_LENGTH);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark(validRemark).build();
        assertParseSuccess(parser, TARGET + " r/" + validRemark, new EditCommand(STUDENT_ID, descriptor));

        // EP: remark exceeds the maximum length
        // BVA: one character above the maximum length
        assertParseFailure(parser, TARGET + " r/" + validRemark + "x", Remark.MESSAGE_CONSTRAINTS);
    }
}
