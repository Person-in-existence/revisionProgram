package revisionprogram.scheduledrevision;

import revisionprogram.CreateDialog;
import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;
import revisionprogram.timetable.Timetable;
import revisionprogram.timetable.TimetablePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateTimetableDocumentPanel extends JPanel {
    private JTable toCreateTable;
    private DefaultTableModel tableModel;
    private final TimetablePanel timetablePanel;
    public static final String[] headings = new String[] {Main.strings.getString("dayCreateNameHeading"), Main.strings.getString("dayCreateSubjectHeading"), Main.strings.getString("dayCreateDateHeading")};
    public static final int[] widths = {200, 100, 130};
    public static final int numRows = 6;
    private ArrayList<DocumentPrompt> prompts;
    public CreateTimetableDocumentPanel(TimetablePanel timetablePanel) {
        super();
        this.timetablePanel = timetablePanel;
        Timetable timetable = timetablePanel.makeTimetable();
        timetablePanel.addChangeListener(this::update);

        prompts = new ArrayList<>(Arrays.asList(ScheduledRevisionManager.getPrompts()));
        String[] activityNames = timetable.configuredActivities;

        tableModel = new DefaultTableModel(makeDataFromActivities(prompts, activityNames), headings);
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
                    int index = prompts.get(row).subjectIndex();
                    System.out.println(index);
                    String subject;
                    if (index < activityNames.length) {
                        subject = activityNames[index];
                    } else {
                        subject = Main.strings.getString("timetableNoActivitySelected");
                    }

                    CreateDialog createDialog = new CreateDialog(subject);
                    createDialog.setVisible(true);
                    createDialog.addCreationListener(()->removeItem(row));
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
        prompts = new ArrayList<>(Arrays.asList(ScheduledRevisionManager.getPrompts()));
        Object[][] data = makeDataFromActivities(prompts, timetable.configuredActivities);
        for (Object[] row: data) {
            tableModel.addRow(row);
        }
    }
    private Object[] makeRow(DocumentPrompt data, String[] activityNames) {
        Object[] row = new Object[headings.length];
        row[0] = data.name();
        int nameIndex = data.subjectIndex();
        if (nameIndex < activityNames.length) {
            row[1] = activityNames[nameIndex];
        } else {
            row[1] = Main.strings.getString("timetableNoActivitySelected");
        }
        row[2] = Main.getUserStyleDateString(data.date());
        return row;

    }
    private Object[][] makeDataFromActivities(ArrayList<DocumentPrompt> activities, String[] activityNames) {
        Object[][] data = new Object[activities.size()][];
        for (int activityIndex = 0; activityIndex < activities.size(); activityIndex++) {
            data[activityIndex] = makeRow(activities.get(activityIndex), activityNames);
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
    private void removeItem(int row) {
        prompts.remove(row);
        // Update ScheduledRevisionManager
        ScheduledRevisionManager.updatePromptFile(prompts);

    }

}
