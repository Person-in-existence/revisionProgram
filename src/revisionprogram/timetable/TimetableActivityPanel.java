package revisionprogram.timetable;

import com.formdev.flatlaf.ui.FlatBorder;
import revisionprogram.EditableLabel;
import revisionprogram.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class TimetableActivityPanel extends JPanel {
    private JTextField nameField;
    private ArrayList<String> configuredActivities;
    private JComboBox<String> activityChoice;
    private JLabel activityLabel;
    private boolean createNewActive = true;
    public final boolean canEdit;
    private TimetablePanel parent;
    private JLabel activityTypeLabel;
    private JPanel activityTypePanel;
    public TimetableActivityPanel(boolean canEdit, ArrayList<String> configuredActivities, TimetablePanel parent, TimetableDayPanel dayPanel) {
        super(new GridBagLayout());
        this.parent = parent;
        this.canEdit = canEdit;
        GridBagConstraints constraints = Main.makeConstraints();
        this.configuredActivities = configuredActivities;
        this.setFocusable(false);
        this.setBorder(new FlatBorder());

        JPanel namePanel = new JPanel(new BorderLayout());
        if (canEdit) {
            nameField = new EditableLabel(Main.strings.getString("timetableActivityNamePlaceholder"), this, true);
        } else {
            nameField = new EditableLabel("", this, false);
        }
        namePanel.add(nameField);

        this.add(namePanel, constraints);
        constraints.gridy++;

        // Activity type panel
        activityTypePanel = new JPanel(new GridBagLayout());
        GridBagConstraints activityTypeConstraints = Main.makeConstraints();
        // Activity type label
        activityTypeLabel = new JLabel(Main.strings.getString("timetableActivityLabel"));
        activityTypePanel.add(activityTypeLabel, activityTypeConstraints);
        // Add a padding panel
        activityTypeConstraints.gridx++;
        activityTypePanel.add(new JPanel(), activityTypeConstraints);

        activityTypeConstraints.gridx++;

        // Activity choice box
        if (canEdit) {
            activityChoice = new JComboBox<>(configuredActivities.toArray(new String[0]));
            // Add the create new option
            activityChoice.addItem(Main.strings.getString("timetableNewActivity"));
            activityChoice.addActionListener(_ -> {
                // if create new is not active, exit the action listener (stops it from triggering while components change)
                if (!createNewActive) {
                    return;
                }
                // Use invokeLater so the "create new" does not show up over the menu
                SwingUtilities.invokeLater(()-> {
                    int activityIndex = activityChoice.getSelectedIndex();
                    // Check if this is "create new"
                    if (activityIndex == this.configuredActivities.size()) {
                        // Open a dialog to create a new category
                        String name = JOptionPane.showInputDialog(this, Main.strings.getString("timetableNewActivityDialogText"), Main.strings.getString("timetableNewActivityDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
                        // Check the user did not press cancel, or input nothing
                        if (name != null) {
                            // Before adding the item, check that the item is not in the list already
                            boolean inList = false;
                            for (String configuredActivity : configuredActivities) {
                                if (Objects.equals(configuredActivity, name)) {
                                    inList = true;
                                    break;
                                }
                            }
                            if (!inList) {
                                configuredActivities.add(name);
                                activityChoice.insertItemAt(name, configuredActivities.size() - 1);
                                parent.configuredActivitiesUpdated();
                            }
                            // Select the new item
                            activityChoice.setSelectedItem(name);
                        }
                    }
                });
            });
            activityTypePanel.add(activityChoice, activityTypeConstraints);
        } else {
            activityLabel = new JLabel();
            activityTypePanel.add(activityLabel, activityTypeConstraints);
        }

        /// Add an on-click listener to this panel so that when it is clicked, the event is sent to the dayPanel above this
        TimetableActivityPanel selfReference = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dayPanel.registerClick(selfReference);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dayPanel.registerClick(selfReference);
            }
        });
        // If you can't edit, make all subcomponents transparent
        if (!canEdit) {
            activityTypePanel.setOpaque(false);
            namePanel.setOpaque(false);
            nameField.setOpaque(false);
            // Add a mouselistener to the nameField as it fixes the focus issue
            nameField.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dayPanel.registerClick(selfReference);
                }
            });
        }


        this.add(activityTypePanel, constraints);
        // Set the maximum size to be the preferred size, so they don't expand to fill the revisionprogram.timetable panel
        this.setMaximumSize(this.getPreferredSize());
    }
    public void setData(TimetableActivity activity) {
        if (canEdit) {
            activityChoice.setSelectedIndex(activity.activityIndex());
        } else {
            if (activity.activityIndex() == configuredActivities.size()) {
                activityLabel.setText(Main.strings.getString("timetableNoActivitySelected"));
            } else {
                activityLabel.setText(configuredActivities.get(activity.activityIndex()));
            }
            TimetablePanel.resizeLabel(activityLabel);
        }
        nameField.setText(activity.name());
        TimetablePanel.resizeField(nameField);
        if (nameField.getPreferredSize().width > this.getMaximumSize().width) {
            Dimension d = new Dimension (nameField.getPreferredSize().width + 10, this.getPreferredSize().height);
            this.setPreferredSize(d);
            this.setMaximumSize(d);
        }
        if (activityLabel != null) {
            updateSize(activityLabel);
        }
        if (activityChoice != null) {
            updateSize(activityChoice);
        }

        this.revalidate();
        this.repaint();
    }
    public void configuredActivitiesUpdated() {
        // Temporarily disable the action listeners while this is changing
        createNewActive = false;
        // Get the selected item to preserve it (index works as things can only be added onto the end, but it has to be checked in case it is the last thing in the JComboBox)
        int selectedIndex = activityChoice.getSelectedIndex();
        int itemCount = activityChoice.getItemCount();

        activityChoice.removeAllItems();
        for (String configuredActivity : configuredActivities) {
            activityChoice.addItem(configuredActivity);
        }
        // Add "create new"
        activityChoice.addItem(Main.strings.getString("timetableNewActivity"));
        // Select the correct, previously selected item
        if (selectedIndex != itemCount - 1) {
            activityChoice.setSelectedIndex(selectedIndex);
        } else {
            // Safe as one item is added for every item in configured activities, plus one extra (create new)
            activityChoice.setSelectedIndex(configuredActivities.size());
        }
        createNewActive = true;
    }
    public TimetableActivity getActivity() {
        return new TimetableActivity(nameField.getText(), activityChoice.getSelectedIndex());
    }
    private void updateSize(Component c) {
        if (c.getPreferredSize().width + activityTypeLabel.getPreferredSize().width + 10 > this.getMaximumSize().width) {
            Dimension d = new Dimension(c.getPreferredSize().width + activityTypeLabel.getPreferredSize().width + 10, this.getPreferredSize().height);
            this.setPreferredSize(d);
            this.setMaximumSize(d);
            this.setMinimumSize(d);
            this.revalidate();
            this.repaint();
            parent.refresh();
        }
    }
}
