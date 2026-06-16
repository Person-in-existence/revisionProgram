package revisionprogram.components.tables;

import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;

import javax.swing.*;
import java.awt.*;

public class ScrollingTable extends JScrollPane {
    public Table table;

    public ScrollingTable(Object[][] data, Object[] headers, int[] widths, int numRowsHeight, boolean editable) {
        super();
        table = new Table(data, headers, editable);
        table.setWidths(widths);

        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredSize().width, (int) (table.getRowHeight()*numRowsHeight+table.getTableHeader().getPreferredSize().getHeight()+2)));

        this.getViewport().add(table);


        // Code from ScrollingPanel
        Dimension contentPreferredSize = table.getPreferredScrollableViewportSize();
        this.setBorder(null);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Scroll dimension including scroll bar
        Dimension scrollPreferredSize = new Dimension(contentPreferredSize.width + this.getVerticalScrollBar().getPreferredSize().width, contentPreferredSize.height);
        this.setMinimumSize(scrollPreferredSize);
        this.setPreferredSize(scrollPreferredSize);

        this.getVerticalScrollBar().setUnitIncrement(Main.scrollSpeed);
    }
}
