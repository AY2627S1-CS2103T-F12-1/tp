package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String MISSING_LABEL_MESSAGE = "Person's label field is missing!";
    public static final String MESSAGE_DUPLICATE_LABEL = "Person's labels contain duplicate label(s).";
    public static final String MESSAGE_DUPLICATE_ATTENDANCE = "Person's attendance contains duplicate record(s).";
    public static final String MISSING_ATTENDANCE_MESSAGE = "Person's attendance record is missing!";

    private final String name;
    private final String studentId;
    private final String email;
    private final String remark;
    private final List<JsonAdaptedLabel> labels = new ArrayList<>();
    private final List<JsonAdaptedAttendanceRecord> attendance = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * Defaults a missing or null remark to empty text for compatibility with older data files.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("studentId") String studentId,
            @JsonProperty("email") String email, @JsonProperty("labels") List<JsonAdaptedLabel> labels,
            @JsonProperty("remark") String remark) {
        this(name, studentId, email, labels, remark, null);
    }

    /**
     * Constructs an adapted person, defaulting missing attendance to an empty set.
     */
    public JsonAdaptedPerson(String name, String studentId, String email, List<JsonAdaptedLabel> labels,
            String remark, List<JsonAdaptedAttendanceRecord> attendance) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.remark = remark == null ? "" : remark;
        if (labels != null) {
            this.labels.addAll(labels);
        }
        setAttendance(attendance);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        studentId = source.getStudentId().value;
        email = source.getEmail().value;
        remark = source.getRemark().value;
        labels.addAll(source.getLabels().stream()
                .map(JsonAdaptedLabel::new)
                .collect(Collectors.toList()));
        attendance.addAll(source.getAttendance().stream()
                .map(JsonAdaptedAttendanceRecord::new)
                .collect(Collectors.toList()));
    }

    /**
     * Loads attendance records, treating a missing or null value as empty.
     */
    @JsonProperty("attendance")
    public void setAttendance(List<JsonAdaptedAttendanceRecord> attendance) {
        this.attendance.clear();
        if (attendance != null) {
            this.attendance.addAll(attendance);
        }
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     * Normalises Unicode space separators and trims surrounding whitespace from text fields before validation.
     * Remarks are only trimmed, preserving their internal spacing.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Label> personLabels = new ArrayList<>();
        final Set<String> labelNames = new HashSet<>();
        for (JsonAdaptedLabel label : labels) {
            if (label == null) {
                throw new IllegalValueException(MISSING_LABEL_MESSAGE);
            }
            Label modelLabel = label.toModelType();
            if (!labelNames.add(modelLabel.labelName.toLowerCase(Locale.ROOT))) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_LABEL);
            }
            personLabels.add(modelLabel);
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        final String trimmedName = StringUtil.normaliseSpaces(name).trim();
        if (!Name.isValidName(trimmedName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(trimmedName);

        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    StudentId.class.getSimpleName()));
        }
        final String trimmedStudentId = StringUtil.normaliseSpaces(studentId).trim();
        if (!StudentId.isValidStudentId(trimmedStudentId)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        final StudentId modelStudentId = new StudentId(trimmedStudentId);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        final String trimmedEmail = StringUtil.normaliseSpaces(email).trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(trimmedEmail);

        final Set<Label> modelLabels = new HashSet<>(personLabels);
        final String trimmedRemark = StringUtil.trimSpaces(remark);
        if (!Remark.isValidRemark(trimmedRemark)) {
            throw new IllegalValueException(Remark.MESSAGE_CONSTRAINTS);
        }
        final Remark modelRemark = new Remark(trimmedRemark);
        final Set<AttendanceRecord> modelAttendance = new HashSet<>();
        for (JsonAdaptedAttendanceRecord record : attendance) {
            if (record == null) {
                throw new IllegalValueException(MISSING_ATTENDANCE_MESSAGE);
            }
            if (!modelAttendance.add(record.toModelType())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_ATTENDANCE);
            }
        }
        return new Person(modelName, modelStudentId, modelEmail, modelLabels, modelRemark, modelAttendance);
    }

}
