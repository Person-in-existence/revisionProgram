package revisionprogram.components.panellist;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import revisionprogram.Main;
import revisionprogram.components.ScrollingPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class PanelList extends JPanel {
    private JPanel contentPanel;
    private ArrayList<ListCard> panels = new ArrayList<>();
    private JButton createButton;
    private ScrollingPanel scrollingPanel;
    private boolean canCreate = true;
    private int selectedIndex = -1;
    private boolean selectionEnabled = false;
    private double widthFactor = 0.66;
    public PanelList(int axis) {
        super();

        // Make the content panel with a box layout
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, axis));

        // Add a button to add a new panel
        createButton = new JButton(Main.strings.getString("panelListCreateButtonDefault"));
        contentPanel.add(createButton);
        contentPanel.add(Box.createVerticalStrut(8)); // Make sure there is a bit of padding so the button isn't cut off

        // Make a scrolling panel to put the content panel in
        scrollingPanel = new ScrollingPanel(contentPanel);
        this.add(scrollingPanel);


        // Add a component listener so that we can resize all the contained panels
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(()->{
                    resizeAll();
                    scrollingPanel.setHeight(getHeight());
                    // Fix it not being there at start?
                    Dimension size = getSize();
                    // REMOVING 10 SEEMS TO FIX THE RANDOM SCROLLING ISSUE?
                    size.width -= 10;
                    size.height -= 10;
                    scrollingPanel.setPreferredSize(size);

                    revalidate();
                    repaint();
                });

            }
        });
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension(contentPanel.getPreferredSize().width, contentPanel.getPreferredSize().height + 10);
    }



    public void setCanCreate(boolean canCreate) {
        // Only do something if there is a change
        if (this.canCreate != canCreate) {
            if (canCreate) {
                contentPanel.add(createButton);
            } else {
                contentPanel.remove(createButton);
            }
        }
        this.canCreate = canCreate;
    }

    public void resizeAll() {
        int width = this.getSize().width;
        for (ListCard card: panels) {
            card.resize((int)(width*widthFactor));
        }
    }
    public void setWidthFactor(double widthFactor) {
        this.widthFactor = widthFactor;
    }

    public void setCreateButtonText(String text) {
        createButton.setText(text);
    }

    public void addCreateButtonListener(ActionListener actionListener) {
        createButton.addActionListener(actionListener);
    }
    public ArrayList<ListCard> getPanels() {
        return new ArrayList<>(panels); // Return it as a new arraylist so you can't accidentally remove a panel
    }

    public void update() {
        this.revalidate();
        this.repaint();
    }

    public void addPanel(ListCard panel) {
        addWithoutUpdate(panel);
        update();

    }
    public void addWithoutUpdate(ListCard panel) {
        // Check if the createButton is in the panel, and change the index based on that
        if (canCreate) {
            contentPanel.add(panel, panels.size()); // Insert it at panels.size() - we haven't yet added the panel to panels
        } else {
            contentPanel.add(panel); // Just insert it - it will be at the end.
        }
        panels.add(panel);
        panel.setParent(this);
        scrollingPanel.setPreferredSize(this.getSize());

    }

    public void removePanel(ListCard panel) {
        panels.remove(panel);
        contentPanel.remove(panel);
        panel.removeParent();
        this.revalidate();
        this.repaint();

    }
    public void removeAllPanels() {
        // Remove parents
        for (ListCard card: panels) {
            card.removeParent();
        }
        panels = new ArrayList<>();
        contentPanel.removeAll();
        this.revalidate();
        this.repaint();

    }

    public int numPanels() {
        return panels.size();
    }

    protected void setSelectedPanel(ListCard listCard) {
        if (selectionEnabled) {
            if (listCard != null) {
                selectedIndex = panels.indexOf(listCard);
                listCard.highlight();
            } else {
                // Unhighlight the currently selected panel
                if (selectedIndex != -1) {
                    panels.get(selectedIndex).unhighlight();
                }
                // listCard was null, set the selected index to -1 as no card is selected
                selectedIndex = -1;
            }
        }
    }

    public interface CreateListener {
        void create();
    }

    public void scrollToPanel(ListCard card) {
        Rectangle bounds = card.getBounds();
        contentPanel.scrollRectToVisible(bounds);
    }
    public ListCard panelAt(int index) {
        return panels.get(index);
    }
    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    public ListCard getSelectedPanel() {
        return panels.get(selectedIndex);
    }

}
