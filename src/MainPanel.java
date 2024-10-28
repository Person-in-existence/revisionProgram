import javax.swing.*;
import java.awt.*;

public abstract class MainPanel extends JPanel {
    public MainPanel() {
        super();
    }
    public MainPanel(LayoutManager layout) {
        super(layout);
    }
    public abstract boolean close();
}
