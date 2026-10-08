package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;

/**
 * Filters the displayed student list by group label.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD + " "
            + PREFIX_LABEL + "LABEL_NAME";
    public static final String MESSAGE_STUDENTS_FOUND = "%1$d students found with label \"%2$s\".";
    public static final String MESSAGE_NO_STUDENTS_FOUND = "No students found with label \"%1$s\".";

    private final Label label;

    /**
     * Creates a FilterCommand to display students with {@code label}.
     */
    public FilterCommand(Label label) {
        requireNonNull(label);
        this.label = label;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredPersonList(this::hasMatchingLabel);
        int numberOfMatchingStudents = model.getFilteredPersonList().size();
        String resultMessage = numberOfMatchingStudents == 0
                ? String.format(MESSAGE_NO_STUDENTS_FOUND, label.labelName)
                : String.format(MESSAGE_STUDENTS_FOUND, numberOfMatchingStudents, label.labelName);
        return new CommandResult(resultMessage);
    }

    private boolean hasMatchingLabel(Person person) {
        return person.getLabels().stream()
                .anyMatch(existingLabel -> existingLabel.labelName.equalsIgnoreCase(label.labelName));
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
