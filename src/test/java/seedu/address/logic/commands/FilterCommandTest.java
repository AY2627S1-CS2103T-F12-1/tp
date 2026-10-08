package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {

    private static final String VALID_LABEL = "Discrete Math Tutorial";
    private static final String DIFFERENT_CASE_LABEL = "discrete math tutorial";
    private static final String OTHER_LABEL = "CS2103T Tutorial";

    private final Person samuel = new PersonBuilder().withName("Samuel Tan")
            .withStudentId("A0101010A")
            .withEmail("samuel@example.com")
            .withLabels(VALID_LABEL)
            .build();
    private final Person alex = new PersonBuilder().withName("Alex Yeoh")
            .withStudentId("A0101011A")
            .withEmail("alex@example.com")
            .withLabels(DIFFERENT_CASE_LABEL)
            .build();
    private final Person bernice = new PersonBuilder().withName("Bernice Yu")
            .withStudentId("A0101012A")
            .withEmail("bernice@example.com")
            .withLabels(OTHER_LABEL)
            .build();
    private final Person charlotte = new PersonBuilder().withName("Charlotte Oliveiro")
            .withStudentId("A0101013A")
            .withEmail("charlotte@example.com")
            .build();

    @Test
    public void constructor_nullLabel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
    }

    @Test
    public void execute_matchingLabel_showsMatchingStudents() {
        Model model = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        Model expectedModel = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(samuel));

        FilterCommand filterCommand = new FilterCommand(new Label(VALID_LABEL));
        String expectedMessage = "1 students found with label \"Discrete Math Tutorial\".";

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(samuel), model.getFilteredPersonList());
    }

    @Test
    public void execute_queryLabelDifferentCase_showsMatchingStudents() {
        Model model = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        Model expectedModel = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(samuel));

        FilterCommand filterCommand = new FilterCommand(new Label(DIFFERENT_CASE_LABEL));
        String expectedMessage = "1 students found with label \"discrete math tutorial\".";

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(samuel), model.getFilteredPersonList());
    }

    @Test
    public void execute_storedLabelDifferentCase_showsMatchingStudents() {
        Model model = new ModelManager(getAddressBookWith(alex, bernice, charlotte), new UserPrefs());
        Model expectedModel = new ModelManager(getAddressBookWith(alex, bernice, charlotte), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(alex));

        FilterCommand filterCommand = new FilterCommand(new Label(VALID_LABEL));
        String expectedMessage = "1 students found with label \"Discrete Math Tutorial\".";

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(alex), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchingLabel_clearsDisplayedList() {
        Model model = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        Model expectedModel = new ModelManager(getAddressBookWith(samuel, bernice, charlotte), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);

        FilterCommand filterCommand = new FilterCommand(new Label("NonExistent"));
        String expectedMessage = "No students found with label \"NonExistent\".";

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    private static AddressBook getAddressBookWith(Person... persons) {
        AddressBook addressBook = new AddressBook();
        for (Person person : persons) {
            addressBook.addPerson(person);
        }
        return addressBook;
    }
}
