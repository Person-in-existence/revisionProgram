import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsDialog extends JDialog {
    public static final String darkModeKey = "dark_mode";
    private boolean darkModeEnabled;
    public SettingsDialog(Window window) {
        super();
        // Make it so the lower panels cannot be clicked on while this is open
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        Settings settings = new Settings();
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
        JCheckBox darkModeCheckBox = new JCheckBox();
        // Get the setting preference
        if (settings.darkMode) {
            darkModeCheckBox.setSelected(true);
        }
        // Add action listener
        darkModeCheckBox.addActionListener(_->{
            System.out.println("Checked");
            if (darkModeCheckBox.isSelected()) {
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
            // Change the setting
        });
        darkModePanel.add(darkModeCheckBox, BorderLayout.LINE_END);
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

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                boolean darkMode = darkModeCheckBox.isSelected();

                Settings currentSettings = new Settings(darkMode);
                currentSettings.save();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
