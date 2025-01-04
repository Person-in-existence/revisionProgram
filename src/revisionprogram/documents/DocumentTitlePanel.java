package revisionprogram.documents;

import revisionprogram.Borders;
import revisionprogram.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DocumentTitlePanel extends JPanel {
    private final JTextField titleField;
    private JComboBox<String> subjectSelectionBox;
    private JLabel subjectLabel;
    private final boolean editable;
    public DocumentTitlePanel(boolean editable) {
        super(new GridBagLayout());
        this.editable = editable;
        GridBagConstraints constraints = Main.makeConstraints();
        // Set constraints

        /// Make title panel
        // Make constraints
        // Add padding between components
        constraints.insets = new Insets(5,3,5,3);

        // Make a border
        this.setBorder(new EmptyBorder(0, 10, 10, 10));

        /// Make title label
        JLabel titleLabel = new JLabel(Main.strings.getString("title"));
        titleLabel.setFont(Main.titleFont);
        // Add the title label to the panel
        this.add(titleLabel,constraints);
        // Increment constraints
        constraints.gridx++;

        /// Make the title Field
        // Change the title constraints
        // Create the field
        titleField = new JTextField(20);
        titleField.setFont(Main.titleFont);
        titleField.setEditable(editable);
        if (editable) {
            titleField.setBorder(Borders.defaultBorder());
        } else {
            titleField.setBorder(new EmptyBorder(1,1,1,1));
        }
        // Add the titlefield to the panel
        this.add(titleField, constraints);

        // Increment constraints
        constraints.gridx++;

        /// Make the subject selection box
        // Subject label
        JLabel subjectAreaLabel = new JLabel(Main.strings.getString("documentSubjectLabel"));
        subjectAreaLabel.setFont(Main.titleFont);
        // Add a border for spacing on the left
        subjectAreaLabel.setBorder(new EmptyBorder(0,50,0,0));
        this.add(subjectAreaLabel, constraints);
        constraints.gridx++;

        if (editable) {

            subjectSelectionBox = new JComboBox<>(Main.getWindow().getSubjects());
            subjectSelectionBox.addItem(Main.strings.getString("timetableNoActivitySelected"));
            subjectSelectionBox.setFont(Main.titleFont);
            this.add(subjectSelectionBox, constraints);
        } else {
            subjectLabel = new JLabel();
            subjectLabel.setFont(Main.titleFont);
            this.add(subjectLabel, constraints);
        }
        constraints.gridx++;


        // Set the weight width to 1
        constraints.weightx = 1;
        /// Add a filler panel to this
        this.add(new JPanel(), constraints);

    }
    public String getText() {
        return titleField.getText();
    }
    public void setText(String text) {
        titleField.setText(text);
    }
    public void setSubject(String subject) {
        if (editable) {
            subjectSelectionBox.setSelectedItem(subject);
        } else {
            subjectLabel.setText(subject);
        }
    }
    public void setSubjectItems(String[] subjects) {
        if (editable) {
            subjectSelectionBox.removeAllItems();
            for (String string: subjects) {
                subjectSelectionBox.addItem(string);
            }
            subjectSelectionBox.addItem(Main.strings.getString("timetableNoActivitySelected"));
        }
    }
    public String getSubject() {
        if (editable) {
            return (String) subjectSelectionBox.getSelectedItem();
        } else {
            return subjectLabel.getText();
        }
    }
}
