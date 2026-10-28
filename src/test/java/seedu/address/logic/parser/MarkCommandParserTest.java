package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

public class MarkCommandParserTest {

    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_validValuesAndOrder_returnsMarkCommand() {
        // EP: one valid value for each required parameter, in canonical and reordered forms
        MarkCommand expected = new MarkCommand(new StudentId("A0123456B"),
                LocalDate.parse("2026-10-20"), new Label("Tutorial 1"));
        assertParseSuccess(parser, "i/a0123456b d/2026-10-20 l/Tutorial 1", expected);
        assertParseSuccess(parser, " l/ Tutorial 1  d/ 2026-10-20  i/ A0123456B ", expected);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingOrEmptyParameter_reportsParameter() {
        // EP: each required parameter is missing or has an empty value
        assertParseFailure(parser, "", "Missing required parameter: i/.");
        assertParseFailure(parser, "i/A0123456B l/Tutorial 1", "Missing required parameter: d/.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20", "Missing required parameter: l/.");
        assertParseFailure(parser, "i/ d/2026-10-20 l/Tutorial 1", "Parameter i/ cannot be empty.");
        assertParseFailure(parser, "i/A0123456B d/ l/Tutorial 1", "Parameter d/ cannot be empty.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/", "Parameter l/ cannot be empty.");
    }

    @Test
    public void parse_repeatedOrUnknownParameter_reportsStructureError() {
        // EP: repeated supported parameter and unsupported prefix
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 d/2026-10-21 l/Tutorial 1",
                "Parameter d/ must be specified only once.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1 x/value",
                "Unknown parameter: x/.");
        assertParseFailure(parser, "I/A0123456B d/2026-10-20 l/Tutorial 1", "Unknown parameter: I/.");
    }

    @Test
    public void parse_invalidStructure_reportsUsage() {
        // EP: non-whitespace preamble or internal line break violates command structure
        assertParseFailure(parser, "unexpected i/A0123456B d/2026-10-20 l/Tutorial 1",
                MarkCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1\nextra",
                MarkCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1\rextra",
                MarkCommand.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_reportsDateError() {
        // BVA: invalid calendar day, missing zero padding, wrong separator, and month above 12
        for (String date : new String[] {"2026-02-30", "2026-2-03", "20/10/2026", "2026-13-01"}) {
            assertParseFailure(parser, "i/A0123456B d/" + date + " l/Tutorial 1", ParserUtil.MESSAGE_INVALID_DATE);
        }
    }

    @Test
    public void parse_invalidStudentOrGroup_reportsFieldError() {
        // EP: invalid student ID or group label value
        assertParseFailure(parser, "i/123 d/2026-10-20 l/Tutorial 1", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Group/1", Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_groupAtLengthBoundary_acceptsMaximumAndRejectsOneAbove() {
        String maximumLengthGroup = "a".repeat(Label.MAX_NAME_LENGTH);
        String tooLongGroup = "a".repeat(Label.MAX_NAME_LENGTH + 1);
        MarkCommand expected = new MarkCommand(new StudentId("A0123456B"),
                LocalDate.parse("2026-10-20"), new Label(maximumLengthGroup));

        // BVA: group labels of 60 code points are valid; 61 code points are rejected
        assertParseSuccess(parser, "i/A0123456B d/2026-10-20 l/" + maximumLengthGroup, expected);
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/" + tooLongGroup, Label.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parseDate_validLeapDayAndWhitespace_returnsDate() throws Exception {
        // BVA: February 29 on a leap year is accepted; surrounding whitespace is ignored
        assertEquals(LocalDate.parse("2028-02-29"), ParserUtil.parseDate(" 2028-02-29 "));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDate(null));
    }
}
