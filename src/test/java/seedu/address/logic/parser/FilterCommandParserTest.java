package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.label.Label;

public class FilterCommandParserTest {

    private static final String VALID_LABEL = "Discrete Math Tutorial";

    private FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        FilterCommand expectedCommand = new FilterCommand(new Label(VALID_LABEL));

        assertParseSuccess(parser, " l/" + VALID_LABEL, expectedCommand);
    }

    @Test
    public void parse_labelMissing_failure() {
        assertParseFailure(parser, "", FilterCommandParser.MESSAGE_MISSING_LABEL);
    }

    @Test
    public void parse_labelEmpty_failure() {
        assertParseFailure(parser, " l/", FilterCommandParser.MESSAGE_EMPTY_LABEL);
    }

    @Test
    public void parse_labelRepeated_failure() {
        assertParseFailure(parser, " l/Tutorial 1 l/Tutorial 2", FilterCommandParser.MESSAGE_DUPLICATE_LABEL);
    }

    @Test
    public void parse_invalidLabel_failure() {
        assertParseFailure(parser, " l/Tutorial/1", FilterCommandParser.MESSAGE_LABEL_CONTAINS_SLASH);
    }

    @Test
    public void parse_labelWithAsciiControlCharacter_failure() {
        assertParseFailure(parser, " l/Tutorial\n1",
                FilterCommandParser.MESSAGE_LABEL_CONTAINS_ASCII_CONTROL);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, " extra l/" + VALID_LABEL, FilterCommandParser.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
