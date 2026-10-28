package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;

/**
 * Jackson-friendly version of {@link AttendanceRecord}.
 */
class JsonAdaptedAttendanceRecord {

    static final String MESSAGE_INVALID_DATE = "Attendance date must use a valid YYYY-MM-DD date.";

    private final String date;
    private final String group;

    /**
     * Constructs an adapted attendance record from JSON fields.
     */
    @JsonCreator
    public JsonAdaptedAttendanceRecord(@JsonProperty("date") String date,
            @JsonProperty("group") String group) {
        this.date = date;
        this.group = group;
    }

    /**
     * Converts a model attendance record for JSON storage.
     */
    public JsonAdaptedAttendanceRecord(AttendanceRecord source) {
        date = source.getDate().toString();
        group = source.getGroup().labelName;
    }

    /**
     * Converts this adapted record into its validated model form.
     * Normalises Unicode space separators and trims surrounding whitespace from the date and group before validation.
     *
     * @throws IllegalValueException if the date or group is invalid.
     */
    public AttendanceRecord toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(MESSAGE_INVALID_DATE);
        }
        final String trimmedDate = StringUtil.normaliseSpaces(date).trim();
        if (!trimmedDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new IllegalValueException(MESSAGE_INVALID_DATE);
        }
        LocalDate modelDate;
        try {
            modelDate = LocalDate.parse(trimmedDate);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(MESSAGE_INVALID_DATE);
        }
        if (group == null) {
            throw new IllegalValueException(Label.MESSAGE_CONSTRAINTS);
        }
        final String trimmedGroup = StringUtil.normaliseSpaces(group).trim();
        if (!Label.isValidLabelName(trimmedGroup)) {
            throw new IllegalValueException(Label.MESSAGE_CONSTRAINTS);
        }
        return new AttendanceRecord(modelDate, new Label(trimmedGroup));
    }
}
