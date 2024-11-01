import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {
    private MainPanel panel;
    private boolean panelHasClosed;
    private Window window;
    public ContentPanel(Window window) {
        super(new GridLayout(1,1));
        this.window = window;
    }

    public void editDocument(Document document) {
        switchPanel(document.makeEditPanel());
    }
    public void viewDocument(Document document) {
        switchPanel(document.makeViewPanel());
    }

    public void showHomepage() {
        System.out.println("ContentPanel switching to homepage");
        switchPanel(new ContentHomePage(window));
    }
    public void declarePanelOpen() {
        panelHasClosed = false;
    }
    private void switchPanel(MainPanel newPanel) {
        // If the panel has not closed successfully but is being switched anyway, try and force the panel to close.
        if (!panelHasClosed & panel != null) {
            panel.close();
        }
        this.removeAll();
        panel = newPanel;

        this.add(newPanel);

        this.revalidate();
        this.repaint();
    }
    public boolean closePanel() {
        if (panel == null) {
            return true;
        }
        boolean closed = panel.close();
        if (closed) {
            panelHasClosed = true;
        }
        return closed;
    }
}
