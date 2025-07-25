package revisionprogram.files;

import revisionprogram.TriggerListener;
import revisionprogram.DocumentErrorPackage;
import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.components.panellist.PanelList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

public class FilesPanel extends JPanel {
    private JComboBox<String> subjectSelector;
    private JTextField searchBar;
    private PanelList panelList;
    private DocumentMetadata[] currentData;
    private DocumentMetadata[] filteredData;
    private final ArrayList<TriggerListener> deleteListeners = new ArrayList<>();
    public FilesPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        currentData = Main.getDocumentData();
        filteredData = currentData;

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
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setPreferredSize(new Dimension(getPreferredSize().width, getParent().getHeight()-50));
            }
        });
    }
    public void addDeleteListener(TriggerListener listener) {
        deleteListeners.add(listener);
    }
    public void removeDeleteListener(TriggerListener listener) {
        deleteListeners.remove(listener);
    }
    private void fireDeleteListeners() {
        for (TriggerListener listener: deleteListeners) {
            listener.fire();
        }
    }
    private void addPanels(DocumentMetadata[] files) {
        for (DocumentMetadata data: files) {
            panelList.addWithoutUpdate(new FileListItem(data));
        }
        panelList.update();
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
        deleteButton.setFocusable(false);
        deleteButton.addActionListener(e->delete());

        panel.add(deleteButton, constraints);

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
        searchBar = new JTextField();

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(()->update()); // Use invokeLater to allow the text to be processed first
                // Otherwise, we update before anything changes.
            }
        });

        panel.add(searchBar, constraints);

        /// Subject selector
        constraints.gridx++;
        subjectSelector = new JComboBox<>(makeSubjects());
        subjectSelector.addActionListener(e->update());
        panel.add(subjectSelector, constraints);

        return panel;
    }

    private void delete() {
        DocumentMetadata data = getSelectedData();
        String confirmMessage = Main.strings.getString("confirmDeleteMessage") + " " + data.title();
        // Confirm delete
        if (JOptionPane.showConfirmDialog(Main.getWindow(), confirmMessage, Main.strings.getString("confirmDeleteTitle"), JOptionPane.YES_NO_OPTION) == 0) {
            FileException e = Main.removeFile(data);
            if (e.failed) {
                // Show an error dialog
                Main.showErrorDialog(e.getMessage());
            }
            // Update the panel
            // Get the updated document data from main
            currentData = Main.getDocumentData();
            update();
            fireDeleteListeners();
        }

    }

    private void update() {
        filter();
        DocumentMetadata[] search = search(searchBar.getText());

        // Switch out the panels
        panelList.removeAllPanels();
        addPanels(search);
    }

    private boolean withinCaseInsensitive(DocumentMetadata data, String searchTerm) {
        // Convert to upper case to make case insensitive
        String upperTitle = data.title().toUpperCase();
        String upperSearchTerm = searchTerm.toUpperCase();
        return upperTitle.contains(upperSearchTerm);
    }

    private DocumentMetadata[] search(String searchTerm) {
        // If no search term, then just return filtered
        if (Objects.equals(searchTerm, "")) {
            return filteredData;
        } else {
            // Go through the list. If an item contains the searchTerm, add it to the arraylist
            ArrayList<DocumentMetadata> items = new ArrayList<>();
            for (DocumentMetadata item: filteredData) {
                if (withinCaseInsensitive(item, searchTerm)) {
                    items.add(item);
                }
            }
            return items.toArray(new DocumentMetadata[0]);
        }

    }

    private void filter() {
        String subject = (String) subjectSelector.getSelectedItem();
        // Check for all
        if (Objects.equals(subject, Main.strings.getString("subjectFilterAll"))) {
            filteredData = currentData;

        } else {
            filteredData = Main.filter(currentData, subject);
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