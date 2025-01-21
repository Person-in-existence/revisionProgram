package revisionprogram.documents.factdocuments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Objects;

import revisionprogram.Main;
import revisionprogram.documents.*;
import revisionprogram.components.ScrollingPanel;

public class FactEditDocumentPanel extends EditDocumentPanel {
    public DocumentTitlePanel titlePanel;
    private JPanel contentPanel;
    private ArrayList<FactPanel> panels = new ArrayList<>();
    private FactDocument originalDocument;

    public FactEditDocumentPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Make the title panel
        titlePanel = new DocumentTitlePanel(true);
        this.add(titlePanel, constraints);

        // Set the weighty to 1
        constraints.gridy++;
        constraints.weighty = 1;
        this.add(makeContentPanel(), constraints);
        originalDocument = new FactDocument();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refresh();
            }
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }

    private JScrollPane makeContentPanel() {

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Make a panel for the button so it is centred in the window
        JPanel addNewFactButtonPanel = new JPanel();
        JButton addNewFactButton = makeNewFactButton();
        addNewFactButtonPanel.add(addNewFactButton);
        contentPanel.add(addNewFactButtonPanel);


        return new ScrollingPanel(contentPanel);
    }
    private void newFact(Fact fact) {
        FactPanel newFact = new FactPanel(fact, (int) (this.getSize().width*0.66), this);
        panels.add(newFact);
        contentPanel.add(newFact, contentPanel.getComponentCount() - 1);
        this.revalidate();
        this.repaint();
    }

    public void deleteFact(FactPanel panel) {
        contentPanel.remove(panel);
        panels.remove(panel);
        this.revalidate();
        this.repaint();
    }

    private JButton makeNewFactButton() {
        JButton addNewFactButton = new JButton(Main.strings.getString("factNewFact"));
        addNewFactButton.setFocusable(false);
        addNewFactButton.addActionListener(e->newFact(new Fact()));
        return addNewFactButton;
    }

    @Override
    public void refresh() {
        FactDocument currentDocument = getDocument();
        contentPanel.removeAll();
        panels = new ArrayList<>();
        // Add the button back
        contentPanel.add(makeNewFactButton());
        for (Fact fact : currentDocument.facts) {
            // Make fact panels and add them to contentPanel.
            newFact(fact);

        };
    }

    public FactDocument getDocument() {
        Fact[] facts = new Fact[panels.size()];
        for (int index = 0; index < panels.size(); index++) {
            facts[index] = panels.get(index).getFact();
        }
        return new FactDocument(titlePanel.getSubject(), titlePanel.getText(), originalDocument.fileName, facts, originalDocument.lastRevised, originalDocument.nextRevision);
    }

    @Override
    public void setDocument(Document document) {
        titlePanel.setText(document.getTitle());
        titlePanel.setSubject(document.getSubject());
        originalDocument = (FactDocument) document;
        Fact[] facts = originalDocument.facts;
        contentPanel.removeAll();
        // Add the button back
        contentPanel.add(makeNewFactButton());
        for (Fact fact : facts) {
            // Make fact panels and add them to contentPanel.
            newFact(fact);

        }
    }

    @Override
    public boolean hasChanged() {
        FactDocument currentDocument = getDocument();
        // Check the title
        if (!(Objects.equals(getTitle(), originalDocument.getTitle()) & Objects.equals(titlePanel.getSubject(), originalDocument.subject))) {
            return true;
        }
        // Check individual facts
        // Check lengths
        if (currentDocument.facts.length != originalDocument.facts.length) {
            return true;
        }
        for (int index = 0; index < currentDocument.facts.length; index++) {
            // Check whether the corresponding fact is the same
            if (!Objects.equals(originalDocument.facts[index].question, currentDocument.facts[index].question) | !Objects.equals(originalDocument.facts[index].answer, currentDocument.facts[index].answer)) {
                return true;
            }
        }
        // If it hasn't returned true, then it is the same, so it hasn't changed
        return false;
    }

    @Override
    public boolean doSave() {
        FactDocument currentDocument = getDocument();
        boolean title = !(Objects.equals(currentDocument.title, originalDocument.title) & Objects.equals(originalDocument.title, ""));
        boolean content = !(currentDocument.facts.length == 0 & originalDocument.facts.length != 0);
        return title | content;
    }

    @Override
    protected String getTitle() {
        return titlePanel.getText();
    }
    @Override
    protected Document getOriginalDocument() {
        return originalDocument;
    }
}
