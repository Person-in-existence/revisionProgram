package revisionprogram.components.tables;

import revisionprogram.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Table extends JTable {
    private DefaultTableModel tableModel;
    private Object[] headers;
    private boolean editable;
    public Table(Object[][] data, Object[] headers, boolean editable) {
        super();
        tableModel = new DefaultTableModel(data, headers);

        setModel(tableModel);

        // Store the headers for updates later
        this.headers = headers;

        // Returned by isCellEditable
        this.editable = editable;
        setFont(Main.textContentFont);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        getTableHeader().setVisible(true);
        getTableHeader().setReorderingAllowed(false);

    }
    public void setWidths(int[] widths) {
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            getColumnModel().getColumn(columnIndex).setMinWidth(widths[columnIndex]);
        }
    }
    public void changeData(Object[][] data) {
        tableModel = new DefaultTableModel(data, headers);
        setModel(tableModel);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return editable;
    }
}
