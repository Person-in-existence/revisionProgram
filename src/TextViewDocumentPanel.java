import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextViewDocumentPanel extends ViewDocumentPanel {
    private JTextPane mainPaneArea;
    private JTextField titleField;
    private TextDocument document;
    public TextViewDocumentPanel() {
        super(new GridBagLayout());
        document = new TextDocument();
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
        pane.setEditable(false);
        pane.setBorder(new EmptyBorder(5,5,5,5));
        mainPaneArea = pane;
        pane.setFont(Main.textContentFont);
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


        /// Make the title display
        // Create the field
        titleField = new JTextField(20);
        titleField.setEditable(false);
        titleField.setBorder(new EmptyBorder(5,5,5,5));

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
        upperPanel.add(titlePanel, constraints);

        return upperPanel;
    }
    public Document getDocument() {
        return document;
    }
    public void setDocument(Document document) {
        this.document = (TextDocument) document;
        this.mainPaneArea.setText(this.document.content);
        this.titleField.setText(this.document.title);
    }
    public void refresh(){}
    public boolean close() {
        return true;
    }
}
