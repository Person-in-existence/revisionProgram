import com.formdev.flatlaf.ui.FlatBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class TimetableDayPanel extends JPanel {
    private final ArrayList<TimetableActivityPanel> dayActivityArrayList; // Keep timetable activity panel list around for later use
    public TimetableDayPanel(int index, Day day, boolean editMode, ArrayList<ArrayList<TimetableActivityPanel>> dayActivities, JPanel contentPanel, ArrayList<JTextField> dayNameFields, ArrayList<TimetableActivityPanel> activities, TimetablePanel parent, ArrayList<String> configuredActivities) {
        /// Logic for dayPanel highlighting
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
        this.setBorder(defaultBorder());
        // Make an arraylist in dayActivities
        // Keep it as a variable so indexes in dayActivities can change and it can still be used
        dayActivityArrayList = new ArrayList<>();
        dayActivities.add(dayActivityArrayList);
        /// Add a title field for the day name
        JPanel titleFieldPanel = new JPanel(new BorderLayout());
        String dayTitle = Objects.equals(day.name, "") ? Main.strings.getString("timetableDayTitle") + (index + 1) : day.name;
        // Add an editable label if it can be changed, otherwise make it non-editable
        JTextField dayTitleField;
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
            JButton createButton = new JButton(Main.strings.getString("timetableCreateDay"));
            createButton.setFocusable(false);
            createButton.addActionListener(_ -> {
                TimetableActivityPanel activity = new TimetableActivityPanel(true, configuredActivities, parent, this);
                activities.add(activity);
                dayActivityArrayList.add(activity);
                this.add(activity, this.getComponentCount() - 1); // Add it second-last here so createButton stays at the end
                this.add(Box.createVerticalStrut(3), this.getComponentCount() - 1);
                parent.refresh();
            });
            this.add(createButton);
        }
    }
    public void highlight() {
        this.setBorder(highlightedBorder());
    }
    public void unhighlight() {
        this.setBorder(defaultBorder());
    }
    private static Border defaultBorder() {
        return new CompoundBorder(new FlatBorder(), new EmptyBorder(1,1,1,1));
    }
    private static Border highlightedBorder() {
        FlatBorder border = new FlatBorder();
        border.applyStyleProperty("borderColor", Color.cyan);
        border.applyStyleProperty("focusWidth", 1);
        return border;
    }
    public void registerClick(TimetableActivityPanel component) {
        // For now, just register the focus on this
        // In future, could be used to handle deleting of items
        // TODO: DELETING OF ITEMS
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        System.out.println("Click Registered");

    }
}
