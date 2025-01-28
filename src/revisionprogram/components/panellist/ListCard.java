package revisionprogram.components.panellist;

import revisionprogram.Borders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class ListCard extends JPanel {
    private PanelList parent;
    private boolean expands = true;
    public ListCard() {
        super();
        this.setBorder(Borders.defaultBorder());
        addFocusListener();
        addMouseListener();
    }
    public void highlight() {
        this.setBorder(Borders.highlightedBorder());
    }
    public void unhighlight() {
        this.setBorder(Borders.defaultBorder());
    }
    @Override
    public Component add(Component c) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        super.add(c);
        return c;
    }
    public ListCard(LayoutManager layoutManager) {
        super(layoutManager);
        this.setBorder(Borders.defaultBorder());
        addFocusListener();
        addMouseListener();
    }
    private void addMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }
    private void addFocusListener() {
        ListCard thisReference = this;
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (parent != null) {
                    parent.setSelectedPanel(thisReference);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (parent != null) {
                    parent.setSelectedPanel(null);
                }
            }
        });
    }
    public void setExpandToFit(boolean expands) {
        this.expands = expands;
    }
    public void resize(int width) {
        Dimension preferredSize = getPreferredSize();
        Dimension size = new Dimension(width, (int) (width*1.618/6));
        // Check that the height of the content does not need to be taller to fit it
        if (expands) {
            size = new Dimension(Math.max(size.width, preferredSize.width), Math.max(size.height, preferredSize.height));
        }
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
