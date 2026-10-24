package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.StudentId;

/**
 * Tests Student ID-based deletion against complete and filtered student lists.
 */
public class DeleteCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullId_throwsNullPointerException() {
        // EP: null id
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    @Test
    public void execute_existingIdUnfilteredList_success() {
        // EP: existing id unfiltered list
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_visibleStudentFilteredList_preservesFilter() {
        // EP: visible student filtered list
        NameContainsKeywordsPredicate filter = new NameContainsKeywordsPredicate(List.of("Alice"));
        model.updateFilteredPersonList(filter);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(filter);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_hiddenStudentFilteredList_deletesFromCompleteRecords() {
        // EP: hidden student filtered list
        NameContainsKeywordsPredicate filter = new NameContainsKeywordsPredicate(List.of("Benson"));
        model.updateFilteredPersonList(filter);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(filter);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertFalse(model.hasPerson(ALICE));
    }

    @Test
    public void execute_emptyDisplayedList_deletesExistingStudent() {
        // EP: empty displayed list
        // BVA: zero visible students
        model.updateFilteredPersonList(person -> false);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> false);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_lowercaseId_matchesNormalisedStudentId() {
        // EP: lowercase id
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(new StudentId("a1234567b")), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_unknownId_preservesRecordsAndFilter() {
        // EP: unknown id
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Benson")));
        assertCommandFailure(new DeleteCommand(new StudentId("Z9999999Z")), model,
                "Student with ID Z9999999Z is not in the records.");
    }

    @Test
    public void execute_emptyAddressBook_reportsStudentNotFound() {
        // EP: target Student ID absent from an empty address book
        // BVA: zero stored students
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandFailure(new DeleteCommand(new StudentId("Z9999999Z")), emptyModel,
                "Student with ID Z9999999Z is not in the records.");
    }

    @Test
    public void equals() {
        DeleteCommand deleteAlice = new DeleteCommand(ALICE.getStudentId());
        // EP: same object
        assertTrue(deleteAlice.equals(deleteAlice));
        // EP: same normalised Student ID
        assertTrue(deleteAlice.equals(new DeleteCommand(new StudentId("a1234567b"))));
        // EP: different Student ID
        assertFalse(deleteAlice.equals(new DeleteCommand(BENSON.getStudentId())));
        // EP: null
        assertFalse(deleteAlice.equals(null));
        // EP: different type
        assertFalse(deleteAlice.equals(1));
    }

    @Test
    public void toStringMethod() {
        // EP: valid object representation
        DeleteCommand command = new DeleteCommand(ALICE.getStudentId());
        assertEquals(DeleteCommand.class.getCanonicalName() + "{targetStudentId=A1234567B}", command.toString());
    }
}
