import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class Window extends JFrame {
    JPanel panel = new JPanel(new GridBagLayout());
    ContentPanel contentPanel;
    public Window() {
        super();
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
        JPanel topBarPanel = makeTopPanel();
        // Add the top bar panel to the main panel
        panel.add(topBarPanel, constraints);
        // Increment constraints
        constraints.gridy++;
        /// Make a lower content panel to hold the rest of the window
        contentPanel = new ContentPanel();
        // Set the weighty of constraints to 1
        constraints.weighty = 1;
        // Add contentPanel to the main panel
        panel.add(contentPanel, constraints);


        /// Add the main panel to the window
        this.add(panel);
        // Make the window visible
        this.setVisible(true);

        // Centre the window in the frame
        this.centreOnScreen();
    }

    public JPanel makeTopPanel() {
        // Make the top bar panel with a borderLayout, as it doesn't need a full GridBagLayout
        JPanel topBarPanel = new JPanel(new BorderLayout());
        // Add padding to the top bar panel
        topBarPanel.setBorder(new EmptyBorder(15,30,15,30));

        /// Add a button in the top left
        // TODO: EXTRACT STRING RESOURCE
        JButton homeButton = new JButton("Home");
        // Add the action listener to the button
        homeButton.addActionListener(_->goToHome());
        // Add the button to the panel
        topBarPanel.add(homeButton, BorderLayout.LINE_START);

        /// Add a button in the top right
        // TODO: EXTRACT STRING RESOURCE
        JButton newButton = new JButton("Create new");
        newButton.addActionListener(_ -> {
            openCreateDialog();
        });
        // Add the button to the panel
        topBarPanel.add(newButton, BorderLayout.LINE_END);

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

    }
    public void goToHome() {
        System.out.println("Going to homepage");
        contentPanel.showHomepage();
    }
    private void centreOnScreen() {
        Point p = getCentredWindowPoint(0,0, Main.screenWidth, Main.screenHeight, Main.windowWidth, Main.windowHeight);
        // Move the window
        this.setLocation(p);
    }
    private static Point getCentredWindowPoint(int topX, int topY, int backWidth, int backHeight, int width, int height) {
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

}
