package revisionprogram.scheduledrevision;

import revisionprogram.CreateDialog;
import revisionprogram.Main;
import revisionprogram.ScrollingPanel;
import revisionprogram.timetable.Timetable;
import revisionprogram.timetable.TimetableActivity;
import revisionprogram.timetable.TimetablePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

public class CreateTimetableDocumentPanel extends JPanel {
    private JTable toCreateTable;
    private DefaultTableModel tableModel;
    private final TimetablePanel timetablePanel;
    public static final String[] headings = new String[] {Main.strings.getString("dayCreateNameHeading"), Main.strings.getString("dayCreateSubjectHeading"), Main.strings.getString("dayCreateDateHeading")};
    public static final int[] widths = {200, 100, 130};
    public static final int numRows = 6;
    public CreateTimetableDocumentPanel(TimetablePanel timetablePanel) {
        super();
        this.timetablePanel = timetablePanel;
        Timetable timetable = timetablePanel.makeTimetable();
        timetablePanel.addChangeListener(this::update);

        // TODO: ADD PREVIOUS UNDONE DAYS
        TimetableActivity[] timetableActivities = timetable.getDayActivities(timetable.getIndexOnDay(LocalDate.now()));
        String[] activityNames = timetable.configuredActivities;

        tableModel = new DefaultTableModel(makeDataFromActivities(timetableActivities, activityNames), headings);
        toCreateTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing on table
                return false;
            }
        };
        toCreateTable.getTableHeader().setReorderingAllowed(false);
        // Limit table height
        toCreateTable.setPreferredScrollableViewportSize(new Dimension(getTableWidth(), toCreateTable.getRowHeight()*numRows));
        toCreateTable.setFont(Main.textContentFont);

        // Add an on-click listener
        toCreateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = toCreateTable.getSelectedRow();
                    int index = timetableActivities[row].activityIndex();
                    System.out.println(index);
                    String subject;
                    if (index < activityNames.length) {
                        subject = activityNames[index];
                    } else {
                        subject = Main.strings.getString("timetableNoActivitySelected");
                    }

                    CreateDialog createDialog = new CreateDialog(subject);
                    createDialog.setVisible(true);
                }
            }
        });

        resize();
        ScrollingPanel tableScrollPanel = new ScrollingPanel(toCreateTable);
        this.add(tableScrollPanel);

    }
    public void update() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        Timetable timetable = timetablePanel.makeTimetable();
        Object[][] data = makeDataFromActivities(timetable.getDayActivities(timetable.getCurrentDay()), timetable.configuredActivities);
        for (Object[] row: data) {
            tableModel.addRow(row);
        }
    }
    private Object[] makeRow(TimetableActivity data, String[] activityNames) {
        Object[] row = new Object[headings.length];
        row[0] = data.name();
        int nameIndex = data.activityIndex();
        if (nameIndex < activityNames.length) {
            row[1] = activityNames[nameIndex];
        } else {
            row[1] = Main.strings.getString("timetableNoActivitySelected");
        }
        row[2] = Main.getUserStyleDateString(LocalDate.now());
        return row;

    }
    private Object[][] makeDataFromActivities(TimetableActivity[] activities, String[] activityNames) {
        Object[][] data = new Object[activities.length][];
        for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
            data[activityIndex] = makeRow(activities[activityIndex], activityNames);
        }
        return data;
    }
    public void resize() {
        for (int index = 0; index < widths.length; index++) {
            toCreateTable.getColumnModel().getColumn(index).setMinWidth(widths[index]);
        }
    }
    private int getTableWidth() {
        int sum = 0;
        for (int width: widths) {
            sum += width;
        }
        return sum;
    }

}
