package seedu.address.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.attendance.AttendanceReport.AttendanceEntry;

/**
 * Panel showing a group's attendance on a date, shown in place of the student list.
 */
public class AttendanceListPanel extends UiPart<Region> {
    private static final String FXML = "AttendanceListPanel.fxml";

    @FXML
    private Label title;
    @FXML
    private Label totals;
    @FXML
    private ListView<AttendanceEntry> attendanceListView;

    /**
     * Creates an {@code AttendanceListPanel} showing {@code report}.
     */
    public AttendanceListPanel(AttendanceReport report) {
        super(FXML);
        title.setText("Attendance: " + report.getGroup().labelName + " on " + report.getDate());
        totals.setText("Present: " + report.getPresentCount() + "    Absent: " + report.getAbsentCount());
        attendanceListView.setItems(FXCollections.observableList(report.getEntries()));
        attendanceListView.setCellFactory(listView -> new AttendanceListViewCell());
        attendanceListView.setPlaceholder(new Label("No students currently have this group label."));
    }

    /**
     * Custom {@code ListCell} that displays an {@code AttendanceEntry} using an {@code AttendanceCard}.
     */
    class AttendanceListViewCell extends ListCell<AttendanceEntry> {
        @Override
        protected void updateItem(AttendanceEntry entry, boolean empty) {
            super.updateItem(entry, empty);

            if (empty || entry == null) {
                setGraphic(null);
                setText(null);
            } else {
                Region card = new AttendanceCard(entry, getIndex() + 1).getRoot();
                card.prefWidthProperty().bind(widthProperty());
                setGraphic(card);
            }
        }
    }
}
