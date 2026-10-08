package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.label.Label;

/**
 * Filters the displayed student list by group label.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD + " "
            + PREFIX_LABEL + "LABEL_NAME";

    private final Label label;

    /**
     * Creates a FilterCommand to display students with {@code label}.
     */
    public FilterCommand(Label label) {
        requireNonNull(label);
        this.label = label;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException("Filter command execution is not implemented yet.");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return label.equals(otherFilterCommand.label);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("label", label)
                .toString();
    }
}
