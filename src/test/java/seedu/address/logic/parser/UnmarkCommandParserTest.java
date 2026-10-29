package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

public class UnmarkCommandParserTest {

    private final UnmarkCommandParser parser = new UnmarkCommandParser();

    @Test
    public void parse_validValuesAndOrder_returnsUnmarkCommand() {
        // EP: one valid value for each required parameter, in canonical and reordered forms
        UnmarkCommand expected = new UnmarkCommand(new StudentId("A0123456B"),
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
        // EP: each supported parameter repeated once, and an unsupported prefix
        assertParseFailure(parser, "i/A0123456B i/A0123456B d/2026-10-20 l/Tutorial 1",
                "Parameter i/ must be specified only once.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 d/2026-10-21 l/Tutorial 1",
                "Parameter d/ must be specified only once.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1 l/Tutorial 2",
                "Parameter l/ must be specified only once.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1 x/value",
                "Unknown parameter: x/.");
        assertParseFailure(parser, "I/A0123456B d/2026-10-20 l/Tutorial 1", "Unknown parameter: I/.");
    }

    @Test
    public void parse_invalidStructure_reportsUsage() {
        // EP: non-whitespace preamble or internal line break violates command structure
        assertParseFailure(parser, "unexpected i/A0123456B d/2026-10-20 l/Tutorial 1",
                UnmarkCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1\nextra",
                UnmarkCommand.MESSAGE_INVALID_FORMAT);
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
    public void parse_validLeapDay_returnsUnmarkCommand() {
        // BVA: February 29 on a leap year is valid
        UnmarkCommand expected = new UnmarkCommand(new StudentId("A0123456B"),
                LocalDate.parse("2028-02-29"), new Label("Tutorial 1"));
        assertParseSuccess(parser, "i/A0123456B d/2028-02-29 l/Tutorial 1", expected);
    }

    @Test
    public void parse_multipleErrors_followsMarkValidationOrder() {
        // EP: multiple invalid inputs report the first error in the documented validation order
        assertParseFailure(parser, "unexpected x/value i/A0123456B i/A0123456B",
                "Unknown parameter: x/.");
        assertParseFailure(parser, "unexpected i/A0123456B i/A0123456B",
                UnmarkCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B i/A0123456B", "Parameter i/ must be specified only once.");
        assertParseFailure(parser, "i/", "Parameter i/ cannot be empty.");
        assertParseFailure(parser, "i/123 d/2026-02-30 l/Group/1", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "i/A0123456B d/2026-02-30 l/Group/1", ParserUtil.MESSAGE_INVALID_DATE);
    }
}
