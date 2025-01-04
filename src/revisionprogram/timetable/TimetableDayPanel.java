package revisionprogram.timetable;

import revisionprogram.Borders;
import revisionprogram.Day;
import revisionprogram.EditableLabel;
import revisionprogram.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class TimetableDayPanel extends JPanel {
    protected final ArrayList<TimetableActivityPanel> dayActivityArrayList; // Keep revisionprogram.timetable activity panel list around for later use
    private final TimetablePanel parent;
    private final JTextField dayTitleField;
    private boolean isSelectedDay = false;
    public TimetableDayPanel(int index, Day day, boolean editMode, ArrayList<TimetableDayPanel> dayActivities, JPanel contentPanel, ArrayList<JTextField> dayNameFields, ArrayList<TimetableActivityPanel> activities, TimetablePanel parent, ArrayList<String> configuredActivities) {
        super();
        /// Logic for dayPanel highlighting
        this.parent = parent;
        this.setFocusable(true);
        // Add a mouse listener to get focus when clicked on
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Using requestFocusInWindow() rather than requestFocus() stops it from only highlighting the first time
                requestFocusInWindow();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                highlight();
                parent.focusIndex(index);
            }

            @Override
            public void focusLost(FocusEvent e) {
                unhighlight();
                parent.focusIndex(-1);
            }
        });
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        /// Add a border to the dayPanel
        this.setBorder(Borders.defaultBorder());
        // Make an arraylist in dayActivities
        // Keep it as a variable so indexes in dayActivities can change and it can still be used
        dayActivityArrayList = new ArrayList<>();
        dayActivities.add(this);
        /// Add a title field for the day name
        JPanel titleFieldPanel = new JPanel(new BorderLayout());
        String dayTitle = Objects.equals(day.name, "") ? Main.strings.getString("timetableDayTitle") + (index + 1) : day.name;
        // Add an editable label if it can be changed, otherwise make it non-editable
        if (editMode) {
            dayTitleField = new EditableLabel(dayTitle, contentPanel, true);
        } else {
            dayTitleField = new EditableLabel(dayTitle, contentPanel, false);
            // Make sure the title field is the correct size
            TimetablePanel.resizeField(dayTitleField);
            // Add a mouseListener to the dayTitleField so that focusing the panel works properly
            dayTitleField.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                }
            });
        }
        // Add the field to the panel and list
        titleFieldPanel.add(dayTitleField);
        dayNameFields.add(dayTitleField);

        /// Make the remove button
        if (editMode) {
            JButton removeDayButton = new JButton(Main.strings.getString("timetableRemove"));
            removeDayButton.addActionListener(e->{
                delete();
            });
            titleFieldPanel.add(removeDayButton, BorderLayout.LINE_END);

        }

        // Set the maximum size of the panel to be its preferred size in height, so it doesnt get too big
        titleFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleFieldPanel.getPreferredSize().height));
        this.add(titleFieldPanel);

        // If the day has any, add the activities
        for (int activityIndex = 0; activityIndex < day.activities.length; activityIndex++) {
            TimetableActivityPanel activity = new TimetableActivityPanel(editMode, configuredActivities, parent, this);
            activity.setData(day.activities[activityIndex]);
            activities.add(activity);
            dayActivityArrayList.add(activity);
            this.add(activity); // We don't need to add it second-last here because the create button hasn't been added yet
            this.add(Box.createVerticalStrut(3));
        }
        // Add a create button to the day panel if it can be edited, otherwise add the activities
        if (editMode) {
            JPanel controlPanel = new JPanel();
            JButton createButton = new JButton(Main.strings.getString("timetableCreateDay"));
            createButton.setFocusable(false);
            createButton.addActionListener(e -> {
                TimetableActivityPanel activity = new TimetableActivityPanel(true, configuredActivities, parent, this);
                activities.add(activity);
                dayActivityArrayList.add(activity);
                this.add(activity, this.getComponentCount() - 1); // Add it second-last here so createButton stays at the end
                this.add(Box.createVerticalStrut(3), this.getComponentCount() - 1);
                parent.refresh();
            });
            controlPanel.add(createButton);

            JButton removeButton = new JButton(Main.strings.getString("timetableRemove"));
            removeButton.setFocusable(false);
            removeButton.addActionListener(e->{
                if (!dayActivityArrayList.isEmpty()) {
                    // Remove the last item from the list
                    TimetableActivityPanel toRemove = dayActivityArrayList.get(dayActivityArrayList.size()-1);
                    activities.remove(toRemove);
                    dayActivityArrayList.remove(toRemove);
                    this.remove(toRemove);
                    this.remove(this.getComponentCount()-2); // Remove the vertical strut
                    this.revalidate();
                }
            });
            controlPanel.add(removeButton);
            this.add(controlPanel);
        }
    }
    public void highlight() {
        this.setBorder(Borders.highlightedBorder());
    }
    public void unhighlight() {
        this.setBorder(defaultBorder());
    }
    private Border defaultBorder() {
        if (isSelectedDay) {
            return Borders.colouredBorder(Color.YELLOW);
        }
        // Otherwise, return the default border
        return Borders.defaultBorder();
    }
    public void registerClick(TimetableActivityPanel component) {
        // For now, just register the focus on this
        // In future, could be used to handle deleting of items
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        System.out.println("Click Registered");

    }
    private void delete() {
        parent.dayActivities.remove(this);
        for (TimetableActivityPanel panel : dayActivityArrayList) {
            parent.activities.remove(panel);
        }
        parent.dayNameFields.remove(dayTitleField);
        parent.removeDay(this);
    }
    public void setSelectedDay(boolean selected) {
        isSelectedDay = selected;
        this.setBorder(defaultBorder());
    }
}
