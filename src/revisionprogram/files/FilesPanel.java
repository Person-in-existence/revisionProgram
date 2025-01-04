package revisionprogram.files;

import revisionprogram.DocumentErrorPackage;
import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FilesPanel extends JPanel {
    private JPanel fileScrollPanel;
    private DocumentMetadata[] data;
    private int dataIndex = -1;
    private final Window parent;
    public FilesPanel(Window parent) {
        super(new GridBagLayout());
        this.parent = parent;
        make();

    }
    private JPanel makeButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0,5,0,5);

        /// Refresh button
        JButton refreshButton = new JButton(Main.strings.getString("fileRefreshButton"));
        // Stop it being focusable so that it doesn't remove focus when pressed
        refreshButton.setFocusable(false);
        refreshButton.addActionListener(e-> refreshFiles());
        // Add the button to the panel
        panel.add(refreshButton, constraints);

        /// View button
        JButton viewButton = new JButton(Main.strings.getString("fileViewButton"));
        // Stop it being focusable so that it doesn't remove focus when pressed
        viewButton.setFocusable(false);
        viewButton.addActionListener(e->openViewDocument());
        // Increment constraints
        constraints.gridx++;
        // Add the button to the panel
        panel.add(viewButton, constraints);

        /// Edit button
        JButton editButton = new JButton(Main.strings.getString("fileEditButton"));
        // Stop it being focusable so that it doesn't remove focus when pressed
        editButton.setFocusable(false);
        editButton.addActionListener(e->openEditDocument());
        // Increment constraints
        constraints.gridx++;
        // Add the button to the panel
        panel.add(editButton);

        return panel;
    }

    private JPanel makeFileListPanel() {
        JPanel panel = new JPanel(new GridLayout(1,1));

        /// Add a JScrollPane
        JPanel scrollPane = new JPanel(new GridLayout(1,1));
        fileScrollPanel = scrollPane;
        // Make the table for the files
        makeFileTable();
        panel.add(scrollPane);

        return panel;
    }
    public void refreshUI() {
        this.removeAll();
        this.make();
    }
    public void refreshFiles() {
        this.remove(fileScrollPanel);
        makeFileListPanel();
    }
    private void makeFileTable() {
        // Get all the available files
        DocumentMetadata[] files = Main.getDocumentData();
        data = files;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Keep a total of the height required so that
        int totalHeight = 0;
        int largestWidth = 0;
        for (int index = 0; index < files.length; index++) {
            FileListItem item = new FileListItem(files[index]);
            item.setFocusable(true);
            int finalIndex = index;

            // Add a mouse listener, to transfer focus when clicked.
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    item.requestFocus();
                }
            });
            item.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    dataIndex = finalIndex;
                    System.out.println("Focus");
                    item.highlight();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (!panel.isAncestorOf(e.getOppositeComponent())) {
                        dataIndex = -1;
                    }
                    item.unhighlight();
                }
            });
            panel.add(item);
            totalHeight += (int) item.getPreferredSize().getHeight();
            if (largestWidth < item.getPreferredSize().width) {
                largestWidth = item.getPreferredSize().width;
            }
            // Add spacing
            final int verticalSpace = 5;
            totalHeight += verticalSpace;
            panel.add(Box.createVerticalStrut(verticalSpace));
        }
        // Add padding to the border
        final int padSize = 5;
        totalHeight += padSize*2;
        largestWidth += padSize*2;
        panel.setBorder(new EmptyBorder(padSize,padSize,padSize, padSize));
        // Set the preferred size of the panel
        Dimension preferredSize = new Dimension(largestWidth, totalHeight);
        fileScrollPanel.setPreferredSize(preferredSize);
        fileScrollPanel.requestFocusInWindow();

        // Add it to the panel
        fileScrollPanel.removeAll();
        JScrollPane scrollPane = makeScrollPane(panel, preferredSize);
        fileScrollPanel.add(scrollPane);
    }

    public static JScrollPane makeScrollPane(JPanel content, Dimension contentPreferredSize) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Scroll dimension including scroll bar
        Dimension scrollPreferredSize = new Dimension(contentPreferredSize.width + scrollPane.getVerticalScrollBar().getPreferredSize().width, contentPreferredSize.height);
        scrollPane.setMinimumSize(scrollPreferredSize);
        return scrollPane;
    }
    public DocumentMetadata getSelectedData() {
        if (dataIndex != -1 & dataIndex < data.length) {
            return data[dataIndex];
        } else {
            System.err.println("No focused component");
            return null;
        }
    }
    private void openEditDocument() {
        DocumentMetadata data = getSelectedData();
        DocumentErrorPackage document = Main.makeDocumentFromData(data);
        if (document.e().failed) {
            // display an error message
            JOptionPane.showMessageDialog(parent, document.e().getMessage(), Main.strings.getString("fileErrorTitle"), JOptionPane.ERROR_MESSAGE);
        } else {
            parent.editDocument(document.document());
        }
    }
    private void openViewDocument() {
        DocumentMetadata data = getSelectedData();
        DocumentErrorPackage document = Main.makeDocumentFromData(data);
        if (document.e().failed) {
            // display an error message
            JOptionPane.showMessageDialog(parent, document.e().getMessage(), Main.strings.getString("fileErrorTitle"), JOptionPane.ERROR_MESSAGE);
        } else {
            parent.viewDocument(document.document());
        }
    }
    private void make() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Add the button panel
        this.add(makeButtonPanel(), constraints);
        // Add a padding panel to separate the button panel from the file scroll panel
        constraints.gridy++;
        this.add(new JPanel(), constraints);

        // Change constraints
        constraints.weighty = 1;
        constraints.gridy++;
        // Add the file scroll panel
        this.add(makeFileListPanel(), constraints);
    }
}
