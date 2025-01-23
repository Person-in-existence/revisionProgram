package revisionprogram.timetable;

import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;
import revisionprogram.files.FileException;
import revisionprogram.Day;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetablePanel extends JPanel {
    private JPanel contentPanel;
    private JButton switchModeButton;
    private ArrayList<String> configuredActivities;
    protected ArrayList<TimetableActivityPanel> activities = new ArrayList<>();
    protected ArrayList<JTextField> dayNameFields = new ArrayList<>();
    protected ArrayList<TimetableDayPanel> dayActivities = new ArrayList<>();
    private boolean isSomethingFocused = false;
    private int selectedIndex = -1;
    private int setDayIndex;
    private int originalSetDayIndex;
    private boolean editMode = false;
    private JPanel editPanel;
    private Timetable timetable;
    private final ArrayList<ChangeListener> changeListeners = new ArrayList<>();

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
        // Make a scrollPanel for the content panel
        ScrollingPanel scrollingPanel = new ScrollingPanel(contentPanel) {
            @Override
            public Dimension getPreferredSize() {
                Dimension superPreferredSize = super.getPreferredSize();
                if (superPreferredSize != null) {
                    return new Dimension(500, super.getPreferredSize().height);
                } else{
                    return new Dimension(500,500);
                }
            }
            public Dimension getMinimumSize() {
                return new Dimension(300, super.getPreferredSize().height);
            }
        };
        scrollingPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollingPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollingPanel.getHorizontalScrollBar().setUnitIncrement(12);

        TimetablePanel thisReference = this;

        contentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisReference.revalidate();
            }
        });

        this.add(scrollingPanel, constraints);


        timetable = new Timetable();
        // Try reading from file.
        FileException exception = timetable.readFromFile();
        if (exception.failed) {
            System.err.println("Reading from file failed");
            System.err.println(exception.getMessage());
            System.err.println(Arrays.toString(exception.getStackTrace()));
        }
        this.configuredActivities = new ArrayList<>(List.of(timetable.configuredActivities));
        this.originalSetDayIndex = timetable.getIndexOnDay(LocalDate.now());
        this.setDayIndex = originalSetDayIndex;
        makeViewPanel(timetable);

    }
    private JPanel makeUpperPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();

        /// Add a set day button
        JButton setDayButton = new JButton(Main.strings.getString("timetableSetDay"));
        setDayButton.setFocusable(false);
        setDayButton.addActionListener(e->setDay());
        panel.add(setDayButton, constraints);
        // Increment constraints
        constraints.gridx++;

        /// Add a padding panel
        constraints.weightx = 1;
        panel.add(new JPanel(), constraints);

        // Add the switch mode button
        switchModeButton = new JButton(Main.strings.getString("timetableToEdit"));
        switchModeButton.setFocusable(false);
        switchModeButton.addActionListener(e->switchMode());
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
            TimetableDayPanel day = makeDayPanel(dayIndex, timetable.days[dayIndex]);
            if (dayIndex == setDayIndex) {
                day.setSelectedDay(true);
            }
            viewPanel.add(day);
        }

        contentPanel.removeAll();
        contentPanel.add(viewPanel);
        contentPanel.revalidate();
        contentPanel.repaint();

    }
    public void switchToView() {
        close();
        timetable = makeTimetable();
        dayActivities = new ArrayList<>();
        makeViewPanel(timetable);
        // Fire the changeListeners and update Window
        Main.getWindow().setTimetable(timetable);
        fireChangeListeners();
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

        editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));


        // Add panels for the days in the revisionprogram.timetable
        for (int dayIndex = 0; dayIndex < timetable.days.length; dayIndex++) {
            editPanel.add(makeDayPanel(dayIndex, timetable.days[dayIndex]));
            editPanel.add(Box.createHorizontalStrut(5));
        }
        // Add the "create" button
        JButton createButton = new JButton(Main.strings.getString("timetableCreateDay"));
        createButton.setFocusable(false);
        createButton.addActionListener(e->{
            int index = editPanel.getComponentCount()-1;
            TimetableDayPanel day = makeDayPanel(index/2, new Day());
            // Select it if it is the only day
            if (dayActivities.isEmpty()) {
                day.setSelectedDay(true);
                setDayIndex = 0;
            }
            editPanel.add(day, index);
            // If this new day is the only day thats there
            // Add some spacing between panels
            editPanel.add(Box.createHorizontalStrut(5), index + 1); // Add 1 to the index because the previous component means everything shifts over 1
            editPanel.revalidate();
            editPanel.repaint();
        });

        // Add the button to the panel
        editPanel.add(createButton);

        contentPanel.removeAll();
        contentPanel.add(editPanel);
    }
    private TimetableDayPanel makeDayPanel(int index, Day day) {
        return new TimetableDayPanel(index, day, editMode, dayActivities, contentPanel, dayNameFields, activities, this, configuredActivities);
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
        if (!editMode) {
            return timetable;
        }
        Day[] days = new Day[dayActivities.size()];
        // Loop through the days
        for (int dayIndex = 0; dayIndex < dayActivities.size(); dayIndex++) {
            ArrayList<TimetableActivityPanel> activityList = dayActivities.get(dayIndex).dayActivityArrayList;
            TimetableActivity[] dayList = new TimetableActivity[activityList.size()];
            // Loop through the activities in the day
            for (int activityIndex = 0; activityIndex < activityList.size(); activityIndex++) {
                dayList[activityIndex] = activityList.get(activityIndex).getActivity();
            }
            // Make a day object from the list
            Day day = new Day(dayList, dayNameFields.get(dayIndex).getText());
            days[dayIndex] = day;
        }
        return new Timetable(LocalDate.now(), setDayIndex, days, configuredActivities.toArray(new String[0]));
    }
    public void close() {
        if (editMode | (originalSetDayIndex != setDayIndex)) {
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
                    Main.showErrorDialog(fileException2.getMessage());
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
    public void focusIndex(int index) {
        isSomethingFocused = index != -1;
        selectedIndex = index;
    }
    private void setDay() {
        if (isSomethingFocused) {
            System.out.println("Day set");
            System.out.println(selectedIndex);
            System.out.println(dayActivities.size());
            // Check whether there is another day selected to deselect
            if (setDayIndex != -1) {
                dayActivities.get(setDayIndex).setSelectedDay(false);
            }
            setDayIndex = selectedIndex;
            // Set the current day
            dayActivities.get(setDayIndex).setSelectedDay(true);
            // Request the focus in this window so that the yellow border shows
            this.requestFocusInWindow();
        }
    }
    protected void removeDay(TimetableDayPanel panel) {
        if (editMode) {
            Component[] editPanelComponents = editPanel.getComponents();
            int finalIndex = -1;
            for (int index = 0; index < editPanelComponents.length; index++) {
                if (editPanelComponents[index] == panel) {
                    finalIndex = index;
                    break;
                }
            }
            // Check if the component has not been found
            if (finalIndex == -1) {
                System.err.println("Component could not be found");
                return;
            }

            editPanel.remove(finalIndex);
            editPanel.remove(finalIndex); // Strut comes after, so remove that same index again to remove the strut
            this.revalidate();
        } else {
            System.err.println("Tried to remove panel while not in edit mode");
        }
    }
    private void fireChangeListeners() {
        for (ChangeListener changeListener: changeListeners) {
            changeListener.change();
        }
    }
    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }
    public interface ChangeListener {
        void change();
    }
}
