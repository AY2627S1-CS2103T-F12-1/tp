package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void saveAddressBook_replacementFailure_preservesFileAndRemovesTemporaryFile() throws Exception {
        Path file = testFolder.resolve("existing.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        AddressBook original = getTypicalAddressBook();
        storage.saveAddressBook(original);
        String originalText = Files.readString(file);
        JsonAddressBookStorage failingStorage = replacementFailingStorage(file);
        assertThrows(IOException.class, () -> failingStorage.saveAddressBook(new AddressBook()));
        assertEquals(originalText, Files.readString(file));
        assertEquals(original, storage.readAddressBook().orElseThrow());
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void saveAddressBook_firstSaveFails_doesNotCreateEmptyDataFile() throws Exception {
        Path file = testFolder.resolve("new.json");
        assertThrows(IOException.class, () -> replacementFailingStorage(file).saveAddressBook(new AddressBook()));
        assertFalse(Files.exists(file));
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void saveAddressBook_folderCannotBeCreated_throwsDataStorageLocationException() throws Exception {
        Path blockingFile = Files.writeString(testFolder.resolve("data"), "not a folder");
        Path file = blockingFile.resolve("addressBook.json");
        assertThrows(DataStorageLocationException.class, () ->
                new JsonAddressBookStorage(file).saveAddressBook(getTypicalAddressBook()));
        assertEquals("not a folder", Files.readString(blockingFile));
    }

    @Test
    public void saveAddressBook_missingParentDirectories_createsThem() throws Exception {
        Path file = testFolder.resolve("nested/data/students.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(getTypicalAddressBook());
        assertTrue(Files.isRegularFile(file));
        assertEquals(getTypicalAddressBook(), storage.readAddressBook().orElseThrow());
    }

    private JsonAddressBookStorage replacementFailingStorage(Path file) {
        return new JsonAddressBookStorage(file) {
            @Override
            protected void replaceDataFile(Path temporaryFile, Path targetFile) throws IOException {
                assertTrue(Files.size(temporaryFile) > 0);
                throw new IOException("Simulated replacement failure");
            }
        };
    }

    @Test
    public void readAndSaveAddressBook_paddedFields_savesTrimmedValues() throws Exception {
        // EP: Manually padded JSON fields represent valid data that should load and save as trimmed model values.
        Path file = testFolder.resolve("padded.json");
        String originalContents = """
                {"persons":[{
                  "name":"  Alice   Pauline  ",
                  "studentId":" a1234567b ",
                  "email":" Alice@EXAMPLE.COM ",
                  "remark":"  Follow up  soon\\nNext week  ",
                  "labels":[" Tutorial  1 "],
                  "attendance":[{"date":" 2026-10-20 ","group":" Tutorial  1 "}]
                }]}
                """;
        Files.writeString(file, originalContents);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        AddressBook expected = new AddressBook();
        expected.addPerson(new PersonBuilder(ALICE).withName("Alice Pauline").withStudentId("A1234567B")
                .withEmail("Alice@example.com").withRemark("Follow up  soon\nNext week").withLabels("Tutorial  1")
                .withAttendance(new AttendanceRecord(LocalDate.parse("2026-10-20"), new Label("Tutorial  1")))
                .build());

        assertEquals(expected, restored);
        assertEquals(originalContents, Files.readString(file));
        storage.saveAddressBook(restored);
        assertEquals(JsonUtil.toJsonString(new JsonSerializableAddressBook(expected)), Files.readString(file));
        assertEquals(expected, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void readAndSaveAddressBook_withRemark_preservesRemark() throws Exception {
        Path filePath = testFolder.resolve("RemarksAddressBook.json");
        Person person = new PersonBuilder(ALICE).withRemark("Needs help with recursion\nFollow up next week").build();
        AddressBook original = new AddressBook();
        original.addPerson(person);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook restored = new JsonAddressBookStorage(filePath).readAddressBook().get();

        assertEquals(original, new AddressBook(restored));
        assertEquals(person.getRemark(), restored.getPersonList().get(0).getRemark());
    }

    @Test
    public void readAndSaveAddressBook_withLabel_preservesLabel() throws Exception {
        Path filePath = testFolder.resolve("LabelsAddressBook.json");
        Person person = new PersonBuilder(ALICE).withLabels("Discrete Math Tutorial").build();
        AddressBook original = new AddressBook();
        original.addPerson(person);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook restored = new JsonAddressBookStorage(filePath).readAddressBook().get();

        assertEquals(original, new AddressBook(restored));
        assertEquals(person.getLabels(), restored.getPersonList().get(0).getLabels());
    }

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_INVALID_FILE, () ->
                readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_INVALID_FILE, () ->
                readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_INVALID_FILE, () ->
                readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_duplicateStudentId_throwsDuplicateStudentIdMessage() {
        Path file = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest",
                "duplicatePersonAddressBook.json");
        assertThrows(DataLoadingException.class,
                String.format(JsonSerializableAddressBook.MESSAGE_DUPLICATE_STUDENT_ID, "A1234567B"), () ->
                new JsonAddressBookStorage(file).readAddressBook());
    }

    @Test
    public void readAddressBook_nullJson_throwsInvalidFileMessageAndPreservesFile() throws Exception {
        Path file = testFolder.resolve("nullAddressBook.json");
        String originalContents = "  null\n";
        Files.writeString(file, originalContents);

        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_INVALID_FILE, ()
            -> new JsonAddressBookStorage(file).readAddressBook());
        assertEquals(originalContents, Files.readString(file));
    }

    @Test
    public void readAddressBook_wrongJsonStructure_throwsInvalidFileMessage() throws Exception {
        Path file = testFolder.resolve("wrongStructure.json");
        Files.writeString(file, "{ \"persons\": \"not a list\" }");
        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_INVALID_FILE, () ->
                new JsonAddressBookStorage(file).readAddressBook());
    }

    @Test
    public void readAddressBook_unreadableFile_throwsUnreadableFileMessage() throws Exception {
        Path directory = Files.createDirectory(testFolder.resolve("addressBook.json"));
        assertThrows(DataLoadingException.class, JsonAddressBookStorage.MESSAGE_UNREADABLE_FILE, () ->
                new JsonAddressBookStorage(directory).readAddressBook());
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
