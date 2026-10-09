package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import seedu.address.logic.commands.LabelCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new LabelCommand object.
 */
public class LabelCommandParser implements Parser<LabelCommand> {

    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Invalid command format. " + LabelCommand.MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_LABEL = "Missing required parameter: l/.";
    public static final String MESSAGE_EMPTY_LABEL = "Parameter l/ cannot be empty.";
    public static final String MESSAGE_DUPLICATE_LABEL = "Parameter l/ must be specified only once.";
    public static final String MESSAGE_LABEL_CONTAINS_SLASH = "Label name cannot contain '/'.";
    public static final String MESSAGE_LABEL_CONTAINS_ASCII_CONTROL =
            "Label name contains disallowed ASCII control characters. Consider removing them.";
    public static final String MESSAGE_MISSING_STUDENT_ID = "Missing required parameter: i/.";
    public static final String MESSAGE_EMPTY_STUDENT_ID = "Parameter i/ cannot be empty.";
    public static final String MESSAGE_DUPLICATE_STUDENT_ID = "Parameter i/ must be specified only once.";

    /**
     * Parses the given {@code String} of arguments in the context of the LabelCommand
     * and returns a LabelCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public LabelCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_LABEL, PREFIX_STUDENT_ID);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }

        validatePrefix(argMultimap, PREFIX_LABEL, MESSAGE_MISSING_LABEL,
                MESSAGE_EMPTY_LABEL, MESSAGE_DUPLICATE_LABEL);
        validatePrefix(argMultimap, PREFIX_STUDENT_ID, MESSAGE_MISSING_STUDENT_ID,
                MESSAGE_EMPTY_STUDENT_ID, MESSAGE_DUPLICATE_STUDENT_ID);

        Label label = parseLabel(argMultimap.getValue(PREFIX_LABEL).get());
        StudentId studentId = ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENT_ID).get());
        return new LabelCommand(label, studentId);
    }

    private static void validatePrefix(ArgumentMultimap argMultimap, Prefix prefix, String missingMessage,
            String emptyMessage, String duplicateMessage) throws ParseException {
        if (argMultimap.getAllValues(prefix).isEmpty()) {
            throw new ParseException(missingMessage);
        }
        if (argMultimap.getAllValues(prefix).size() > 1) {
            throw new ParseException(duplicateMessage);
        }
        if (argMultimap.getValue(prefix).get().isEmpty()) {
            throw new ParseException(emptyMessage);
        }
    }

    private static Label parseLabel(String label) throws ParseException {
        if (label.contains("/")) {
            throw new ParseException(MESSAGE_LABEL_CONTAINS_SLASH);
        }
        if (containsAsciiControlCharacter(label)) {
            throw new ParseException(MESSAGE_LABEL_CONTAINS_ASCII_CONTROL);
        }
        return ParserUtil.parseLabel(label);
    }

    private static boolean containsAsciiControlCharacter(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) <= 0x1F || text.charAt(i) == 0x7F) {
                return true;
            }
        }
        return false;
    }
}
