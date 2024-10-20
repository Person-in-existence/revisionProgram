import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {
    JPanel panel;
    public ContentPanel() {
        super(new GridLayout(1,1));
    }

    public void showDocument(Document document) {
        switchPanel(document.makePanel());
    }

    public void showHomepage() {
        System.out.println("ContentPanel switching to homepage");
        switchPanel(new ContentHomePage());
    }
    private void switchPanel(JPanel newPanel) {
        this.removeAll();
        panel = newPanel;

        this.add(newPanel);

        this.revalidate();
        this.repaint();
    }
}
