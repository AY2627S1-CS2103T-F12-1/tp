package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

/**
 * Checks how startup failures are reported. Skipped where JavaFX cannot start, such as on a machine without a display;
 * CI runs it on Linux under a virtual display.
 */
public class UiManagerTest {

    private static final String FAILURE_MESSAGE = "GUI settings unavailable";

    private static boolean isToolkitAvailable;

    @BeforeAll
    public static void startToolkit() {
        try {
            Platform.startup(() -> { });
            isToolkitAvailable = true;
        } catch (IllegalStateException e) {
            isToolkitAvailable = true; // already started by another test
        } catch (RuntimeException | UnsatisfiedLinkError e) {
            isToolkitAvailable = false;
        }
    }

    @Test
    public void start_mainWindowCreationFails_showsErrorDialogOnPrimaryStage() throws Exception {
        // EP: failure before the main window exists is reported on the primary stage, not a null window (#210)
        assumeTrue(isToolkitAvailable);
        RecordingUiManager uiManager = new RecordingUiManager();

        Stage primaryStage = onFxThread(() -> {
            Stage stage = new Stage();
            uiManager.start(stage);
            return stage;
        });

        assertSame(primaryStage, uiManager.dialogOwner);
        assertEquals(AlertType.ERROR, uiManager.dialogType);
        assertEquals(FAILURE_MESSAGE, uiManager.dialogHeaderText);
        assertTrue(uiManager.hasExited);
    }

    private static <T> T onFxThread(Supplier<T> supplier) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(supplier.get());
            } catch (RuntimeException | AssertionError e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(10, TimeUnit.SECONDS);
    }

    /**
     * Records the fatal-error dialog and exit instead of showing the dialog and ending the test run.
     */
    private static class RecordingUiManager extends UiManager {
        private Stage dialogOwner;
        private AlertType dialogType;
        private String dialogHeaderText;
        private boolean hasExited;

        RecordingUiManager() {
            super(new FailingLogic(), Path.of("data", "addressbook.json"));
        }

        @Override
        void showAlertDialogAndWait(Stage owner, AlertType type, String title, String headerText,
                String contentText) {
            dialogOwner = owner;
            dialogType = type;
            dialogHeaderText = headerText;
        }

        @Override
        void exitApplication() {
            hasExited = true;
        }
    }

    /**
     * A logic component whose GUI settings cannot be read, so the main window fails during construction.
     */
    private static class FailingLogic implements Logic {
        @Override
        public CommandResult execute(String commandText) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getStartupMessage() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new IllegalStateException(FAILURE_MESSAGE);
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }
    }
}
