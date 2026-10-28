package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.StudentId;

/**
 * Parses a delete command containing exactly one Student ID parameter.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {
    private static final String MESSAGE_MISSING_PARAMETER = "Missing required parameter: i/.";
    private static final String MESSAGE_EMPTY_PARAMETER = "Parameter i/ cannot be empty.";
    private static final String MESSAGE_REPEATED_PARAMETER = "Parameter i/ must be specified only once.";
    private static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %s.";
    private static final Set<Prefix> SUPPORTED_PREFIXES = Set.of(PREFIX_STUDENT_ID);

    /**
     * Parses and validates the command structure and Student ID before creating a delete command.
     *
     * @param args The non-null command arguments.
     * @return A delete command targeting the normalised Student ID.
     * @throws ParseException if the structure or Student ID is invalid.
     * @throws NullPointerException if {@code args} is null.
     */
    @Override
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(trimmedArgs, PREFIX_STUDENT_ID);
        validateStructure(trimmedArgs, arguments);
        StudentId studentId = ParserUtil.parseStudentId(arguments.getValue(PREFIX_STUDENT_ID).orElseThrow());
        return new DeleteCommand(studentId);
    }

    private static void validateStructure(String args, ArgumentMultimap arguments) throws ParseException {
        if (args.contains("\n") || args.contains("\r")) {
            throw new ParseException(DeleteCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectUnknownPrefixes(args);
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(DeleteCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectRepeatedStudentId(arguments);
        requirePresentStudentId(arguments);
        requireNonEmptyStudentId(arguments);
    }

    private static void rejectUnknownPrefixes(String args) throws ParseException {
        Optional<Prefix> unknownPrefix = ArgumentTokenizer.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_PARAMETER, unknownPrefix.get()));
        }
    }

    private static void rejectRepeatedStudentId(ArgumentMultimap arguments) throws ParseException {
        if (arguments.getAllValues(PREFIX_STUDENT_ID).size() > 1) {
            throw new ParseException(MESSAGE_REPEATED_PARAMETER);
        }
    }

    private static void requirePresentStudentId(ArgumentMultimap arguments) throws ParseException {
        if (arguments.getValue(PREFIX_STUDENT_ID).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_PARAMETER);
        }
    }

    private static void requireNonEmptyStudentId(ArgumentMultimap arguments) throws ParseException {
        if (arguments.getValue(PREFIX_STUDENT_ID).get().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_PARAMETER);
        }
    }
}
