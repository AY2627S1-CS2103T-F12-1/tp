package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        // EP: invalid student id
        String invalidStudentId = "";
        assertThrows(IllegalArgumentException.class, () -> new StudentId(invalidStudentId));
    }

    @Test
    public void constructor_lowerCaseStudentId_normalizesToUpperCase() {
        // EP: lower case student id
        assertEquals("A0123456B", new StudentId("a0123456b").toString());
    }

    @Test
    public void isValidStudentId() {
        // EP: null Student ID
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // EP: empty or whitespace-only Student ID
        assertFalse(StudentId.isValidStudentId("")); // BVA: zero characters
        assertFalse(StudentId.isValidStudentId(" ")); // spaces only

        // EP: Student ID has an invalid length
        assertFalse(StudentId.isValidStudentId("A0123456")); // BVA: eight characters
        assertFalse(StudentId.isValidStudentId("A0123456BC")); // BVA: ten characters

        // EP: Student ID contains a non-ASCII letter, space, or punctuation
        assertFalse(StudentId.isValidStudentId("A012 456B")); // spaces within student ID
        assertFalse(StudentId.isValidStudentId("A012345-B")); // punctuation within student ID
        assertFalse(StudentId.isValidStudentId("A012345_B")); // underscores within student ID
        assertFalse(StudentId.isValidStudentId("A012345\u00c9")); // non-ASCII letter

        // EP: valid nine-character Student IDs
        assertTrue(StudentId.isValidStudentId("A0123456B")); // BVA: exactly nine characters
        assertTrue(StudentId.isValidStudentId("a0123456b")); // lowercase letters and digits
        assertTrue(StudentId.isValidStudentId("123456789")); // digits only
        assertTrue(StudentId.isValidStudentId("ABCDEFGHI")); // letters only
    }

    @Test
    public void equals() {
        StudentId studentId = new StudentId("A0123456B");

        // EP: same normalised Student ID
        assertTrue(studentId.equals(new StudentId("a0123456b")));

        // EP: same object
        assertTrue(studentId.equals(studentId));

        // EP: null
        assertFalse(studentId.equals(null));

        // EP: different type
        assertFalse(studentId.equals(5.0f));

        // EP: different Student ID
        assertFalse(studentId.equals(new StudentId("A0123456C")));
    }

    @Test
    public void hashCode_sameNormalizedValue_returnsSameHashCode() {
        // EP: same normalized value
        assertEquals(new StudentId("A0123456B").hashCode(), new StudentId("a0123456b").hashCode());
    }
}
