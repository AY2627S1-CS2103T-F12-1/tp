package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.label.Label;
import seedu.address.testutil.PersonBuilder;

public class LabelMatchesPredicateTest {
    @Test
    public void constructor_nullLabel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LabelMatchesPredicate(null));
    }

    @Test
    public void test_completeLabelMatchesIgnoringCase() {
        LabelMatchesPredicate predicate = new LabelMatchesPredicate(new Label("Discrete Math Tutorial"));
        assertTrue(predicate.test(new PersonBuilder().withLabels("Discrete Math Tutorial").build()));
        assertTrue(predicate.test(new PersonBuilder().withLabels("Other", "discrete math tutorial").build()));
        assertTrue(new LabelMatchesPredicate(new Label("discrete math tutorial"))
                .test(new PersonBuilder().withLabels("Discrete Math Tutorial").build()));
        assertFalse(predicate.test(new PersonBuilder().withLabels("Discrete Math Tutorial 1").build()));
        assertFalse(predicate.test(new PersonBuilder().withLabels("Discrete Math", "Tutorial").build()));
        assertFalse(predicate.test(new PersonBuilder().withLabels().build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Discrete Math Tutorial").withLabels().build()));
    }

    @Test
    public void equals() {
        LabelMatchesPredicate predicate = new LabelMatchesPredicate(new Label("Tutorial 1"));
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new LabelMatchesPredicate(new Label("Tutorial 1"))));
        assertFalse(predicate.equals(new LabelMatchesPredicate(new Label("Tutorial 2"))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(1));
    }

    @Test
    public void toStringMethod() {
        Label label = new Label("Tutorial 1");
        LabelMatchesPredicate predicate = new LabelMatchesPredicate(label);
        assertEquals(LabelMatchesPredicate.class.getCanonicalName() + "{label=" + label + "}", predicate.toString());
    }
}
