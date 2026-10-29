package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final StudentId studentId;
    private final Email email;

    // Data fields
    private final Remark remark;
    private final Set<Label> labels = new HashSet<>();
    private final Set<AttendanceRecord> attendance = new HashSet<>();

    /**
     * Constructs a person with no labels and an empty remark.
     *
     * @param name The non-null student name.
     * @param studentId The non-null student ID.
     * @param email The non-null email address.
     * @throws NullPointerException if any parameter is null.
     */
    public Person(Name name, StudentId studentId, Email email) {
        this(name, studentId, email, Set.of(), new Remark(""));
    }

    /**
     * Constructs a person with the supplied remark and no labels.
     *
     * @param name The non-null student name.
     * @param studentId The non-null student ID.
     * @param email The non-null email address.
     * @param remark The non-null remark.
     * @throws NullPointerException if any parameter is null.
     */
    public Person(Name name, StudentId studentId, Email email, Remark remark) {
        this(name, studentId, email, Set.of(), remark);
    }

    /**
     * Constructs a person with the supplied details, labels, and remark.
     *
     * @param name The non-null student name.
     * @param studentId The non-null student ID.
     * @param email The non-null email address.
     * @param labels The non-null set of labels.
     * @param remark The non-null remark.
     * @throws NullPointerException if any parameter is null.
     */
    public Person(Name name, StudentId studentId, Email email, Set<Label> labels, Remark remark) {
        this(name, studentId, email, labels, remark, Set.of());
    }

    /**
     * Constructs a person with the supplied details and attendance records.
     *
     * @param name The non-null student name.
     * @param studentId The non-null student ID.
     * @param email The non-null email address.
     * @param labels The non-null set of labels.
     * @param remark The non-null remark.
     * @param attendance The non-null set of attendance records.
     * @throws NullPointerException if any parameter is null.
     */
    public Person(Name name, StudentId studentId, Email email, Set<Label> labels, Remark remark,
            Set<AttendanceRecord> attendance) {
        requireAllNonNull(name, studentId, email, labels, remark, attendance);
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.remark = remark;
        this.labels.addAll(labels);
        this.attendance.addAll(attendance);
    }

    public Name getName() {
        return name;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public Email getEmail() {
        return email;
    }

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns an immutable label set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Label> getLabels() {
        return Collections.unmodifiableSet(labels);
    }

    /**
     * Returns an unmodifiable set of this student's attendance records.
     */
    public Set<AttendanceRecord> getAttendance() {
        return Collections.unmodifiableSet(attendance);
    }

    /**
     * Returns true if both persons have the same student ID.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getStudentId().equals(getStudentId());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && studentId.equals(otherPerson.studentId)
                && email.equals(otherPerson.email)
                && remark.equals(otherPerson.remark)
                && labels.equals(otherPerson.labels)
                && attendance.equals(otherPerson.attendance);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, studentId, email, labels, remark, attendance);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studentId", studentId)
                .add("email", email)
                .add("labels", labels)
                .add("remark", remark)
                .add("attendance", attendance)
                .toString();
    }

}
