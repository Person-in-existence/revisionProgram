import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;


public class Window extends JFrame {
    public JPanel panel = new JPanel(new GridBagLayout());
    private final ContentPanel contentPanel;
    private JPanel topBarPanel;
    public Window() {
        super();

        /// REMOVE FOCUS IF EMPTY SPACE CLICKED
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
                System.out.println("Focus Lost");
            }
        });

        // Change the size of the window
        this.setSize(Main.windowWidth, Main.windowHeight);

        /// Make constraints
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        // Allows the panels to expand in both directions fully
        constraints.fill = GridBagConstraints.BOTH;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /// Make the top bar
        topBarPanel = makeTopPanel();
        // Add the top bar panel to the main panel
        panel.add(topBarPanel, constraints);
        // Increment constraints
        constraints.gridy++;
        /// Make a lower content panel to hold the rest of the window
        contentPanel = new ContentPanel(this);
        // Set the weighty of constraints to 1
        constraints.weighty = 1;
        // Add contentPanel to the main panel
        panel.add(contentPanel, constraints);


        /// Add the main panel to the window
        this.add(panel);
        // Go to the homepage
        goToHome();
        // Make the window visible
        this.setVisible(true);

        // Centre the window in the frame
        this.centreOnScreen();
    }

    public JPanel makeTopPanel() {
        // Make the top bar panel with a GridBagLayout
        JPanel topBarPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        // Add padding to the top bar panel
        topBarPanel.setBorder(new EmptyBorder(15,30,15,30));

        /// Add a button in the top left
        JButton homeButton = new JButton(Main.strings.getString("homeButton"));
        // Make button not focusable
        homeButton.setFocusable(false);
        // Add the action listener to the button
        homeButton.addActionListener(_->goToHome());
        // Add the button to the panel
        topBarPanel.add(homeButton, constraints);

        /// Add a filler panel
        constraints.weightx = 1;
        constraints.gridx++;
        topBarPanel.add(new JPanel(), constraints);
        constraints.weightx = 0;

        /// Add the settings button
        JButton settingsButton = new JButton("Settings");
        constraints.gridx++;
        settingsButton.addActionListener(_->{
            SettingsDialog settings = new SettingsDialog(this);
            settings.setVisible(true);
        });
        settingsButton.setFocusable(false);
        topBarPanel.add(settingsButton, constraints);

        /// Add a filler panel
        constraints.weightx = 1;
        constraints.gridx++;
        topBarPanel.add(new JPanel(), constraints);
        constraints.weightx = 0;

        /// Add a button in the top right
        constraints.gridx++;
        JButton newButton = new JButton(Main.strings.getString("createNewButton"));
        // Make button not focusable
        newButton.setFocusable(false);
        newButton.addActionListener(_ -> openCreateDialog());
        // Add the button to the panel
        topBarPanel.add(newButton, constraints);

        return topBarPanel;
    }
    // TODO: DEBUG
    public void openCreateDialog() {
        JDialog createDialog = new JDialog(this);
        // TODO: ADD DIALOG PADDING
        createDialog.setSize(Main.dialogWidth, Main.dialogHeight);

        // Make the panel to add stuff to
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        // Make the constraints for the panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;

        // Make a drop-down box
        JComboBox<String> comboBox = new JComboBox<>(Main.docTypes);
        dialogPanel.add(comboBox, constraints);
        // Increment gridy by 1
        constraints.gridy++;

        // Make a button to create
        JButton submitButton = new JButton(Main.strings.getString("submitCreateDialog"));
        submitButton.addActionListener(_->{
            // Call the function
            makeNewDocument(Main.getTypeFromIndex(comboBox.getSelectedIndex()));
            // Close the dialog
            createDialog.dispose();
        });
        // Add button to panel
        dialogPanel.add(submitButton, constraints);

        // Add the panel to the dialog
        createDialog.add(dialogPanel);

        /// Centre the dialog
        // Get the centre point
        Point centrePoint = getCentredWindowPoint(this.getX(), this.getY(), this.getWidth(), this.getHeight(), Main.dialogWidth, Main.dialogHeight);
        // Centre the dialog
        createDialog.setLocation(centrePoint);
        // Show the dialog
        createDialog.setVisible(true);
    }
    public void makeNewDocument(DocumentType documentType) {
        // First, check it is OK to close the current window, running close systems
        if (contentPanel.closePanel()) {
            // Make the document
            Document document = Document.makeFromType(documentType);
            // Show it
            contentPanel.editDocument(document);
        }
        // If not, don't do anything

    }

    public void editDocument(Document document) {
        if (contentPanel.closePanel()) {
            contentPanel.editDocument(document);
        }
    }
    public void viewDocument(Document document) {
        if (contentPanel.closePanel()) {
            contentPanel.viewDocument(document);
        }
    }
    public void goToHome() {
        System.out.println("Going to homepage");
        // Check that it is OK to go to home
        if (contentPanel.closePanel()) {
            contentPanel.showHomepage();
        }// Otherwise, do nothing
    }
    private void centreOnScreen() {
        Point p = getCentredWindowPoint(0,0, Main.screenWidth, Main.screenHeight, Main.windowWidth, Main.windowHeight);
        // Move the window
        this.setLocation(p);
    }
    public static Point getCentredWindowPoint(int topX, int topY, int backWidth, int backHeight, int width, int height) {
        // Get the window midpoint (assuming 0,0 start)
        int centreWidth = backWidth/2;
        int centreHeight = backHeight/2;
        // Adjust for the new window size
        centreWidth -= width/2;
        centreHeight -= height/2;
        // Adjust for window start
        int pointX = centreWidth + topX;
        int pointY = centreHeight + topY;
        // Return it as a point
        return new Point(pointX, pointY);
    }
    public void refresh() {
        FlatLaf.updateUI();
        contentPanel.refresh();
    }
}
