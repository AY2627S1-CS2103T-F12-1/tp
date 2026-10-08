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
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String MISSING_TAG_MESSAGE = "Person's tag field is missing!";
    public static final String MISSING_LABEL_MESSAGE = "Person's label field is missing!";
    public static final String MESSAGE_DUPLICATE_LABEL = "Person's labels contain duplicate label(s).";

    private final String name;
    private final String studentId;
    private final String email;
    private final String remark;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedLabel> labels = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * Defaults a missing or null remark to empty text for compatibility with older data files.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("studentId") String studentId,
            @JsonProperty("email") String email, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("labels") List<JsonAdaptedLabel> labels, @JsonProperty("remark") String remark) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.remark = remark == null ? "" : remark;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (labels != null) {
            this.labels.addAll(labels);
        }
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} without labels for older tests and callers.
     */
    public JsonAdaptedPerson(String name, String studentId, String email, List<JsonAdaptedTag> tags, String remark) {
        this(name, studentId, email, tags, List.of(), remark);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        studentId = source.getStudentId().value;
        email = source.getEmail().value;
        remark = source.getRemark().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        labels.addAll(source.getLabels().stream()
                .map(JsonAdaptedLabel::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(MISSING_TAG_MESSAGE);
            }
            personTags.add(tag.toModelType());
        }
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
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    StudentId.class.getSimpleName()));
        }
        if (!StudentId.isValidStudentId(studentId)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        final StudentId modelStudentId = new StudentId(studentId);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        final Set<Label> modelLabels = new HashSet<>(personLabels);
        if (!Remark.isValidRemark(remark)) {
            throw new IllegalValueException(Remark.MESSAGE_CONSTRAINTS);
        }
        final Remark modelRemark = new Remark(remark);
        return new Person(modelName, modelStudentId, modelEmail, modelTags, modelLabels, modelRemark);
    }

}
