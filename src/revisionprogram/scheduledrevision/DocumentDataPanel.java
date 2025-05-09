package revisionprogram.scheduledrevision;

import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;
import revisionprogram.documents.Document;
import revisionprogram.files.FileException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class DocumentDataPanel extends JPanel {
    public final DocumentMetadata[] data;
    public static Object[] headers = new Object[] {Main.strings.getString("scheduledRevisionTitleHeader"), Main.strings.getString("scheduledRevisionLastRevisedHeader"), Main.strings.getString("scheduledRevisionNextRevisionHeader")};
    public static final int[] widths = {250, 100, 130}; // Widths of the table columns
    public DocumentDataPanel(DocumentMetadata[] data) {
        super(new GridBagLayout());
        this.data = sortData(data);

        // Convert the DocumentMetadata[] to data that can be given to the table
        Object[][] tableData = new Object[this.data.length][headers.length];
        for (int index = 0; index < this.data.length; index++) {
            tableData[index] = dataToObject(this.data[index]);
        }

        DefaultTableModel model = new DefaultTableModel(tableData, headers);
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing
                return false;
            }
        };
        table.setFont(Main.textContentFont);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setVisible(true);
        // Stop the user from reordering the headers
        table.getTableHeader().setReorderingAllowed(false);
        resizeTable(table);

        // Add a listener to the table to open the document when someone double-clicks on it
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SwingUtilities.invokeLater(()->{
                        int row = table.getSelectedRow();
                        // Open that document
                        openDocumentAtRow(row);
                    });
                }
            }
        });

        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredSize().width, table.getRowHeight()*5));

        // Make a scrolling panel for the contentPanel to go in
        ScrollingPanel scrollPanel = new ScrollingPanel(table);
        // Add the scrolling panel to this
        this.add(scrollPanel);

    }
    private Object[] dataToObject(DocumentMetadata documentMetadata) {
        return new Object[] {documentMetadata.title(), Main.getUserStyleDateString(documentMetadata.lastRevised()), Main.getUserStyleDateString(documentMetadata.nextRevision())};
    }
    private DocumentMetadata[] sortData(DocumentMetadata[] data) {
        ArrayList<DocumentMetadata> sorted = new ArrayList<>();
        for (DocumentMetadata documentMetadata : data) {
            LocalDate thisDate = documentMetadata.nextRevision();
            boolean added = false;
            for (int sortedIndex = 0; sortedIndex < sorted.size(); sortedIndex++) {
                LocalDate sortedDate = sorted.get(sortedIndex).nextRevision();
                if (thisDate.isBefore(sortedDate)) {
                    sorted.add(sortedIndex, documentMetadata);
                    added = true;
                    break;
                }
                // If they are the same, do it by alphabetical order of name
                if (ChronoUnit.DAYS.between(thisDate, sortedDate) == 0) {
                    // Will be before (added) if result is LESS than sorted
                    if (documentMetadata.name().compareTo(sorted.get(sortedIndex).name()) <= 0) {
                        sorted.add(sortedIndex, documentMetadata);
                        added = true;
                        break;
                    }
                }
            }
            // Add it anyway if it didn't get added in the list
            if (!added) {
                sorted.add(documentMetadata);
            }
        }
        return sorted.toArray(new DocumentMetadata[0]);
    }
    private static void resizeTable(JTable table) {
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setMinWidth(widths[columnIndex]);
        }
    }
    private void openDocumentAtRow(int row) {
        Document doc = Document.makeFromType(data[row].documentType());
        FileException e = doc.readFromFile(data[row].name());
        if (e.failed) {
            System.err.println("Failed to open document: " + e.getMessage());
            Main.showErrorDialog(e.getMessage());
        } else {
            Main.getWindow().viewDocument(doc);
        }
    }
}
