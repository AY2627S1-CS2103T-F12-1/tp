package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {
    @Test
    public void constructor_lengthBoundary_acceptsLimitAndRejectsOverflow() {
        // EP: valid remark at the maximum length
        // BVA: 4000 characters
        String atLimit = "x".repeat(Remark.MAX_LENGTH);
        assertEquals(atLimit, new Remark(atLimit).value);

        // EP: remark exceeds the maximum length
        // BVA: 4001 characters
        assertThrows(IllegalArgumentException.class, Remark.MESSAGE_CONSTRAINTS, () ->
                new Remark(atLimit + "x"));
    }

    @Test
    public void isValidRemark_unicode_countsCodePoints() {
        // EP: supplementary Unicode characters at the code-point limit
        String atLimit = "\uD83D\uDE00".repeat(Remark.MAX_LENGTH);
        assertTrue(Remark.isValidRemark(atLimit));
        assertEquals(atLimit, new Remark(atLimit).value);
        // EP: remark exceeds the code-point limit
        assertFalse(Remark.isValidRemark(atLimit + "x")); // BVA: one code point over the maximum

        // EP: empty remark
        assertTrue(Remark.isValidRemark("")); // BVA: zero characters

        // EP: null remark
        assertThrows(NullPointerException.class, () -> Remark.isValidRemark(null));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_anyText_preservesValue() {
        // EP: empty, whitespace-only, ordinary, and multi-line remarks
        for (String text : new String[] {"", " ", "Needs help with recursion", "Follow up: 你好!\nNext week"}) {
            assertEquals(text, new Remark(text).value);
            assertEquals(text, new Remark(text).toString());
        }
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Needs help");
        Remark copy = new Remark("Needs help");
        // EP: same object
        assertTrue(remark.equals(remark));
        // EP: same value
        assertTrue(remark.equals(copy));
        assertEquals(remark.hashCode(), copy.hashCode());
        // EP: null
        assertFalse(remark.equals(null));
        // EP: different type
        assertFalse(remark.equals("Needs help"));
        // EP: different remark
        assertFalse(remark.equals(new Remark("Different remark")));
    }
}
