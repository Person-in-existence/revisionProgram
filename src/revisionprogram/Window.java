package revisionprogram;

import com.formdev.flatlaf.FlatLaf;
import revisionprogram.documents.Document;
import revisionprogram.documents.DocumentType;
import revisionprogram.files.FileException;
import revisionprogram.timetable.Timetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;


public class Window extends JFrame {
    public JPanel panel = new JPanel(new GridBagLayout());
    private final ContentPanel contentPanel;
    private JPanel topBarPanel;
    private boolean settingsOpen = false;
    private Timetable timetable;
    public Window() {
        super();

        // Set the window so everything has access to it
        Main.setWindow(this);

        /// REMOVE FOCUS IF EMPTY SPACE CLICKED
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
                System.out.println("Focus Lost");
            }
        });

        // get the timetable
        this.timetable = new Timetable();
        FileException e = timetable.readFromFile();
        if (e.failed) {
            System.err.println("Failed to read timetable on start: " + e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

        // Change the size of the window
        this.setSize(Main.windowWidth, Main.windowHeight);

        /// Make constraints
        GridBagConstraints constraints = Main.makeConstraints();
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
        GridBagConstraints constraints = Main.makeConstraints();
        // Add padding to the top bar panel
        topBarPanel.setBorder(new EmptyBorder(15,30,15,30));

        /// Add a button in the top left
        JButton homeButton = new JButton(Main.strings.getString("homeButton"));
        // Make button not focusable
        homeButton.setFocusable(false);
        // Add the action listener to the button
        homeButton.addActionListener(e->goToHome());
        // Add the button to the panel
        topBarPanel.add(homeButton, constraints);

        /// Add a filler panel
        constraints.weightx = 1;
        constraints.gridx++;
        topBarPanel.add(new JPanel(), constraints);
        constraints.weightx = 0;

        /// Add the settings button
        JButton settingsButton = new JButton(Main.strings.getString("settingTitle"));
        constraints.gridx++;
        settingsButton.addActionListener(e->{
            // Check settings isn't open (will be set back when settings is closed)
            if (!settingsOpen) {
                settingsOpen = true;
                SettingsDialog settings = new SettingsDialog(this);
                settings.setVisible(true);

            }
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
        newButton.addActionListener(e -> openCreateDialog());
        // Add the button to the panel
        topBarPanel.add(newButton, constraints);

        return topBarPanel;
    }
    public void openCreateDialog() {
        CreateDialog dialog = new CreateDialog(Main.strings.getString("timetableNoActivitySelected"));
        // Show the dialog
        dialog.setVisible(true);
    }
    public void makeNewDocument(DocumentType documentType, String subject) {
        // First, check it is OK to close the current window, running close systems
        if (contentPanel.closePanel()) {
            // Make the document
            Document document = Document.makeFromType(documentType);
            if (subject != null) {
                document.setSubject(subject);
                System.out.println("subject was null");
            }
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
    public Point getDialogCentre(int dialogWidth, int dialogHeight) {
        return getCentredWindowPoint(this.getX(), this.getY(), this.getWidth(), this.getHeight(), dialogWidth, dialogHeight);
    }
    public void refresh() {
        FlatLaf.updateUI();
        contentPanel.refresh();
    }
    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }
    public Timetable getTimetable() {return timetable;}
    public String[] getSubjects() {
        return timetable.configuredActivities;
    }
    protected void closeSettings() {
        settingsOpen = false;
    }
}
