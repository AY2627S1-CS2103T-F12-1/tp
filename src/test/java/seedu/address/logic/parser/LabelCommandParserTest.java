package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LabelCommand;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

public class LabelCommandParserTest {

    private static final String VALID_LABEL = "Discrete Math Tutorial";
    private static final String VALID_STUDENT_ID = "A0101010A";

    private LabelCommandParser parser = new LabelCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        LabelCommand expectedCommand = new LabelCommand(new Label(VALID_LABEL), new StudentId(VALID_STUDENT_ID));

        assertParseSuccess(parser, " l/" + VALID_LABEL + " i/" + VALID_STUDENT_ID, expectedCommand);
    }

    @Test
    public void parse_labelMissing_failure() {
        assertParseFailure(parser, " i/" + VALID_STUDENT_ID, LabelCommandParser.MESSAGE_MISSING_LABEL);
    }

    @Test
    public void parse_labelEmpty_failure() {
        assertParseFailure(parser, " l/ i/" + VALID_STUDENT_ID, LabelCommandParser.MESSAGE_EMPTY_LABEL);
    }

    @Test
    public void parse_labelBlank_failure() {
        assertParseFailure(parser, " l/   i/" + VALID_STUDENT_ID, LabelCommandParser.MESSAGE_EMPTY_LABEL);
    }

    @Test
    public void parse_labelWithSlash_failure() {
        assertParseFailure(parser, " l/Tutorial/1 i/" + VALID_STUDENT_ID,
                LabelCommandParser.MESSAGE_LABEL_CONTAINS_SLASH);
    }

    @Test
    public void parse_labelWithAsciiControlCharacter_failure() {
        assertParseFailure(parser, " l/Tutorial\n1 i/" + VALID_STUDENT_ID,
                LabelCommandParser.MESSAGE_LABEL_CONTAINS_ASCII_CONTROL);
    }

    @Test
    public void parse_labelRepeated_failure() {
        assertParseFailure(parser, " l/Tutorial 1 l/Tutorial 2 i/" + VALID_STUDENT_ID,
                LabelCommandParser.MESSAGE_DUPLICATE_LABEL);
    }

    @Test
    public void parse_studentIdMissing_failure() {
        assertParseFailure(parser, " l/" + VALID_LABEL, LabelCommandParser.MESSAGE_MISSING_STUDENT_ID);
    }

    @Test
    public void parse_studentIdEmpty_failure() {
        assertParseFailure(parser, " l/" + VALID_LABEL + " i/", LabelCommandParser.MESSAGE_EMPTY_STUDENT_ID);
    }

    @Test
    public void parse_studentIdRepeated_failure() {
        assertParseFailure(parser, " l/" + VALID_LABEL + " i/A0101010A i/A0101011A",
                LabelCommandParser.MESSAGE_DUPLICATE_STUDENT_ID);
    }

    @Test
    public void parse_invalidStudentId_failure() {
        assertParseFailure(parser, " l/" + VALID_LABEL + " i/A010101-A", StudentId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, " extra l/" + VALID_LABEL + " i/" + VALID_STUDENT_ID,
                LabelCommandParser.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
