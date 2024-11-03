import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsDialog extends JDialog {
    public SettingsDialog(Window window) {
        super();
        final int vPadding = 50;
        final int hPadding = 50;
        int totalWidth = hPadding*3;
        int totalHeight = vPadding*3;
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(vPadding,hPadding,vPadding,hPadding));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this.add(panel);

        /// Setting elements

        // Dark mode toggle
        JPanel darkModePanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(Main.strings.getString("settingDarkMode"));
        darkModePanel.add(label, BorderLayout.LINE_START);
        JCheckBox checkBox = new JCheckBox();
        // Add action listener
        checkBox.addActionListener(_->{
            System.out.println("Checked");
            if (checkBox.isSelected()) {
                System.out.println("Selected");
                for (int i = 0; i < 3; i++) {
                    if (Main.setDarkMode()) {
                        window.refresh();
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    if (Main.setLightMode()) {
                        window.refresh();
                        break;
                    }
                }
            }
        });
        // TODO: SAVE PREFERENCES FOR SETTINGS
        darkModePanel.add(checkBox, BorderLayout.LINE_END);
        panel.add(darkModePanel);
        totalWidth += darkModePanel.getPreferredSize().width;
        totalHeight += darkModePanel.getPreferredSize().height;


        this.setSize(new Dimension(totalWidth, totalHeight));
        // Set the title
        this.setTitle(Main.strings.getString("settingTitle"));
        
        // Centre the dialog in the window
        Point windowLocation = window.getLocation();
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        Point centredPoint = Window.getCentredWindowPoint(windowLocation.x, windowLocation.y, windowWidth, windowHeight, totalWidth, totalHeight);
        this.setLocation(centredPoint);
    }
}
