package revisionprogram.documents.blankdocuments;

import revisionprogram.Borders;
import revisionprogram.Main;
import revisionprogram.components.panellist.PanelList;
import revisionprogram.documents.Document;
import revisionprogram.documents.DocumentTitlePanel;
import revisionprogram.documents.ViewDocumentPanel;
import revisionprogram.scheduledrevision.ScheduledRevisionManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public class BlankViewDocumentPanel extends ViewDocumentPanel {
    private BlankDocument originalDocument;
    private final DocumentTitlePanel titlePanel;
    private PanelList panelList;
    private JTextField inputField;
    private int currentBlankPanel = 0;
    private int currentPanelBlank = 0;


    public BlankViewDocumentPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        titlePanel = new DocumentTitlePanel(false);
        this.add(titlePanel, constraints);

        // Add the panel list
        constraints.weighty = 1;
        constraints.gridy++;
        panelList = new PanelList(BoxLayout.Y_AXIS);
        panelList.setCanCreate(false); // Disable the createButton
        this.add(panelList, constraints);

        // Add the inputPanel at the bottom. We dont want this to take up much space, so we can set the weighty to 0
        constraints.weighty = 0;
        constraints.gridy++;
        this.add(makeInputPanel(), constraints);

    }

    public JPanel makeInputPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(Borders.defaultBorder());

        inputField = new JTextField();
        inputField.setFont(Main.factFont);

        // Add an action listener to the input field to detect when enter is pressed
        inputField.addActionListener(e->handleInput());
        inputField.setColumns(20);

        // Add the inputField to the panel
        panel.add(inputField);
        return panel;
    }

    private void handleInput() {
        // Check the currentBlankPanel is in the list - if it isn't, we have run out of panels
        if (currentBlankPanel >= panelList.numPanels()) {
            System.out.println("currentBlankPanel out of bounds: BlankViewDocumentPanel.handleInput()");
            return;
        }
        // Go to the specific panel
        BlankViewCard card = (BlankViewCard) panelList.panelAt(currentBlankPanel);

        // Get this card's blanks
        BlankString blankString = card.getBlankString();
        Blank[] blanks = blankString.blanks(); // blanks should be sorted, as they are sorted within the card

        // Get the correct value at that blank
        Blank blank = blanks[currentPanelBlank];
        String string = card.getStringAt(blank);
        String input = inputField.getText();
        card.revealBlank(blank, isCorrect(input, string), input);



        int blankCount = blankString.blanks().length;
        // Increment the currentPanelBlank. If it equals the blankCount, reset, otherwise, it is incremented by the if
        if (++currentPanelBlank == blankCount) {
            currentPanelBlank = 0;
            currentBlankPanel += 1;
            // Use a while loop to avoid issues where there is no blank in the panel
            while (currentBlankPanel < panelList.numPanels()) {
                // Get the card
                card = (BlankViewCard) panelList.panelAt(currentBlankPanel);
                if (card.getBlankString().blanks().length != 0) {
                    break;
                }
                currentBlankPanel += 1;
            }
        }


        // Clear the inputField and highlight the blank
        inputField.setText("");
        // Highlight the blank
        if (!(currentBlankPanel >= panelList.numPanels())) {
            card.highlightBlank(card.getBlankString().blanks()[currentPanelBlank]); // Card will be correct - if we have changed it it will be updated
            // Scroll to view this card
            panelList.scrollToPanel(card);
        }

    }

    public boolean isCorrect(String toTest, String answer) {
        return Objects.equals(toTest, answer);
    }



    @Override
    public Document getDocument() {
        LocalDate nextRevision = ScheduledRevisionManager.getNextRevision(originalDocument.lastRevised, originalDocument.nextRevision);
        return new BlankDocument(originalDocument.title, originalDocument.getSubject(), originalDocument.getFileName(), originalDocument.blanks, LocalDate.now(), nextRevision);
    }

    @Override
    protected Document getOriginalDocument() {
        return originalDocument;
    }

    @Override
    public void setDocument(Document document) {
        originalDocument = (BlankDocument) document;
        // Set the title and subject
        titlePanel.setText(originalDocument.getTitle());
        titlePanel.setSubject(originalDocument.getSubject());

        // Set the document
        for (BlankString blankString: originalDocument.blanks) {
            panelList.addPanel(new BlankViewCard(blankString));
        }
        currentBlankPanel = 0;
        currentPanelBlank = 0;
        // Increase blanks to get the first panel which has a blank (in case panel 0 has no blanks)
        // Use a while loop to avoid issues where there is no blank in the panel
        while (currentBlankPanel < panelList.numPanels()) {
            // Get the card
            BlankViewCard card = (BlankViewCard) panelList.panelAt(currentBlankPanel);
            if (card.getBlankString().blanks().length != 0) {
                break;
            }
            currentBlankPanel += 1;
        }

        if (originalDocument.blanks.length != 0 & currentBlankPanel < panelList.numPanels()/*Check that we haven't gone past the end*/) {
            // Highlight the first blank
            BlankViewCard card = (BlankViewCard) panelList.panelAt(currentBlankPanel);
            BlankString blankString = card.getBlankString();

            if (blankString.blanks().length != 0) {
                Blank blank = blankString.blanks()[0];
                card.highlightBlank(blank);
            }
        }
    }

    @Override
    public void refresh() {

    }
}
