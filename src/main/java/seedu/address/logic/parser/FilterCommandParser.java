package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;

/**
 * Parses input arguments and creates a new FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Invalid command format. " + FilterCommand.MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_LABEL = "Missing required parameter: l/.";
    public static final String MESSAGE_EMPTY_LABEL = "Parameter l/ cannot be empty.";
    public static final String MESSAGE_DUPLICATE_LABEL = "Parameter l/ must be specified only once.";

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_LABEL);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }

        validateLabelPrefix(argMultimap);

        Label label = ParserUtil.parseLabel(argMultimap.getValue(PREFIX_LABEL).get());
        return new FilterCommand(label);
    }

    private static void validateLabelPrefix(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getAllValues(PREFIX_LABEL).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_LABEL);
        }
        if (argMultimap.getAllValues(PREFIX_LABEL).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_LABEL);
        }
        if (argMultimap.getValue(PREFIX_LABEL).get().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_LABEL);
        }
    }
}
