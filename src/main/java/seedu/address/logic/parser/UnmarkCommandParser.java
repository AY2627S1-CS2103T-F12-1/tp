package seedu.address.logic.parser;

<<<<<<< HEAD
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.StringUtil;
=======
>>>>>>> master
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the parameters needed to unmark one student's attendance.
 */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {

    /**
     * Parses and validates a student's ID, attendance date, and group label.
     *
     * @param args The non-null command arguments.
     * @return A command ready to unmark attendance.
     * @throws ParseException if the command or a parameter is invalid.
     */
    @Override
    public UnmarkCommand parse(String args) throws ParseException {
        MarkUnmarkCommandParserSupport.MarkUnmarkArguments arguments =
                MarkUnmarkCommandParserSupport.parseArguments(args, UnmarkCommand.MESSAGE_INVALID_FORMAT);
        return new UnmarkCommand(arguments.studentId(), arguments.date(), arguments.group());
    }
}
