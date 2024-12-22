import javax.print.Doc;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextViewDocumentPanel extends ViewDocumentPanel {
    private JTextPane mainPaneArea;
    private DocumentTitlePanel titlePanel;
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
        titlePanel = new DocumentTitlePanel(false);
        // Add the upper panel to the panel
        this.add(titlePanel, constraints);

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


    public Document getDocument() {
        return document;
    }
    public void setDocument(Document document) {
        this.document = (TextDocument) document;
        this.mainPaneArea.setText(this.document.content);
        this.titlePanel.setText(this.document.title);
    }
    public void refresh(){}
    public boolean close() {
        return true;
    }
}
