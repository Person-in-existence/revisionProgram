package revisionprogram.documents.textdocuments;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import revisionprogram.Main;
import revisionprogram.documents.*;

public class TextEditDocumentPanel extends EditDocumentPanel {
    private JTextPane mainPaneArea;
    private DocumentTitlePanel titlePanel;
    private TextDocument originalDocument;
    public TextEditDocumentPanel() {
        super(new GridBagLayout());
        originalDocument = new TextDocument();
        GridBagConstraints constraints = new GridBagConstraints();
        // Constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        /// Title panel
        constraints.weightx = 1;
        titlePanel = new DocumentTitlePanel(true);
        // Add the title panel to the panel
        this.add(titlePanel, constraints);

        /// revisionprogram.Main text pane
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
        pane.setFont(Main.textContentFont);
        // Add the pane to the panel
        panel.add(pane);
        // Return the panel
        return panel;
    }

    public String getMainPaneText() {
        return this.mainPaneArea.getText();
    }
    @Override
    public TextDocument getDocument() {
        return new TextDocument(titlePanel.getText(), mainPaneArea.getText(), originalDocument.fileName, originalDocument.lastRevised, originalDocument.nextRevision);
    }
    @Override
    public boolean hasChanged() {
        return !(Objects.equals(this.titlePanel.getText(), originalDocument.title) & Objects.equals(this.mainPaneArea.getText(), originalDocument.content));
    }
    @Override
    public void setDocument(Document document) {
        originalDocument = (TextDocument) document;
        titlePanel.setText(originalDocument.title);
        mainPaneArea.setText(originalDocument.content);
    }
    public void refresh(){}

    @Override
    protected Document getOriginalDocument() {
        return this.originalDocument;
    }
    protected String getTitle() {
        return titlePanel.getText();
    }
}
