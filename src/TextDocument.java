import javax.swing.*;
import java.awt.*;

public class TextDocument extends Document{

    @Override
    public JPanel makePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.CYAN);
        JLabel label = new JLabel("Insert Document Here");
        label.setForeground(Color.BLACK);
        label.setFont(new Font(null, Font.PLAIN, 50));
        panel.add(label);

        return panel;
    }

    @Override
    public void writeToFile(String filePath) {

    }

    @Override
    public void readFromFile(String filePath) {

    }
}
