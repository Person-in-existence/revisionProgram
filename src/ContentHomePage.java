import javax.swing.*;
import java.awt.*;

public class ContentHomePage extends JPanel {
    public ContentHomePage() {
        super();
        this.setBackground(Color.RED);
        System.out.println("Hi");
        JLabel label = new JLabel("Insert Home Page Here");
        label.setForeground(Color.BLACK);
        label.setFont(new Font(null, Font.PLAIN, 50));
        this.add(label);
    }
}
