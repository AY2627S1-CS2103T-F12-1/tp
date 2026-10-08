package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private StorageManager storage;

    @Test
    public void execute_deleteHiddenStudent_preservesFilterAndPersists() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        logic.execute("find n/Benson");
        CommandResult result = logic.execute("delete i/a1234567b");
        assertEquals("Deleted student A1234567B: Alice Pauline.", result.getFeedbackToUser());
        assertEquals(List.of(BENSON), model.getAddressBook().getPersonList());
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_delete_removesEntireStudentRecordFromSavedData() throws Exception {
        Person student = new PersonBuilder().withStudentId("A0123456B").withName("Alex Tan")
                .withEmail("alex@example.com").withRemark("Weak in recursion.").withTags("tutorial").build();
        model.addPerson(student);
        storage.saveAddressBook(model.getAddressBook());
        assertEquals("Deleted student A0123456B: Alex Tan.",
                logic.execute("delete i/A0123456B").getFeedbackToUser());
        assertEquals(List.of(), model.getAddressBook().getPersonList());
        assertEquals(List.of(), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_deleteInvalidOrUnknownId_preservesRecordsAndSavedFile() throws Exception {
        model.addPerson(ALICE);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alice")));
        storage.saveAddressBook(model.getAddressBook());
        String originalFile = Files.readString(storage.getAddressBookFilePath());
        assertParseException("delete i/", "Parameter i/ cannot be empty.");
        assertParseException("delete i/A1234567B x/value", "Unknown parameter: x/.");
        assertCommandException("delete i/Z9999999Z", "Student with ID Z9999999Z is not in the records.");
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(originalFile, Files.readString(storage.getAddressBookFilePath()));
    }

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_add_successSavesBeforePublishing() throws Exception {
        CommandResult result = logic.execute("add n/Samuel i/a0123456b e/Sam@EXAMPLE.COM r/Quiz: 8/10.");
        assertEquals("Added student A0123456B: Samuel. Remark: Quiz: 8/10.\n1 students listed.",
                result.getFeedbackToUser());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals("Sam@example.com", model.getAddressBook().getPersonList().getFirst().getEmail().value);
    }

    @Test
    public void execute_label_successPersistsLabel() throws Exception {
        model.addPerson(ALICE);
        logic.execute("label l/Tutorial 1 i/" + ALICE.getStudentId());

        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_readOnlyCommands_doNotSave() throws Exception {
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(temporaryFolder.resolve("unused.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Read-only commands must not save");
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute("list");
        logic.execute("find n/ Samuel");
        logic.execute("help");
        logic.execute("exit");
    }

    @Test
    public void execute_loadingFailed_blocksModificationsAndPreservesCorruptFile() throws Exception {
        Files.writeString(storage.getAddressBookFilePath(), "invalid student data");
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        logic = new LogicManager(model, storage, false);
        for (String command : List.of("add n/Samuel i/A0123456B e/sam@example.com", "clear", "delete i/B1234567C",
                "edit 1 n/Samuel", "label l/Tutorial 1 i/A0123456B")) {
            assertCommandException(command, LogicManager.MESSAGE_DATA_UNAVAILABLE);
        }
        logic.execute("list");
        assertEquals("invalid student data", Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_replacementFails_preservesRecordsFileAndActiveFilter() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Benson")));
        storage.saveAddressBook(model.getAddressBook());
        String previousFile = Files.readString(storage.getAddressBookFilePath());
        AddressBook previousRecords = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(storage.getAddressBookFilePath()) {
            @Override
            protected void replaceDataFile(Path temporaryFile, Path targetFile) throws IOException {
                assertEquals(previousRecords, model.getAddressBook());
                throw new IOException("Simulated replacement failure");
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        for (String command : List.of("add n/Samuel i/A0123456B e/sam@example.com", "delete i/B1234567C", "clear",
                "edit 1 n/Updated Name")) {
            assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> logic.execute(command));
            assertEquals(previousRecords, model.getAddressBook());
            assertEquals(List.of(BENSON), model.getFilteredPersonList());
            assertEquals(previousFile, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_deleteAfterFind_usesStudentIdAndPersists() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        logic.execute("find n/Benson");
        logic.execute("delete i/B1234567C");
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("list");
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete i/Z9999999Z";
        assertCommandException(deleteCommand,
                "Student with ID Z9999999Z is not in the records.");
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_labelCommand_success() throws Exception {
        Person samuel = new PersonBuilder().withName("Samuel Tan")
                .withStudentId("A0101010A")
                .withEmail("samuel@example.com")
                .build();
        model.addPerson(samuel);
        Person labelledSamuel = new PersonBuilder(samuel).withLabels("Discrete Math Tutorial").build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(samuel, labelledSamuel);

        assertCommandSuccess("label l/Discrete Math Tutorial i/A0101010A",
                "Added label \"Discrete Math Tutorial\" to Samuel Tan (A0101010A).", expectedModel);
    }

    @Test
    public void execute_labelCommand_persistsLabel() throws Exception {
        model.addPerson(new PersonBuilder().withName("Samuel Tan")
                .withStudentId("A0101010A")
                .withEmail("samuel@example.com")
                .build());
        logic.execute("label l/Discrete Math Tutorial i/A0101010A");
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_labelCommandDuplicateLabel_throwsCommandException() {
        Person samuel = new PersonBuilder().withName("Samuel Tan")
                .withStudentId("A0101010A")
                .withEmail("samuel@example.com")
                .withLabels("Discrete Math Tutorial")
                .build();
        model.addPerson(samuel);

        assertCommandException("label l/discrete math tutorial i/A0101010A",
                "Student A0101010A already has label \"Discrete Math Tutorial\".");
    }

    @Test
    public void execute_labelCommandNonExistentStudent_throwsCommandException() {
        assertCommandException("label l/Discrete Math Tutorial i/A0101010A",
                "Student with ID A0101010A is not in the records. Consider using add to include the student.");
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + STUDENT_ID_DESC_AMY
                + EMAIL_DESC_AMY;
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
