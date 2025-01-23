package revisionprogram.files;

import revisionprogram.DocumentErrorPackage;
import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.components.panellist.PanelList;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class FilesPanel extends JPanel {
    private JComboBox<String> subjectSelector;
    private PanelList panelList;
    private DocumentMetadata[] currentData;
    public FilesPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        currentData = Main.getDocumentData();

        /// Upper panel
        this.add(makeUpperPanel(), constraints);

        constraints.gridy++;
        constraints.weighty = 1;
        panelList = new PanelList(BoxLayout.Y_AXIS);
        panelList.setSelectionEnabled(true);
        panelList.setCanCreate(false);
        panelList.setWidthFactor(0.8); // Set the widthFactor to 0.8 so the panels take up more of the width of the list
        addPanels(currentData);
        this.add(panelList, constraints);
    }
    private void addPanels(DocumentMetadata[] files) {
        for (DocumentMetadata data: files) {
            panelList.addPanel(new FileListItem(data));
        }
    }
    private DocumentMetadata getSelectedData() {
        FileListItem card = (FileListItem) panelList.getSelectedPanel();
        return card.getData();
    }
    private void openEditDocument() {
        DocumentMetadata data = getSelectedData();
        DocumentErrorPackage document = Main.makeDocumentFromData(data);
        // Check if opening the document failed and display an error if it did
        if (document.e().failed) {
            Main.showErrorDialog(document.e().getMessage());
        } else {
            // Otherwise, edit the document
            Main.getWindow().editDocument(document.document());
        }
    }
    private void openViewDocument() {
        DocumentMetadata data = getSelectedData();
        DocumentErrorPackage document = Main.makeDocumentFromData(data);
        // Check if opening the document failed and display an error if it did
        if (document.e().failed) {
            Main.showErrorDialog(document.e().getMessage());
        } else {
            Main.getWindow().viewDocument(document.document());
        }
    }
    private JPanel makeUpperPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = new Insets(3,3,3,3);

        /// Delete Button
        JButton deleteButton = new JButton(Main.strings.getString("fileDeleteButton"));

        panel.add(deleteButton, constraints);

        /// Rename button
        constraints.gridx++;
        JButton renameButton = new JButton(Main.strings.getString("fileRenameButton"));

        panel.add(renameButton, constraints);

        /// View Button
        constraints.gridx++;
        JButton viewButton = new JButton(Main.strings.getString("fileViewButton"));
        viewButton.addActionListener(e-> openViewDocument());
        viewButton.setFocusable(false);

        panel.add(viewButton, constraints);


        /// Edit Button
        constraints.gridx++;
        JButton editButton = new JButton(Main.strings.getString("fileEditButton"));
        editButton.addActionListener(e->openEditDocument());
        editButton.setFocusable(false);

        panel.add(editButton, constraints);

        /// Search bar
        // New line
        constraints.gridx = 0;
        constraints.gridy++;
        JTextField searchBar = new JTextField();
        panel.add(searchBar, constraints);

        /// Subject selector
        constraints.gridx++;
        subjectSelector = new JComboBox<>(makeSubjects());
        subjectSelector.addActionListener(e->filter());
        panel.add(subjectSelector, constraints);

        return panel;
    }
    private void filter() {
        String subject = (String) subjectSelector.getSelectedItem();
        // Check for all
        if (Objects.equals(subject, Main.strings.getString("subjectFilterAll"))) {
            panelList.removeAllPanels();
            addPanels(currentData);
        } else {
            DocumentMetadata[] data = Main.filter(currentData, subject);
            // Show the data
            panelList.removeAllPanels();
            addPanels(data);
        }

    }
    private String[] makeSubjects() {
        String[] windowSubjects = Main.getWindow().getSubjects();
        String[] subjects = new String[windowSubjects.length+1];
        subjects[0] = Main.strings.getString("subjectFilterAll");
        System.arraycopy(windowSubjects,0, subjects, 1, windowSubjects.length);
        return subjects;
    }
    public void refreshUI() {

    }
}