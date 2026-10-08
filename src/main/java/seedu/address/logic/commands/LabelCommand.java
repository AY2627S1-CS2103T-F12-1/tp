package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;

/**
 * Adds a group label to an existing student.
 */
public class LabelCommand extends Command {

    public static final String COMMAND_WORD = "label";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD + " "
            + PREFIX_LABEL + "LABEL_NAME "
            + PREFIX_STUDENT_ID + "STUDENT_ID";
    public static final String MESSAGE_SUCCESS = "Added label \"%1$s\" to %2$s (%3$s).";
    public static final String MESSAGE_STUDENT_NOT_FOUND =
            "Student with ID %1$s is not in the records. Consider using add to include the student.";
    public static final String MESSAGE_DUPLICATE_LABEL = "Student %1$s already has label \"%2$s\".";

    private final Label label;
    private final StudentId studentId;

    /**
     * Creates a LabelCommand to add {@code label} to the student with {@code studentId}.
     */
    public LabelCommand(Label label, StudentId studentId) {
        requireNonNull(label);
        requireNonNull(studentId);
        this.label = label;
        this.studentId = studentId;
    }

    @Override
    public boolean isModifyingData() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person student = findStudent(model);
        Label duplicateLabel = findDuplicateLabel(student);
        if (duplicateLabel != null) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_LABEL, studentId, duplicateLabel.labelName));
        }

        Person labelledStudent = new Person(student.getName(), student.getStudentId(), student.getEmail(),
                student.getTags(), addLabelTo(student), student.getRemark());
        model.setPerson(student, labelledStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, label.labelName, student.getName(), studentId));
    }

    private Person findStudent(Model model) throws CommandException {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));
    }

    private Label findDuplicateLabel(Person student) {
        return student.getLabels().stream()
                .filter(existingLabel -> existingLabel.labelName.equalsIgnoreCase(label.labelName))
                .findFirst()
                .orElse(null);
    }

    private Set<Label> addLabelTo(Person student) {
        Set<Label> labels = new HashSet<>(student.getLabels());
        labels.add(label);
        return labels;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LabelCommand otherLabelCommand)) {
            return false;
        }

        return label.equals(otherLabelCommand.label)
                && studentId.equals(otherLabelCommand.studentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("label", label)
                .add("studentId", studentId)
                .toString();
    }
}
