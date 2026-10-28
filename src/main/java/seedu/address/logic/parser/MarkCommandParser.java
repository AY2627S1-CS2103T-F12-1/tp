package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the parameters needed to mark one student's attendance.
 */
public class MarkCommandParser implements Parser<MarkCommand> {

    /**
     * Parses and validates a student's ID, attendance date, and group label.
     *
     * @param args The non-null command arguments.
     * @return A command ready to mark attendance.
     * @throws ParseException if the command or a parameter is invalid.
     * @throws NullPointerException if {@code args} is null.
     */
    @Override
    public MarkCommand parse(String args) throws ParseException {
        MarkUnmarkCommandParserSupport.MarkUnmarkArguments arguments =
                MarkUnmarkCommandParserSupport.parseArguments(args, MarkCommand.MESSAGE_INVALID_FORMAT);
        return new MarkCommand(arguments.studentId(), arguments.date(), arguments.group());
    }
}
