package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    private static final String VALID_STUDENT_ID_OTHER_BOB = "C0123456D";
    private static final String VALID_LABEL_TUTORIAL = "Discrete Math Tutorial";

    @Test
    public void constructor_nullParameters_throwsNullPointerException() {
        // EP: null name
        assertThrows(NullPointerException.class, () ->
                new Person(null, ALICE.getStudentId(), ALICE.getEmail()));
        // EP: null studentId
        assertThrows(NullPointerException.class, () ->
                new Person(ALICE.getName(), null, ALICE.getEmail()));
        // EP: null email
        assertThrows(NullPointerException.class, () ->
                new Person(ALICE.getName(), ALICE.getStudentId(), null));
        // EP: null labels
        assertThrows(NullPointerException.class, () ->
                new Person(ALICE.getName(), ALICE.getStudentId(), ALICE.getEmail(), null, new Remark("")));
        // EP: null remark
        assertThrows(NullPointerException.class, () ->
                new Person(ALICE.getName(), ALICE.getStudentId(), ALICE.getEmail(), ALICE.getLabels(), null));
        // EP: null attendance
        assertThrows(NullPointerException.class, () ->
                new Person(ALICE.getName(), ALICE.getStudentId(), ALICE.getEmail(),
                        ALICE.getLabels(), ALICE.getRemark(), null));
    }

    @Test
    public void getLabels_modifySet_throwsUnsupportedOperationException() {
        // EP: modify unmodifiable labels collection
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getLabels().clear());
    }

    @Test
    public void getAttendance_modifySet_throwsUnsupportedOperationException() {
        // EP: modify unmodifiable attendance collection
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getAttendance().clear());
    }

    @Test
    public void isSamePerson() {
        // EP: same object
        assertTrue(ALICE.isSamePerson(ALICE));

        // EP: null
        assertFalse(ALICE.isSamePerson(null));

        // EP: same student ID, all other attributes different
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB)
                .withEmail(VALID_EMAIL_BOB).withLabels(VALID_LABEL_TUTORIAL).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // EP: different student ID, all other attributes same
        editedAlice = new PersonBuilder(ALICE).withStudentId(VALID_STUDENT_ID_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // EP: student ID differs only in case, all other attributes same
        Person anotherPersonBob = new PersonBuilder(BOB)
                .withStudentId(BOB.getStudentId().toString().toLowerCase())
                .build();
        assertTrue(BOB.isSamePerson(anotherPersonBob));

        // EP: same name, different student ID
        anotherPersonBob = new PersonBuilder(BOB).withStudentId(VALID_STUDENT_ID_OTHER_BOB).build();
        assertFalse(BOB.isSamePerson(anotherPersonBob));
    }

    @Test
    public void equals() {
        // EP: same values
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // EP: same object
        assertTrue(ALICE.equals(ALICE));

        // EP: null
        assertFalse(ALICE.equals(null));

        // EP: different type
        assertFalse(ALICE.equals(5));

        // EP: different person
        assertFalse(ALICE.equals(BOB));

        // EP: different name
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // EP: different student ID
        editedAlice = new PersonBuilder(ALICE).withStudentId(VALID_STUDENT_ID_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // EP: different email
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // EP: different labels
        editedAlice = new PersonBuilder(ALICE).withLabels(VALID_LABEL_TUTORIAL).build();
        assertFalse(ALICE.equals(editedAlice));

        // EP: different attendance records
        AttendanceRecord record = new AttendanceRecord(LocalDate.parse("2026-10-20"), new Label("Tutorial 1"));
        editedAlice = new PersonBuilder(ALICE).withAttendance(record).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void constructor_withoutRemark_defaultsToEmptyRemark() {
        // EP: 3-parameter constructor defaults remark and attendance
        Person person = new Person(ALICE.getName(), ALICE.getStudentId(), ALICE.getEmail());

        assertEquals(ALICE.getStudentId(), person.getStudentId());
        assertEquals(new Remark(""), person.getRemark());
        assertTrue(person.getAttendance().isEmpty());
    }

    @Test
    public void constructor_nullRemark_throwsNullPointerException() {
        // EP: null remark
        assertThrows(NullPointerException.class, () -> withRemark(ALICE, null));
    }

    @Test
    public void equals_differentRemark_returnsFalseButSamePerson() {
        // EP: same student ID, different remark
        Person remarkedAlice = withRemark(ALICE, new Remark("Needs help with recursion"));
        assertEquals(new Remark("Needs help with recursion"), remarkedAlice.getRemark());
        assertFalse(ALICE.equals(remarkedAlice));
        assertTrue(ALICE.isSamePerson(remarkedAlice));

        // EP: same values with custom remark
        Person remarkedAliceCopy = withRemark(ALICE, new Remark("Needs help with recursion"));
        assertEquals(remarkedAlice, remarkedAliceCopy);
        assertEquals(remarkedAlice.hashCode(), remarkedAliceCopy.hashCode());
        assertTrue(remarkedAlice.toString().contains("remark=Needs help with recursion"));
    }

    private Person withRemark(Person person, Remark remark) {
        return new Person(person.getName(), person.getStudentId(), person.getEmail(), person.getLabels(), remark);
    }

    @Test
    public void toStringMethod() {
        // EP: toString includes all attributes
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", studentId=" + ALICE.getStudentId() + ", email=" + ALICE.getEmail()
                + ", labels=" + ALICE.getLabels()
                + ", remark=" + ALICE.getRemark()
                + ", attendance=" + ALICE.getAttendance() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
