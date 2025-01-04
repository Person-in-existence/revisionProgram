package revisionprogram;

import javax.swing.*;
import java.awt.*;

public class CreateDialog extends JDialog {
    public CreateDialog(String subject) {
        super();
        this.setSize(Main.dialogWidth, Main.dialogHeight);

        // Make the panel to add stuff to
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        // Make the constraints for the panel
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = new Insets(2,2,3,3);

        // Make a drop-down box
        JComboBox<String> comboBox = new JComboBox<>(Main.docTypes);
        dialogPanel.add(comboBox, constraints);
        // Increment gridy by 1
        constraints.gridy++;

        // Make a button to create
        JButton submitButton = new JButton(Main.strings.getString("submitCreateDialog"));
        submitButton.addActionListener(_->{
            // Call the function
            Main.getWindow().makeNewDocument(Main.getTypeFromIndex(comboBox.getSelectedIndex()), subject);
            // Close the dialog
            this.dispose();
        });
        // Add button to panel
        dialogPanel.add(submitButton, constraints);

        // Add the panel to the dialog
        this.add(dialogPanel);

        // Give the dialog a title
        this.setTitle(Main.strings.getString("createDialogTitle"));

        /// Centre the dialog
        this.setLocation(Main.getWindow().getDialogCentre(Main.dialogWidth, Main.dialogHeight));

    }
}
