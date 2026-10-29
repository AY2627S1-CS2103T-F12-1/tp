package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedAttendanceRecord.MESSAGE_INVALID_DATE;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;

public class JsonAdaptedAttendanceRecordTest {
    private static final String VALID_DATE = "2026-10-20";
    private static final String VALID_GROUP = "Tutorial 1";

    @Test
    public void toModelType_validAttendanceDetails_returnsAttendanceRecord() throws Exception {
        // EP: valid date and valid group name
        JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(VALID_DATE, VALID_GROUP);
        AttendanceRecord expectedRecord = new AttendanceRecord(LocalDate.parse(VALID_DATE), new Label(VALID_GROUP));
        assertEquals(expectedRecord, record.toModelType());

        // EP: constructed from domain model AttendanceRecord
        JsonAdaptedAttendanceRecord fromModelRecord = new JsonAdaptedAttendanceRecord(expectedRecord);
        assertEquals(expectedRecord, fromModelRecord.toModelType());
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        // EP: null date
        JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(null, VALID_GROUP);
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, record::toModelType);
    }

    @Test
    public void toModelType_invalidDateFormat_throwsIllegalValueException() {
        // EP: malformed date strings not adhering to YYYY-MM-DD
        // BVA: empty string
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("", VALID_GROUP).toModelType());

        // EP: wrong separator
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("2026/10/20", VALID_GROUP).toModelType());

        // EP: day first
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("20-10-2026", VALID_GROUP).toModelType());

        // EP: arbitrary text
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("not-a-date", VALID_GROUP).toModelType());
    }

    @Test
    public void toModelType_invalidCalendarDate_throwsIllegalValueException() {
        // EP: matches YYYY-MM-DD pattern but invalid calendar date
        // BVA: day 30 in February
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("2026-02-30", VALID_GROUP).toModelType());

        // BVA: month 13
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("2026-13-01", VALID_GROUP).toModelType());

        // BVA: day 00
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE, () ->
                new JsonAdaptedAttendanceRecord("2026-10-00", VALID_GROUP).toModelType());
    }

    @Test
    public void toModelType_nullGroup_throwsIllegalValueException() {
        // EP: null group
        JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(VALID_DATE, null);
        assertThrows(IllegalValueException.class, Label.MESSAGE_CONSTRAINTS, record::toModelType);
    }

    @Test
    public void toModelType_invalidGroupName_throwsIllegalValueException() {
        // EP: blank group name
        // BVA: empty string
        assertThrows(IllegalValueException.class, Label.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedAttendanceRecord(VALID_DATE, "").toModelType());

        // EP: disallowed special characters
        assertThrows(IllegalValueException.class, Label.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedAttendanceRecord(VALID_DATE, "Tutorial/1").toModelType());

        // EP: exceeds maximum label length
        // BVA: MAX_NAME_LENGTH + 1 characters
        String overlongGroup = "a".repeat(Label.MAX_NAME_LENGTH + 1);
        assertThrows(IllegalValueException.class, Label.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedAttendanceRecord(VALID_DATE, overlongGroup).toModelType());
    }
}
