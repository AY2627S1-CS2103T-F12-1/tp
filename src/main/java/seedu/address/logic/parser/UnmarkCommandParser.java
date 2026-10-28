package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

/**
 * Parses the parameters needed to unmark one student's attendance.
 */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {

    private static final List<Prefix> REQUIRED_PREFIXES = List.of(PREFIX_STUDENT_ID, PREFIX_DATE, PREFIX_LABEL);
    private static final Set<Prefix> SUPPORTED_PREFIXES = Set.copyOf(REQUIRED_PREFIXES);
    private static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %s.";
    private static final String MESSAGE_REPEATED_PARAMETER = "Parameter %s must be specified only once.";
    private static final String MESSAGE_MISSING_PARAMETER = "Missing required parameter: %s.";
    private static final String MESSAGE_EMPTY_PARAMETER = "Parameter %s cannot be empty.";

    /**
     * Parses and validates a student's ID, attendance date, and group label.
     *
     * @param args The non-null command arguments.
     * @return A command ready to unmark attendance.
     * @throws ParseException if the command or a parameter is invalid.
     */
    @Override
    public UnmarkCommand parse(String args) throws ParseException {
        String trimmedArgs = StringUtil.normaliseSpaces(args).trim();
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(trimmedArgs,
                PREFIX_STUDENT_ID, PREFIX_DATE, PREFIX_LABEL);
        validateStructure(trimmedArgs, arguments);

        StudentId studentId = ParserUtil.parseStudentId(arguments.getValue(PREFIX_STUDENT_ID).orElseThrow());
        LocalDate date = ParserUtil.parseDate(arguments.getValue(PREFIX_DATE).orElseThrow());
        Label group = ParserUtil.parseLabel(arguments.getValue(PREFIX_LABEL).orElseThrow());
        return new UnmarkCommand(studentId, date, group);
    }

    private static void validateStructure(String args, ArgumentMultimap arguments) throws ParseException {
        if (args.contains("\n") || args.contains("\r")) {
            throw new ParseException(UnmarkCommand.MESSAGE_INVALID_FORMAT);
        }
        Optional<Prefix> unknownPrefix = ArgumentTokenizer.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_PARAMETER, unknownPrefix.get()));
        }
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(UnmarkCommand.MESSAGE_INVALID_FORMAT);
        }
        for (Prefix prefix : REQUIRED_PREFIXES) {
            if (arguments.getAllValues(prefix).size() > 1) {
                throw new ParseException(String.format(MESSAGE_REPEATED_PARAMETER, prefix));
            }
        }
        for (Prefix prefix : REQUIRED_PREFIXES) {
            Optional<String> value = arguments.getValue(prefix);
            if (value.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_MISSING_PARAMETER, prefix));
            }
            if (value.get().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_EMPTY_PARAMETER, prefix));
            }
        }
    }
}
