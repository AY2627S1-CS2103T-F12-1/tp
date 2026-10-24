package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        // EP: invalid email
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // EP: null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // EP: empty or whitespace-only email
        assertFalse(Email.isValidEmail("")); // BVA: zero characters
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // EP: missing local part, separator, or domain
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // EP: malformed local part or domain
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part

        // EP: local part has an invalid period placement
        assertFalse(Email.isValidEmail(".peterjack@example.com")); // local part starts with a period
        assertFalse(Email.isValidEmail("peterjack.@example.com")); // local part ends with a period
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // EP: domain has fewer than two labels
        assertFalse(Email.isValidEmail("a@bc")); // requires at least two domain labels
        assertFalse(Email.isValidEmail("test@localhost"));
        assertFalse(Email.isValidEmail("123@145"));

        // EP: final domain label contains nonletters
        assertFalse(Email.isValidEmail("peterjack@example.c0m")); // final label must contain only letters
        assertFalse(Email.isValidEmail("peterjack@example.co-m"));
        assertFalse(Email.isValidEmail("peterjack@example..com"));
        assertFalse(Email.isValidEmail("peterjack@a.-example.com"));
        assertFalse(Email.isValidEmail("peterjack@a.example-.com"));

        // EP: non-ASCII local or domain characters
        assertFalse(Email.isValidEmail("josé@example.com")); // local part must be ASCII
        assertFalse(Email.isValidEmail("peterjack@例子.com"));

        // EP: unsupported control or symbol characters
        assertFalse(Email.isValidEmail("peter\tjack@example.com"));
        assertFalse(Email.isValidEmail("peterjack@example.com\n"));
        assertFalse(Email.isValidEmail("peter!jack@example.com"));

        // EP: valid local and domain syntax
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@b.co")); // BVA: shortest supported parts
        assertTrue(Email.isValidEmail("123@145.com"));
        assertTrue(Email.isValidEmail("-peterjack@example.com"));
        assertTrue(Email.isValidEmail("peterjack-@example.com"));
        assertTrue(Email.isValidEmail("_+a-+_@example.com"));
        assertTrue(Email.isValidEmail("+@example.com"));
        assertTrue(Email.isValidEmail("a@ex--ample.com"));
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_localPartLengthBoundary_validatesLimit() {
        // EP: valid local-part length
        assertTrue(Email.isValidEmail("a".repeat(64) + "@example.com")); // BVA: maximum length

        // EP: local part exceeds its length limit
        // BVA: one character over the maximum
        String oversizedEmail = "a".repeat(65) + "@example.com";
        assertFalse(Email.isValidEmail(oversizedEmail));
        assertThrows(IllegalArgumentException.class, Email.MESSAGE_CONSTRAINTS, () -> new Email(oversizedEmail));
    }

    @Test
    public void isValidEmail_domainLabelLengthBoundary_validatesEachLabel() {
        // EP: valid domain and final-label lengths
        assertTrue(Email.isValidEmail("a@" + "b".repeat(63) + ".com")); // BVA: 63-character label
        assertTrue(Email.isValidEmail("a@b." + "c".repeat(63))); // BVA: 63-character final label

        // EP: domain or final label exceeds its length limit
        assertFalse(Email.isValidEmail("a@" + "b".repeat(64) + ".com")); // BVA: 64-character label
        assertFalse(Email.isValidEmail("a@b." + "c".repeat(64))); // BVA: 64-character final label
    }

    @Test
    public void isValidEmail_totalLengthBoundary_validatesLimit() {
        // EP: valid email at the overall length limit
        String email = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(61);
        assertEquals(Email.MAX_LENGTH, email.length());
        assertTrue(Email.isValidEmail(email)); // BVA: maximum total length

        // EP: email exceeds the overall length limit
        assertFalse(Email.isValidEmail(email + "d")); // BVA: one character over the maximum
    }

    @Test
    public void constructor_uppercaseDomain_preservesLocalPartAndNormalisesDomain() {
        // EP: uppercase domain
        Email email = new Email("SamUEL+Quiz@EXAMPLE.COM");
        assertEquals("SamUEL+Quiz@example.com", email.value);
        assertEquals(email, new Email("SamUEL+Quiz@example.com"));
        assertEquals(email.hashCode(), new Email("SamUEL+Quiz@example.com").hashCode());
        assertFalse(email.equals(new Email("samuel+quiz@example.com")));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email.com");

        // EP: same email value
        assertTrue(email.equals(new Email("valid@email.com")));

        // EP: same object
        assertTrue(email.equals(email));

        // EP: null
        assertFalse(email.equals(null));

        // EP: different type
        assertFalse(email.equals(5.0f));

        // EP: different email
        assertFalse(email.equals(new Email("other.valid@email.com")));
    }
}
