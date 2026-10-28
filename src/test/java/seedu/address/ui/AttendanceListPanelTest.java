package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.attendance.AttendanceReport.AttendanceEntry;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks the attendance view's content. Skipped where JavaFX cannot start, such as on a machine without a display;
 * CI runs it on Linux under a virtual display.
 */
public class AttendanceListPanelTest {

    private static final LocalDate DATE = LocalDate.parse("2026-10-20");
    private static final seedu.address.model.label.Label GROUP = new seedu.address.model.label.Label("Tutorial 1");

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
    public void constructor_report_showsTitleTotalsAndMembers() throws Exception {
        // EP: report with both present and absent members renders title, totals, rows, and both badge styles
        assumeTrue(isToolkitAvailable);
        Person present = new PersonBuilder(ALICE).withLabels("Tutorial 1")
                .withAttendance(new AttendanceRecord(DATE, GROUP)).build();
        Person absent = new PersonBuilder(BENSON).withLabels("Tutorial 1").build();
        AttendanceReport report = AttendanceReport.of(List.of(present, absent), DATE, GROUP);

        Parent root = onFxThread(() -> layOut(new AttendanceListPanel(report).getRoot()));

        assertEquals("Attendance: Tutorial 1 on 2026-10-20", ((Label) root.lookup("#title")).getText());
        assertEquals("Present: 1    Absent: 1", ((Label) root.lookup("#totals")).getText());
        assertEquals(report.getEntries(), ((ListView<?>) root.lookup("#attendanceListView")).getItems());
        assertEquals(1, root.lookupAll(".attendance_present").size());
        assertEquals(1, root.lookupAll(".attendance_absent").size());
    }

    @Test
    public void constructor_emptyReport_showsZeroTotals() throws Exception {
        // BVA: zero present and zero absent members render zero totals and no status badge
        assumeTrue(isToolkitAvailable);
        AttendanceReport report = AttendanceReport.of(List.of(), DATE, GROUP);

        Parent root = onFxThread(() -> layOut(new AttendanceListPanel(report).getRoot()));

        assertEquals("Present: 0    Absent: 0", ((Label) root.lookup("#totals")).getText());
        assertTrue(((ListView<?>) root.lookup("#attendanceListView")).getItems().isEmpty());
        assertTrue(root.lookupAll("#status").isEmpty());
    }

    @Test
    public void attendanceCard_entry_showsStudentAndStatusBadge() throws Exception {
        // EP: present and absent entry cards render their student details and distinct status badges
        assumeTrue(isToolkitAvailable);
        Node presentCard = onFxThread(() -> new AttendanceCard(new AttendanceEntry(ALICE, true), 1).getRoot());
        Node absentCard = onFxThread(() -> new AttendanceCard(new AttendanceEntry(BENSON, false), 2).getRoot());

        assertEquals("1. ", ((Label) presentCard.lookup("#id")).getText());
        assertEquals("Alice Pauline", ((Label) presentCard.lookup("#name")).getText());
        assertEquals("Student ID: A1234567B", ((Label) presentCard.lookup("#studentId")).getText());
        Label presentStatus = (Label) presentCard.lookup("#status");
        assertEquals("Present", presentStatus.getText());
        assertTrue(presentStatus.getStyleClass().contains("attendance_present"));
        assertFalse(presentStatus.getStyleClass().contains("attendance_absent"));

        Label absentStatus = (Label) absentCard.lookup("#status");
        assertEquals("Absent", absentStatus.getText());
        assertTrue(absentStatus.getStyleClass().contains("attendance_absent"));
    }

    @Test
    public void attendanceCard_longName_wrapsAndKeepsStatusVisible() throws Exception {
        assumeTrue(isToolkitAvailable);
        String longName = "Alexandra ".repeat(10).trim() + "A";
        Person student = new PersonBuilder(ALICE).withName(longName).withLabels("Tutorial 1").build();

        onFxThread(() -> {
            // EP: an absent student with a maximum-length name at the minimum list width
            // BVA: 100-character name and 320-pixel list width within the minimum 340-pixel list pane
            double narrowNameHeight = assertBadgeVisible(student, 320);

            // EP: the same student at a wide list width
            double wideNameHeight = assertBadgeVisible(student, 1200);
            assertTrue(narrowNameHeight > wideNameHeight);
            return null;
        });
    }

