package revisionprogram.documents.blankdocuments;

import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;
import revisionprogram.components.panellist.ListCard;
import revisionprogram.components.panellist.PanelList;
import revisionprogram.documents.Document;
import revisionprogram.documents.DocumentTitlePanel;
import revisionprogram.documents.EditDocumentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Objects;

public class BlankEditDocumentPanel extends EditDocumentPanel {
    private BlankDocument originalDocument;
    private DocumentTitlePanel titlePanel;
    private JPanel contentPanel;
    private ArrayList<BlankEditCard> cards = new ArrayList<>();
    private PanelList panelList;

    public BlankEditDocumentPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;


        // Make title panel
        titlePanel = new DocumentTitlePanel(true);
        this.add(titlePanel, constraints);
        constraints.gridy++;

        constraints.weighty = 1;


        // Make the scrolling panel
        panelList = new PanelList(BoxLayout.Y_AXIS);
        panelList.addCreateButtonListener((e)->{
            create();
        });

        this.add(panelList, constraints);
    }

    private void create() {
        // Make a new panel
        BlankEditCard card = new BlankEditCard((int) (this.getWidth()*0.66));
        panelList.addPanel(card);
    }

    private void createWithData(BlankString blankString) {
        // Make the card
        BlankEditCard card = new BlankEditCard((int) (this.getWidth()*0.66));

        String string = blankString.string();
        Blank[] blanks = blankString.blanks();

        card.setText(string);
        card.setBlanks(blanks);

        panelList.addPanel(card);
    }

    @Override
    public void refresh() {

    }


    @Override
    protected Document getOriginalDocument() {
        return originalDocument;
    }

    @Override
    protected String getTitle() {
        return titlePanel.getText();
    }

    @Override
    public Document getDocument() {
        String title = titlePanel.getText();
        String subject = titlePanel.getSubject();

        // Get panels from panelList
        ArrayList<ListCard> panels = panelList.getPanels();
        // Make the array of blank strings, with the same length as panels
        BlankString[] blankStrings = new BlankString[panels.size()];
        for (int index = 0; index < panels.size(); index++) {
            // Safe - we only ever add BlankEditCards to panels
            BlankEditCard card = (BlankEditCard) panels.get(index);
            // Get the blank string from the card
            blankStrings[index] = card.getBlankString();
        }

        // Use the originalDocument lastRevised, fileName and nextRevision
        return new BlankDocument(title, subject, originalDocument.getFileName(), blankStrings, originalDocument.getLastRevised(), originalDocument.getNextRevision());
    }

    @Override
    public void setDocument(Document document) {
        titlePanel.setText(document.getTitle());
        titlePanel.setSubject(document.getSubject());
        originalDocument = (BlankDocument) document;

        // Make the panels from the blank strings
        for (BlankString blankString : originalDocument.blanks) {
            createWithData(blankString);
        }
    }

    @Override
    public boolean hasChanged() {
        // Compare the titles
        String title = titlePanel.getText();
        if (!Objects.equals(title, originalDocument.getTitle())) {
            return true;
        }

        // Compare the subjects
        String subject = titlePanel.getSubject();
        if (!Objects.equals(subject, originalDocument.getSubject())) {
            return true;
        }

        // Compare the contents
        ArrayList<ListCard> cards = panelList.getPanels();
        BlankString[] originalBlankStrings = originalDocument.blanks;
        // Number of blankStrings
        if (cards.size() != originalBlankStrings.length) {
            return true;
        }

        for (int index = 0; index < cards.size(); index++) {
            BlankEditCard card = (BlankEditCard) cards.get(index);

            BlankString blankString = card.getBlankString();
            BlankString originalBlankString = originalBlankStrings[index];
            if (!blankString.isEqual(originalBlankString)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doSave() {
        // Essentially, we want to check whether: there is a title, and whether there is at least on blank card
        if (!Objects.equals(titlePanel.getText(), "")) {
            return true;
        }

        // Whether there is at least one blank card
        if (panelList.numPanels() != 0) {
            return true;
        }

        // We also want to check whether the original document has a fileName - that is, whether it has been saved before. If not, we don't want to save it.
        return !Objects.equals(originalDocument.fileName, "");
    }
}
