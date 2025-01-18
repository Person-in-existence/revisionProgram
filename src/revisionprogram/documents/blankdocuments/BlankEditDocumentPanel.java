package revisionprogram.documents.blankdocuments;

import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;
import revisionprogram.components.panellist.PanelList;
import revisionprogram.documents.Document;
import revisionprogram.documents.DocumentTitlePanel;
import revisionprogram.documents.EditDocumentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

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

    private ScrollingPanel makeScrollingPanel() {
        contentPanel = new JPanel();
        // TEMPORARY
        BlankEditCard card = new BlankEditCard((int) (this.getWidth()*0.66));
        contentPanel.add(card);
        cards.add(card);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        return new ScrollingPanel(contentPanel);
    }

    @Override
    public void refresh() {

    }

    public void resizeCards() {
        int size = (int) (this.getWidth()*0.66);
        for (BlankEditCard card : cards) {
            card.resize(size);
        }
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
        return null;
    }

    @Override
    public void setDocument(Document document) {
        titlePanel.setText(document.getTitle());
        titlePanel.setSubject(document.getSubject());
        originalDocument = (BlankDocument) document;


    }

    @Override
    public boolean hasChanged() {
        return false;
    }

    @Override
    public boolean doSave() {
        return false;
    }
}
