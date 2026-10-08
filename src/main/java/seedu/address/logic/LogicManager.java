package seedu.address.logic;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_SAVE_FAILURE = "Unable to save student data.";
    public static final String MESSAGE_DATA_UNAVAILABLE =
            "Student data is unavailable because loading failed. Fix the data file and restart.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private final boolean isStudentDataAvailable;
    private final String startupMessage;

    /**
     * Constructs a {@code LogicManager} whose student data is available for modification.
     *
     * @param model The live model.
     * @param storage The storage used for saving changes.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, true, "");
    }

    /**
     * Constructs a {@code LogicManager}, blocking modifications if startup loading failed.
     *
     * @param model The live model.
     * @param storage The storage used for saving changes.
     * @param isStudentDataAvailable Whether startup loading succeeded or no data file existed.
     * @param startupMessage The message describing the outcome of startup loading.
     */
    public LogicManager(Model model, Storage storage, boolean isStudentDataAvailable, String startupMessage) {
        this.model = model;
        this.storage = storage;
        this.isStudentDataAvailable = isStudentDataAvailable;
        this.startupMessage = startupMessage;
        addressBookParser = new AddressBookParser();
    }

    /**
     * {@inheritDoc}
     * Executes modifying commands against a temporary model and publishes their changes only after saving succeeds.
     * Commands that do not modify student data execute without saving. Startup loading failures block modifications.
     */
    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        if (!command.isModifyingData()) {
            return command.execute(model);
        }
        if (!isStudentDataAvailable) {
            throw new CommandException(MESSAGE_DATA_UNAVAILABLE);
        }
        return executeAndSave(command);
    }

    private CommandResult executeAndSave(Command command) throws CommandException {
        Model pendingModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        pendingModel.updateFilteredPersonList(model.getFilteredPersonListPredicate()::test);
        CommandResult result = command.execute(pendingModel);
        saveStudentData(pendingModel);
        model.setAddressBook(pendingModel.getAddressBook());
        model.updateFilteredPersonList(pendingModel.getFilteredPersonListPredicate()::test);
        return result;
    }

    private void saveStudentData(Model pendingModel) throws CommandException {
        try {
            storage.saveAddressBook(pendingModel.getAddressBook());
        } catch (IOException ioe) {
            logger.warning("Unable to save student data: " + ioe.getMessage());
            throw new CommandException(MESSAGE_SAVE_FAILURE, ioe);
        }
    }

    @Override
    public String getStartupMessage() {
        return startupMessage;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
