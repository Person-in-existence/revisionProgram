import javax.swing.*;
import java.awt.*;

public class ScrollingPanel extends JScrollPane {
    public ScrollingPanel(JPanel content) {
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
}
