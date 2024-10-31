import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {
    private MainPanel panel;
    private Window window;
    public ContentPanel(Window window) {
        super(new GridLayout(1,1));
        this.window = window;
    }

    public void showDocument(Document document) {
        switchPanel(document.makeEditPanel());
    }

    public void showHomepage() {
        System.out.println("ContentPanel switching to homepage");
        switchPanel(new ContentHomePage(window));
    }
    private void switchPanel(MainPanel newPanel) {
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
        return panel.close();
    }
}
