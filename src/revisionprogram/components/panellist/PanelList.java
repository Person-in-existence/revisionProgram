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

public class PanelList extends JPanel {
    private JPanel contentPanel;
    private ArrayList<CreateListener> createButtonListeners = new ArrayList<>();
    private ArrayList<ListCard> panels = new ArrayList<>();
    private JButton createButton;
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
        ScrollingPanel scrollingPanel = new ScrollingPanel(contentPanel);
        this.add(scrollingPanel);

        // Add a component listener so that we can resize all the contained panels
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeAll();
                scrollingPanel.setHeight(getHeight());
            }
        });
    }

    private void resizeAll() {
        int width = this.getWidth();
        for (ListCard card: panels) {
            card.resize((int)(width*0.66));
        }
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

    public void addPanel(ListCard panel) {
        contentPanel.add(panel, panels.size()); // Insert it at panels.size() - we haven't yet added the panel to panels
        panels.add(panel);
        panel.setParent(this);
        this.revalidate();
        this.repaint();
    }

    public void removePanel(ListCard panel) {
        panels.remove(panel);
        contentPanel.remove(panel);
        panel.removeParent();
        this.revalidate();
        this.repaint();
    }



    public interface CreateListener {
        void create();
    }

}
