package seedu.address.logic.parser;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;

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
     * @throws ParseException because filter parsing is not implemented yet
     */
    public FilterCommand parse(String args) throws ParseException {
        throw new ParseException("Filter command parsing is not implemented yet.");
    }
}
