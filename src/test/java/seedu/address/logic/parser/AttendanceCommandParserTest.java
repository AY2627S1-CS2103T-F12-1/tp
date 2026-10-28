package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.model.label.Label;

public class AttendanceCommandParserTest {

    private final AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_validValuesAndOrder_returnsAttendanceCommand() {
        // EP: one valid value for each required parameter, in canonical and reordered forms
        AttendanceCommand expected = new AttendanceCommand(LocalDate.parse("2026-10-20"), new Label("Tutorial 1"));
        assertParseSuccess(parser, "d/2026-10-20 l/Tutorial 1", expected);
        assertParseSuccess(parser, " l/ Tutorial 1  d/ 2026-10-20 ", expected);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingOrEmptyParameter_reportsParameter() {
        // EP: each required parameter is missing or has an empty value
        assertParseFailure(parser, "", "Missing required parameter: d/.");
        assertParseFailure(parser, "l/Tutorial 1", "Missing required parameter: d/.");
        assertParseFailure(parser, "d/2026-10-20", "Missing required parameter: l/.");
        assertParseFailure(parser, "d/ l/Tutorial 1", "Parameter d/ cannot be empty.");
        assertParseFailure(parser, "d/2026-10-20 l/", "Parameter l/ cannot be empty.");
    }

    @Test
    public void parse_repeatedOrUnknownParameter_reportsStructureError() {
        // EP: each supported parameter repeated once, and an unsupported prefix
        assertParseFailure(parser, "d/2026-10-20 d/2026-10-21 l/Tutorial 1",
                "Parameter d/ must be specified only once.");
        assertParseFailure(parser, "d/2026-10-20 l/Tutorial 1 l/Tutorial 2",
                "Parameter l/ must be specified only once.");
        assertParseFailure(parser, "i/A0123456B d/2026-10-20 l/Tutorial 1", "Unknown parameter: i/.");
        assertParseFailure(parser, "D/2026-10-20 l/Tutorial 1", "Unknown parameter: D/.");
    }

    @Test
    public void parse_invalidStructure_reportsUsage() {
        // EP: non-whitespace preamble and both supported internal line-break forms
        assertParseFailure(parser, "unexpected d/2026-10-20 l/Tutorial 1", AttendanceCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "d/2026-10-20 l/Tutorial 1\nextra", AttendanceCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "d/2026-10-20 l/Tutorial 1\rextra", AttendanceCommand.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDateOrGroup_reportsFieldError() {
        // BVA: invalid calendar day, missing zero padding, wrong separator, and month above 12
        // EP: invalid group label value
        for (String date : new String[] {"2026-02-30", "2026-2-03", "20/10/2026", "2026-13-01"}) {
            assertParseFailure(parser, "d/" + date + " l/Tutorial 1", ParserUtil.MESSAGE_INVALID_DATE);
        }
        assertParseFailure(parser, "d/2026-10-20 l/Group/1", Label.MESSAGE_CONSTRAINTS);
    }
}
