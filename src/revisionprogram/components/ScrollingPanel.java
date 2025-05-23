package revisionprogram.components;

import revisionprogram.Main;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ScrollingPanel extends JScrollPane {
    private int height;
    private boolean doWidth = false;
    public ScrollingPanel(Component content) {
        super(content);
        Dimension contentPreferredSize = content.getPreferredSize();
        this.setBorder(null);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Scroll dimension including scroll bar
        Dimension scrollPreferredSize = new Dimension(contentPreferredSize.width + this.getVerticalScrollBar().getPreferredSize().width, contentPreferredSize.height);
        this.setMinimumSize(scrollPreferredSize);
        this.getVerticalScrollBar().setUnitIncrement(Main.scrollSpeed);
    }
    public void refresh() {
        this.setBorder(null);
    }
    public void setHeight(int height) {
        this.height = height;
        doWidth = true;
    }
}
