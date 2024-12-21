import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DocumentEditTitlePanel extends JPanel {
    private final JTextField titleField;
    public DocumentEditTitlePanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        // Set constraints
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        /// Make title panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        // Make constraints
        GridBagConstraints titlePanelConstraints = new GridBagConstraints();
        titlePanelConstraints.gridy = 0;
        titlePanelConstraints.gridx = 0;
        // Add padding between components
        titlePanelConstraints.insets = new Insets(5,3,5,3);
        // Make a border
        titlePanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        /// Make title label
        JLabel titleLabel = new JLabel(Main.strings.getString("title"));
        titleLabel.setFont(Main.titleFont);
        // Add the title label to the panel
        titlePanel.add(titleLabel,titlePanelConstraints);

        // increment constraints
        titlePanelConstraints.gridx++;
        /// Make the title Field
        // Change the title constraints
        // Create the field
        titleField = new JTextField(20);
        titleField.setFont(Main.titleFont);
        // Add the titlefield to the panel
        titlePanel.add(titleField, titlePanelConstraints);

        // Increment constraints
        titlePanelConstraints.gridx++;
        // Set the weight width to 1
        titlePanelConstraints.weightx = 1;
        /// Add a filler panel to the titlePanel
        titlePanel.add(new JPanel(), titlePanelConstraints);

        // Add the title panel to the upper panel
        this.add(titlePanel, constraints);
    }
    public String getText() {
        return titleField.getText();
    }
    public void setText(String text) {
        titleField.setText(text);
    }
}
