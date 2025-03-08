package revisionprogram;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsDialog extends JDialog {
    public static final String darkModeKey = "dark_mode";
    private boolean darkModeEnabled;
    private static final int vPadding = 50;
    private static final int hPadding = 50;
    private int totalWidth = hPadding*3;
    private int totalHeight = vPadding*3;
    public SettingsDialog(Window window) {
        super();
        // Make it so the lower panels cannot be clicked on while this is open
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        Settings settings = new Settings();
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
        darkModeCheckBox.addActionListener(e->{
            darkModeToggle(darkModeCheckBox);
        });
        darkModePanel.add(darkModeCheckBox, BorderLayout.LINE_END);
        panel.add(darkModePanel);
        updateTotalSize(darkModePanel);



        // BOTTOM!!!
        // License view
        JPanel licensePanel = new JPanel();
        JButton licenseViewButton = new JButton(Main.strings.getString("settingLicenseView"));
        licenseViewButton.setFocusable(false);
        licenseViewButton.addActionListener(e->{
            Main.showLicenseDialog(false);
        });
        licensePanel.add(licenseViewButton);
        panel.add(licensePanel);
        updateTotalSize(panel);


        this.setSize(new Dimension(totalWidth, totalHeight));
        // Set the title
        this.setTitle(Main.strings.getString("settingTitle"));

        // Centre the dialog in the window
        Point windowLocation = window.getLocation();
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        Point centredPoint = Window.getCentredWindowPoint(windowLocation.x, windowLocation.y, windowWidth, windowHeight, totalWidth, totalHeight);
        this.setLocation(centredPoint);
        // Set this to dispose when closed
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boolean darkMode = darkModeCheckBox.isSelected();

                Settings currentSettings = new Settings(darkMode);
                currentSettings.save();
                window.closeSettings();
            }
        });
    }
    private void darkModeToggle(JCheckBox darkModeCheckBox) {
        System.out.println("Checked");
        Window window = Main.getWindow();
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
    }
    private void updateTotalSize(Component component) {
        totalHeight += component.getPreferredSize().height;

        // Only update width if it is larger than totalwidth
        int paddedWidth = component.getPreferredSize().width + 50;
        if (paddedWidth > totalWidth) {
            totalWidth = paddedWidth;
        }
    }
}
