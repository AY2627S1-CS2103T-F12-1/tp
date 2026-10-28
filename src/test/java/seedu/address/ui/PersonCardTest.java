package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks that student cards distinguish group labels from Student ID badges.
 */
public class PersonCardTest {

    private static boolean isToolkitAvailable;

    @BeforeAll
    public static void startToolkit() {
        try {
            Platform.startup(() -> { });
            isToolkitAvailable = true;
        } catch (IllegalStateException e) {
            isToolkitAvailable = true;
        } catch (RuntimeException | UnsatisfiedLinkError e) {
            isToolkitAvailable = false;
        }
    }

    @Test
    public void constructor_studentWithGroupLabel_displaysDistinctBadges() throws Exception {
        assumeTrue(isToolkitAvailable);
        runOnFxThread(() -> {
            // EP: a student with a group label has both badge types
            Parent root = new PersonCard(new PersonBuilder(ALICE).withLabels("Tutorial 1").build(), 1).getRoot();
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(PersonCardTest.class.getResource("/view/DarkTheme.css").toExternalForm());
            root.applyCss();

            Label groupBadge = (Label) ((FlowPane) root.lookup("#labels")).getChildren().get(0);
            Label studentIdBadge = (Label) ((FlowPane) root.lookup("#studentIds")).getChildren().get(0);
            assertEquals("Tutorial 1", groupBadge.getText());
            assertEquals("Student ID: A1234567B", studentIdBadge.getText());
            assertTrue(groupBadge.getStyleClass().contains("group_label_badge"));
            assertTrue(studentIdBadge.getStyleClass().contains("student_id_badge"));
            assertNotNull(groupBadge.getBackground());
            assertNotNull(studentIdBadge.getBorder());
            assertNotEquals(groupBadge.getBackground(), studentIdBadge.getBackground());
        });
    }

    @Test
    public void constructor_studentWithoutGroupLabel_displaysStudentIdOnly() throws Exception {
        assumeTrue(isToolkitAvailable);
        runOnFxThread(() -> {
            // EP: a student without group labels still has an identifiable Student ID badge
            Parent root = new PersonCard(new PersonBuilder(ALICE).withLabels().build(), 1).getRoot();
            FlowPane labels = (FlowPane) root.lookup("#labels");
            FlowPane studentIds = (FlowPane) root.lookup("#studentIds");
            assertTrue(labels.getChildren().isEmpty());
            assertEquals(1, studentIds.getChildren().size());
            assertTrue(studentIds.getChildren().get(0).getStyleClass().contains("student_id_badge"));
        });
    }

    private static void runOnFxThread(Runnable verification) throws Exception {
        CompletableFuture<Void> completed = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                verification.run();
                completed.complete(null);
            } catch (Throwable failure) {
                completed.completeExceptionally(failure);
            }
        });
        completed.get(10, TimeUnit.SECONDS);
    }
}
