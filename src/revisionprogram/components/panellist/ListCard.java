package revisionprogram.components.panellist;

import javax.swing.*;
import java.awt.*;

public abstract class ListCard extends JPanel {
    private PanelList parent;
    public ListCard() {super();}
    public ListCard(LayoutManager layoutManager) {super(layoutManager);}
    public void resize(int width) {
        Dimension size = new Dimension(width, (int) (width*1.618/6));
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.revalidate();
        this.repaint();
        onResize(width);
    }
    public void onResize(int width) {}

    protected void setParent(PanelList parent) {
        // If parent isn't null, we must have tried to give this two parents - raise an exception
        if (this.parent != null) {
            throw new RuntimeException("Tried to give a ListCard two parents");
        }
        this.parent = parent;
    }
    protected void removeParent() {
        this.parent = null;
    }
    protected PanelList getPanelList() {
        return parent;
    }
    protected void delete() {
        parent.removePanel(this);
    }
}
