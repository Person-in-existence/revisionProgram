import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class TextEditDocumentPanel extends EditDocumentPanel {
    private JTextPane mainPaneArea;
    private JTextField titleField;
    private TextDocument originalDocument;
    public TextEditDocumentPanel() {
        super(new GridBagLayout());
        originalDocument = new TextDocument();
        GridBagConstraints constraints = new GridBagConstraints();
        // Constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        /// Upper panel
        constraints.weightx = 1;
        JPanel upperPanel = makeUpperPanel();
        // Add the upper panel to the panel
        this.add(upperPanel, constraints);

        /// Main text pane
        // Weights for main text pane
        constraints.weighty = 1;
        constraints.gridy++;
        // Make text pane
        JPanel panePanel = mainTextPanePanel();

        this.add(panePanel, constraints);

    }
    private JPanel mainTextPanePanel() {
        // Use a grid to make the pane fill the panel
        JPanel panel = new JPanel(new GridLayout(1,1));
        JTextPane pane = new JTextPane();
        mainPaneArea = pane;
        pane.setFont(new Font(null, Font.PLAIN, 16));
        // Add the pane to the panel
        panel.add(pane);
        // Return the panel
        return panel;
    }

    private JPanel makeUpperPanel() {
        JPanel upperPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        // Set constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
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
        Font titleFont = new Font(null, Font.PLAIN, 30);
        titleLabel.setFont(titleFont);
        // Add the title label to the panel
        titlePanel.add(titleLabel,titlePanelConstraints);

        // increment constraints
        titlePanelConstraints.gridx++;
        /// Make the title Field
        // Change the title constraints
        // Create the field
        titleField = new JTextField(20);
        titleField.setFont(titleFont);
        // Add the titlefield to the panel
        titlePanel.add(titleField, titlePanelConstraints);

        // Increment constraints
        titlePanelConstraints.gridx++;
        // Set the weight width to 1
        titlePanelConstraints.weightx = 1;
        /// Add a filler panel to the titlePanel
        titlePanel.add(new JPanel(), titlePanelConstraints);

        // Add the title panel to the upper panel
        upperPanel.add(titlePanel, constraints);

        return upperPanel;
    }
    public String getMainPaneText() {
        return this.mainPaneArea.getText();
    }
    @Override
    public TextDocument getDocument() {
        return new TextDocument(titleField.getText(), mainPaneArea.getText());
    }
    @Override
    public boolean hasChanged() {
        return (Objects.equals(this.titleField.getText(), originalDocument.title) & Objects.equals(this.mainPaneArea.getText(), originalDocument.content));
    }
    @Override
    public void setDocument(Document document) {
        originalDocument = (TextDocument) document;
        titleField.setText(originalDocument.title);
        mainPaneArea.setText(originalDocument.content);
    }
    @Override
    public boolean close() {
        // If the document has not changed, it is OK to close (return true)
        if (!hasChanged()) {
            return true;
        }
        // Otherwise, TODO: HANDLE CLOSE WITH UNSAVED CHANGES - DIALOG AND SAVE ETC
        // TODO: REMOVE RETURN TRUE CASE
        return true;
    }
}
