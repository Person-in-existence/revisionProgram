package revisionprogram;

import revisionprogram.components.tables.ScrollingTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class SettingsDialog extends JDialog {
    public static final String darkModeKey = "dark_mode";
    public static final String[] documentManagerHeaders = new String[] {Main.strings.getString("documentManagerSubjectHeader"), Main.strings.getString("documentManagerNoDocumentsHeader")};
    public static final int[] documentManagerWidths = new int[] {150,100};
    public static final int documentManagerNumRows = 5;
    private Object[][] documentManagerData;
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

        // Document manager

        JPanel documentManagerPanel = makeDocumentManagerPanel();
        panel.add(documentManagerPanel);
        updateTotalSize(documentManagerPanel);




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

        // Save the settings when the window is closed
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
    private JPanel makeDocumentManagerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();

        // Section title
        JLabel panelTitle = new JLabel(Main.strings.getString("documentManagerTitle"));
        panel.add(panelTitle, constraints);

        // Spacer
        constraints.gridy++;
        panel.add(new JPanel(), constraints);

        documentManagerData = Main.getAllSubjectsAndNumDocuments();

        constraints.gridy++;
        ScrollingTable subjectTable = new ScrollingTable(documentManagerData, documentManagerHeaders, documentManagerWidths, documentManagerNumRows, false);
        panel.add(subjectTable, constraints);

        // Spacer
        constraints.gridy++;
        panel.add(new JPanel(), constraints);


        // Button panel
        constraints.gridy++;
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonPanelConstraints = Main.makeConstraints();
        buttonPanelConstraints.insets = new Insets(0,5,0,5);


        // Delete button
        JButton deleteButton = new JButton(Main.strings.getString("documentManagerDeleteButton"));
        deleteButton.addActionListener(e-> {
            // Get the selected subject
            int index = subjectTable.table.getSelectedRow();
            // Check a row is actually selected
            if (index != -1) {
                String name = (String) documentManagerData[index][0];

                // Confirmation dialog
                String confirmMessage = Main.strings.getString("documentManagerDeleteSubjectConfirmMessagePart1")
                        + " " + name + Main.strings.getString("documentManagerDeleteSubjectConfirmMessagePart2")
                        + "\n" + Main.strings.getString("documentManagerDeleteSubjectConfirmMessagePart3");
                int choice = JOptionPane.showConfirmDialog(this, confirmMessage, "", JOptionPane.OK_CANCEL_OPTION);



                // Choice is 0 when "Yes" is selected
                if (choice == 0) {
                    Main.deleteSubject(name);
                    // Update the data
                    documentManagerData = Main.getAllSubjectsAndNumDocuments();
                    subjectTable.table.changeData(documentManagerData);
                }

            }

        });
        buttonPanel.add(deleteButton, buttonPanelConstraints);

        // // Rename button - not added
        // buttonPanelConstraints.gridx++;
        // JButton renameButton = new JButton(Main.strings.getString("documentManagerRenameButton"));
        // renameButton.addActionListener(e->{
        //     System.out.println("Temporary rename debug listener");
        // });
        // buttonPanel.add(renameButton, buttonPanelConstraints);

        // Add button
        buttonPanelConstraints.gridx++;
        JButton addButton = new JButton(Main.strings.getString("documentManagerAddButton"));
        addButton.addActionListener(e->{
            // Get the subject name
            String name = JOptionPane.showInputDialog(this, Main.strings.getString("timetableNewActivityDialogText"), Main.strings.getString("timetableNewActivityDialogTitle"), JOptionPane.INFORMATION_MESSAGE);

            // Check the user did not press cancel or input nothing
            if (name != null) {
                // Check if the name is in the subjects already
                boolean inList = false;
                for (Object[] datum : documentManagerData) {
                    String datumName = (String) datum[0];
                    if (Objects.equals(datumName, name)) {
                        inList = true;
                        System.out.println("Adding was in list!");
                        break;
                    }
                }
                if (!inList) {
                    // Add the subject
                    boolean success = Main.addSubject(name);
                    // Update the table
                    documentManagerData = Main.getAllSubjectsAndNumDocuments();
                    subjectTable.table.changeData(documentManagerData);
                    if (!success) {
                        JOptionPane.showMessageDialog(this, Main.strings.getString("documentManagerAddSubjectFailMessage"), Main.strings.getString("errorDialogTitle") ,JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, Main.strings.getString("documentManagerAddSubjectDuplicateMessage"), Main.strings.getString("errorDialogTitle"), JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        buttonPanel.add(addButton, buttonPanelConstraints);

        panel.add(buttonPanel, constraints);


        constraints.gridy++;



        return panel;
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
