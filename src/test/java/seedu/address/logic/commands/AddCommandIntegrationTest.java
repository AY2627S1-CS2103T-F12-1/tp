package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        // EP: new person
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format("Added student %s: %s.\n8 students listed.",
                        validPerson.getStudentId(), validPerson.getName()),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        // EP: duplicate person
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, personInList.getStudentId()));
    }

    @Test
    public void execute_remarkPresent_displaysRemarkAndTotalCount() {
        // EP: remark present
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alice")));
        Person student = new PersonBuilder().withName("Alex Tan").withStudentId("a0123456b")
                .withRemark("Quiz 1: 8/10.  Weak in recursion.").build();
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.addPerson(student);
        assertCommandSuccess(new AddCommand(student), model,
                "Added student A0123456B: Alex Tan. Remark: Quiz 1: 8/10.  Weak in recursion.\n8 students listed.",
                expected);
    }

    @Test
    public void execute_sameIdDifferentDetails_rejectsWithoutMerging() {
        // EP: same id different details
        Person existing = model.getAddressBook().getPersonList().get(0);
        Person duplicate = new PersonBuilder(existing).withStudentId(existing.getStudentId().value.toLowerCase())
                .withName("Another Student").withEmail("another@example.com").withRemark("New remark").build();
        assertCommandFailure(new AddCommand(duplicate), model,
                String.format("Student ID %s already exists.", existing.getStudentId()));
    }

}
