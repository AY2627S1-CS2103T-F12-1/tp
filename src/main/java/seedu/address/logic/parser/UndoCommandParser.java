package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UndoCommand}.
 */
public class UndoCommandParser implements Parser<UndoCommand> {

    /**
     * Parses arguments supplied after the undo command word.
     *
     * @param args arguments supplied after the command word.
     * @return an undo command.
     * @throws ParseException if the arguments do not conform to the command format.
     */
    @Override
    public UndoCommand parse(String args) throws ParseException {
        if (!StringUtil.normaliseSpaces(args).isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE));
        }
        return new UndoCommand();
    }
}
