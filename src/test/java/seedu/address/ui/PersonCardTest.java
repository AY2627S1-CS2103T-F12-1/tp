package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks student card layout. Skipped when JavaFX cannot start, such as on a machine without a display.
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
    public void longGroupLabel_narrowAndResizedList_wrapsWithoutClippingDetails() throws Exception {
        assumeTrue(isToolkitAvailable);
        CompletableFuture<Void> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                var person = new PersonBuilder().withName("Long Label Student")
                        .withStudentId("A0123460F")
                        .withEmail("a".repeat(64) + "@example.com")
                        .withRemark("Short remark")
                        .withLabels("Tutorial", "W".repeat(60)).build();
                Region root = new PersonListPanel(FXCollections.observableArrayList(person)).getRoot();
                Scene scene = new Scene(root, 400, 400);
                scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());

                for (double width : new double[] {400, 800, 340, 400}) {
                    root.resize(width, 400);
                    root.applyCss();
                    for (int pass = 0; pass < 3; pass++) {
                        root.layout();
                    }

                    Region card = (Region) root.lookup("#cardPane");
                    Bounds cardBounds = card.localToScene(card.getLayoutBounds());
                    assertTrue(cardBounds.getMaxX() <= width + 1, "Card exceeds the list width");
                    for (Node node : card.lookupAll(".label")) {
                        Label label = (Label) node;
                        Bounds bounds = label.localToScene(label.getLayoutBounds());
                        assertTrue(bounds.getMaxX() <= cardBounds.getMaxX() + 1,
                                "Label exceeds the card width: " + label.getText());
                        assertTrue(bounds.getMaxY() <= cardBounds.getMaxY() + 1,
                                "Label exceeds the card height: " + label.getText());
                        Text text = (Text) label.lookup(".text");
                        assertEquals(label.getText(), text.getText(), "Label text is truncated");
                        assertTrue(label.getHeight() + 1 >= label.prefHeight(label.getWidth()),
                                "Wrapped label has insufficient height");
                    }
                    Label group = (Label) card.lookupAll("#labels .label").stream()
                            .filter(node -> ((Label) node).getText().startsWith("W")).findFirst().orElseThrow();
                    Label email = (Label) card.lookup("#email");
                    if (width <= 400) {
                        assertTrue(group.getHeight() > 2 * group.getFont().getSize(), "Group should wrap");
                        assertTrue(email.getHeight() > 2 * email.getFont().getSize(), "Email should wrap");
                    }
                    root.lookupAll(".scroll-bar").stream().map(ScrollBar.class::cast)
                            .filter(bar -> bar.getOrientation() == javafx.geometry.Orientation.HORIZONTAL)
                            .forEach(bar -> assertTrue(!bar.isVisible(), "List needs horizontal scrolling"));
                }
                result.complete(null);
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        result.get(10, TimeUnit.SECONDS);
    }
}
