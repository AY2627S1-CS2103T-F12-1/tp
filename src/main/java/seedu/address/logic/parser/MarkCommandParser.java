package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

/**
 * Parses the parameters needed to mark one student's attendance.
 */
public class MarkCommandParser implements Parser<MarkCommand> {

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
     * @return A command ready to mark attendance.
     * @throws ParseException if the command or a parameter is invalid.
     * @throws NullPointerException if {@code args} is null.
     */
    @Override
    public MarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(trimmedArgs,
                PREFIX_STUDENT_ID, PREFIX_DATE, PREFIX_LABEL);
        validateStructure(trimmedArgs, arguments);

        StudentId studentId = ParserUtil.parseStudentId(arguments.getValue(PREFIX_STUDENT_ID).orElseThrow());
        LocalDate date = ParserUtil.parseDate(arguments.getValue(PREFIX_DATE).orElseThrow());
        Label group = ParserUtil.parseLabel(arguments.getValue(PREFIX_LABEL).orElseThrow());
        return new MarkCommand(studentId, date, group);
    }

    private static void validateStructure(String args, ArgumentMultimap arguments) throws ParseException {
        if (args.contains("\n") || args.contains("\r")) {
            throw new ParseException(MarkCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectUnknownPrefixes(args);
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(MarkCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectRepeatedPrefixes(arguments);
        requireMandatoryValues(arguments);
    }

    private static void rejectUnknownPrefixes(String args) throws ParseException {
        Optional<Prefix> unknownPrefix = ArgumentTokenizer.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_PARAMETER, unknownPrefix.get()));
        }
    }

    private static void rejectRepeatedPrefixes(ArgumentMultimap arguments) throws ParseException {
        for (Prefix prefix : REQUIRED_PREFIXES) {
            if (arguments.getAllValues(prefix).size() > 1) {
                throw new ParseException(String.format(MESSAGE_REPEATED_PARAMETER, prefix));
            }
        }
    }

    private static void requireMandatoryValues(ArgumentMultimap arguments) throws ParseException {
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
