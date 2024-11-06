import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BoxView;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TimetablePanel extends JPanel {
    private JPanel contentPanel;
    private JButton switchModeButton;
    private ArrayList<String> configuredActivities;
    private ArrayList<TimetableActivityPanel> activities = new ArrayList<>();
    private ArrayList<JTextField> dayNameFields = new ArrayList<>();
    private ArrayList<ArrayList<TimetableActivityPanel>> dayActivities = new ArrayList<>();
    private boolean editMode = false;
    private Timetable timetable;

    public TimetablePanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Make the content panel (which the other panels will go in)
        this.contentPanel = new JPanel(new GridLayout(1,1));

        // Make the upper panel (buttons)
        JPanel upperPanel = makeUpperPanel();
        this.add(upperPanel, constraints);

        // Add a (small) panel for padding
        constraints.gridy++;
        this.add(new JPanel(), constraints);

        // Add the content panel
        // Change constraints
        constraints.gridy++;
        constraints.weighty = 1;
        this.add(contentPanel, constraints);
        timetable = new Timetable();
        // Try reading from file.
        FileException exception = timetable.readFromFile();
        if (exception.failed) {
            System.err.println("Reading from file failed");
            System.err.println(exception.getMessage());
            System.err.println(Arrays.toString(exception.getStackTrace()));
        }
        this.configuredActivities = new ArrayList<>(List.of(timetable.configuredActivities));
        makeViewPanel(timetable);

    }
    private JPanel makeUpperPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0;
        constraints.weightx = 1;

        /// Add a padding panel
        panel.add(new JPanel(), constraints);

        // Add the switch mode button
        switchModeButton = new JButton(Main.strings.getString("timetableToEdit"));
        switchModeButton.setFocusable(false);
        switchModeButton.addActionListener(_->switchMode());
        // Increment constraints
        constraints.gridx++;
        constraints.weightx = 0;
        // Add the button
        panel.add(switchModeButton, constraints);


        return panel;
    }
    private void makeViewPanel(Timetable timetable) {
        editMode = false;
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.X_AXIS));

        for (int dayIndex = 0; dayIndex < timetable.days.length; dayIndex++) {
            viewPanel.add(makeDayPanel(dayIndex, timetable.days[dayIndex]));
        }

        contentPanel.removeAll();
        contentPanel.add(viewPanel);
        contentPanel.revalidate();
        contentPanel.repaint();

    }
    public void switchToView() {
        close();
        timetable = makeTimetable();
        makeViewPanel(timetable);
    }
    public void switchToEdit() {
        makeEditPanel(timetable);
    }
    public void switchMode() {
        if (editMode) {
            switchToView();
            switchModeButton.setText(Main.strings.getString("timetableToEdit"));
        } else {
            switchToEdit();
            switchModeButton.setText(Main.strings.getString("timetableToView"));
        }
    }
    private void makeEditPanel(Timetable timetable) {
        editMode = true;
        // reset all of the lists
        activities = new ArrayList<>();
        dayNameFields = new ArrayList<>();
        dayActivities = new ArrayList<>();

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));


        // Add panels for the days in the timetable
        for (int dayIndex = 0; dayIndex < timetable.days.length; dayIndex++) {
            editPanel.add(makeDayPanel(dayIndex, timetable.days[dayIndex]));
            editPanel.add(Box.createHorizontalStrut(5));
        }
        // Add the "create" button
        JButton createButton = new JButton(Main.strings.getString("timetableCreateDay"));
        createButton.setFocusable(false);
        createButton.addActionListener(_->{
            int index = editPanel.getComponentCount()-1;
            editPanel.add(makeDayPanel(index/2, new Day()), index);
            // Add some spacing between panels
            editPanel.add(Box.createHorizontalStrut(5), index);
            editPanel.revalidate();
            editPanel.repaint();
        });

        // Add the button to the panel
        editPanel.add(createButton);

        contentPanel.removeAll();
        contentPanel.add(editPanel);
    }
    private JPanel makeDayPanel(int index, Day day) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));

        // Make an arraylist in dayActivities
        // Keep it as a variable so indexes in dayActivities can change and it can still be used
        ArrayList<TimetableActivityPanel> dayActivityArrayList = new ArrayList<>();
        dayActivities.add(dayActivityArrayList);
        /// Add a title field for the day name
        JPanel titleFieldPanel = new JPanel(new BorderLayout());
        String dayTitle = Objects.equals(day.name, "") ? Main.strings.getString("timetableDayTitle") + (index + 1) : day.name;
        // Add an editable label if it can be changed, otherwise make it non-editable
        JTextField dayTitleField;
        if (editMode) {
            dayTitleField = new EditableLabel(dayTitle, contentPanel);
        } else {
            dayTitleField = new JTextField(day.name);
            dayTitleField.setEditable(false);
            dayTitleField.setFocusable(false);
            dayTitleField.setBorder(new EmptyBorder(1,1,1,1));
            // Make sure the title field is the correct size
            resizeField(dayTitleField);
        }
        // Add the field to the panel and list
        titleFieldPanel.add(dayTitleField);
        dayNameFields.add(dayTitleField);

        // Set the maximum size of the panel to be its preferred size in height, so it doesnt get too big
        titleFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleFieldPanel.getPreferredSize().height));
        dayPanel.add(titleFieldPanel);

        // If the day has any, add the activities
        for (int activityIndex = 0; activityIndex < day.activities.length; activityIndex++) {
            TimetableActivityPanel activity = new TimetableActivityPanel(editMode, configuredActivities, this);
            activity.setData(day.activities[activityIndex]);
            activities.add(activity);
            dayActivityArrayList.add(activity);
            dayPanel.add(activity); // We don't need to add it second-last here because the create button hasn't been added yet
            dayPanel.add(Box.createVerticalStrut(3));
        }
        // Add a create button to the day panel if it can be edited, otherwise add the activities
        if (editMode) {
            JButton createButton = new JButton(Main.strings.getString("timetableCreateDay"));
            createButton.setFocusable(false);
            createButton.addActionListener(_ -> {
                TimetableActivityPanel activity = new TimetableActivityPanel(true, configuredActivities, this);
                activities.add(activity);
                dayActivityArrayList.add(activity);
                dayPanel.add(activity, dayPanel.getComponentCount() - 1); // Add it second-last here so createButton stays at the end
                dayPanel.add(Box.createVerticalStrut(3), dayPanel.getComponentCount() - 1);
                refresh();
            });
            dayPanel.add(createButton);
        }
        return dayPanel;
    }
    public void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    public void configuredActivitiesUpdated() {
        // Because configured activities is an arrayList, it automatically updates on all of the activities, but not on the combo boxes, so this function goes through those and updates them.
        for (int index = 0; index < activities.size(); index++) {
            activities.get(index).configuredActivitiesUpdated();
        }


    }
    public Timetable makeTimetable() {
        Day[] days = new Day[dayActivities.size()];
        // Loop through the days
        for (int dayIndex = 0; dayIndex < dayActivities.size(); dayIndex++) {
            ArrayList<TimetableActivityPanel> activityList = dayActivities.get(dayIndex);
            TimetableActivity[] dayList = new TimetableActivity[activityList.size()];
            // Loop through the activities in the day
            for (int activityIndex = 0; activityIndex < activityList.size(); activityIndex++) {
                dayList[activityIndex] = activityList.get(activityIndex).getActivity();
            }
            // Make a day object from the list
            Day day = new Day(dayList, dayNameFields.get(dayIndex).getText());
            days[dayIndex] = day;
        }
        return new Timetable(days, configuredActivities.toArray(new String[0]));
    }
    public void close() {
        if (editMode) {
            Timetable timetable = makeTimetable();
            FileException fileException = timetable.writeToFile();
            if (fileException.failed) {
                // Try again and write error to console
                System.err.println("Writing to file failed, trying again");
                fileException.printStackTrace();
                System.err.println(fileException.getMessage());

                FileException fileException2 = timetable.writeToFile();
                if (fileException2.failed) {
                    System.err.println("Failed again, not retrying");
                    fileException2.printStackTrace();
                    System.err.println(fileException2.getMessage());
                    JOptionPane.showMessageDialog(this, fileException2.getMessage(),Main.strings.getString("fileErrorTitle"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    public static void resizeField(JTextField field) {
        FontMetrics metrics = field.getFontMetrics(field.getFont());
        int width = metrics.stringWidth(field.getText()) + 10;
        Dimension d = new Dimension(width, field.getPreferredSize().height);
        field.setPreferredSize(d);
        field.setMinimumSize(d);

    }
    public static void resizeLabel(JLabel label) {
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int width = metrics.stringWidth(label.getText()) + 10;
        Dimension d = new Dimension(width, label.getPreferredSize().height);
        label.setPreferredSize(d);
        label.setMinimumSize(d);
    }
}