    @Test
    public void constructor_longGroupHeading_remainsReadableInSmallAndResizedWindows() throws Exception {
        assumeTrue(isToolkitAvailable);
        onFxThread(() -> {
            var group = new seedu.address.model.label.Label("W".repeat(60));
            Person member = new PersonBuilder(ALICE).withLabels(group.labelName).build();
            for (List<Person> members : List.of(List.of(member), List.<Person>of())) {
                AttendanceReport report = AttendanceReport.of(members, DATE, group);
                Region root = new AttendanceListPanel(report).getRoot();
                Scene scene = new Scene(root, 400, 180);
                scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
                root.applyCss();
                Region header = (Region) root.lookup(".attendance_header");
                // Leave less space than the empty-list placeholder's natural height, on every platform.
                double compactHeight = header.prefHeight(340) + 10;
                double[][] sizes = {{400, 180}, {800, 400}, {340, 160}, {340, compactHeight}, {400, 180}};
                for (double[] size : sizes) {
                    root.resize(size[0], size[1]);
                    root.applyCss();
                    for (int pass = 0; pass < 3; pass++) {
                        root.layout();
                    }

                    Label title = (Label) root.lookup("#title");
                    Label totals = (Label) root.lookup("#totals");
                    ListView<?> list = (ListView<?>) root.lookup("#attendanceListView");
                    assertEquals("Attendance: " + group.labelName + " on " + DATE, title.getText());
                    assertEquals(title.getText(), ((Text) title.lookup(".text")).getText(),
                            "Heading must display the full group and date without an ellipsis");
                    assertTrue(title.getHeight() + 1 >= title.prefHeight(title.getWidth()),
                            "Heading must have enough height for every wrapped line");
                    if (size[0] <= 400) {
                        assertTrue(title.getHeight() > 2 * title.getFont().getSize(), "Heading should wrap");
                    }
                    Bounds titleBounds = title.localToScene(title.getLayoutBounds());
                    Bounds totalsBounds = totals.localToScene(totals.getLayoutBounds());
                    Bounds listBounds = list.localToScene(list.getLayoutBounds());
                    assertTrue(titleBounds.getMaxX() <= size[0], "Heading must fit within the window width");
                    assertTrue(titleBounds.getMaxY() <= totalsBounds.getMinY(), "Heading must not overlap totals");
                    assertTrue(totalsBounds.getMaxY() <= listBounds.getMinY(), "Totals must not overlap the list");
                    assertEquals(totals.getText(), ((Text) totals.lookup(".text")).getText());
                    assertTrue(list.getHeight() > 0, "The student list must retain space below the heading");
                    assertTrue(listBounds.getMaxY() <= size[1] + 1,
                            "The list must fit within the window height at " + size[0] + " x " + size[1]
                                    + "; bottom was " + listBounds.getMaxY());
                }
            }
            return null;
        });
    }

    private static double assertBadgeVisible(Person student, double width) {
        AttendanceReport report = AttendanceReport.of(List.of(student), DATE, GROUP);
        Parent root = layOut(new AttendanceListPanel(report).getRoot(), width);
        ListView<?> list = (ListView<?>) root.lookup("#attendanceListView");
        Label status = (Label) root.lookup("#status");
        Label name = (Label) root.lookup("#name");
        Bounds listBounds = list.localToScene(list.getBoundsInLocal());
        Bounds statusBounds = status.localToScene(status.getBoundsInLocal());

        assertTrue(statusBounds.getMaxX() <= listBounds.getMaxX());
        return name.getHeight();
    }

    /**
     * Places {@code root} in a scene and lays it out, so that its list creates cells for the visible rows.
     */
    private static Parent layOut(Parent root) {
        return layOut(root, 400);
    }

    private static Parent layOut(Parent root, double width) {
        new Scene(root, width, 400);
        root.applyCss();
        root.layout();
        return root;
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
}
