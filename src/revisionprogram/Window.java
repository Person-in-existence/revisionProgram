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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;


public class Window extends JFrame {
    public JPanel panel = new JPanel(new GridBagLayout());
    private ContentPanel contentPanel;
    private JPanel topBarPanel;
    private boolean settingsOpen = false;
    private Timetable timetable;
    public Window() {
        super();

        // Set to do nothing so our close listener can choose to close
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setTitle(Main.strings.getString("windowTitle"));

        setIcon();

        // Set the window in main so everything has access to it
        Main.setWindow(this);

        // Add a listener to remove focus when empty space in the window is clicked
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
                System.out.println("Focus Lost");
            }
        });

        // Load the timetable
        loadTimetable();

        // Set the size of the window
        this.setSize(Main.windowWidth, Main.windowHeight);

        initialiseUI();

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

        // Add a button in the top left
        JButton homeButton = new JButton(Main.strings.getString("homeButton"));
        // Make button not focusable
        homeButton.setFocusable(false);
        // Add the action listener to the button
        homeButton.addActionListener(e->goToHome());
        // Add the button to the panel
        topBarPanel.add(homeButton, constraints);

        // Add a filler panel
        constraints = addXFillerPanel(constraints, topBarPanel);

        // Add the settings button
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

        // Add a filler panel
        constraints = addXFillerPanel(constraints, topBarPanel);

        // Add a button in the top right
        constraints.gridx++;
        JButton newButton = new JButton(Main.strings.getString("createNewButton"));
        // Make button not focusable
        newButton.setFocusable(false);
        newButton.addActionListener(e -> openCreateDialog());
        // Add the button to the panel
        topBarPanel.add(newButton, constraints);

        return topBarPanel;
    }

    /**
     * Adds a "filler panel" (blank JPanel with weightX of 1) to the panel, to fill horizontal (x) space.
     * @param constraints The GridBagConstraints to use. Panel will be added at x+1, y, and constraints will be incremented to x+1 (where the filler panel is)
     * @param panel The panel to add the filler panel to.
     * @return The modified constraints. Note that this is the same object as the input: the input is also changed.
     */
    public GridBagConstraints addXFillerPanel(GridBagConstraints constraints, JPanel panel) {
        double previousWeight = constraints.weightx;
        constraints.weightx = 1;
        constraints.gridx++;
        panel.add(new JPanel(), constraints);
        constraints.weightx = previousWeight;
        return constraints;
    }

    /**
     * Adds a "filler panel" (blank JPanel with weightY of 1) to the panel, to fill vertical (Y) space.
     * @param constraints The GridBagConstraints to use. Panel will be added at x, y+1, and constraints.gridx will be incremented to y+1 (where the filler panel is)
     * @param panel The panel to add the filler panel to.
     * @return The modified constraints. Note that this is the same object as the input: the input is also changed.
     */
    public GridBagConstraints addYFillerPanel(GridBagConstraints constraints, JPanel panel) {
        double previousWeight = constraints.weighty;
        constraints.weighty = 1;
        constraints.gridy++;
        panel.add(new JPanel(), constraints);
        constraints.weighty = previousWeight;
        return constraints;
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

    /**
     * Returns the point a window should be positioned at for it to have the same centre as another window
     * @param topX The x position of the non-moving window
     * @param topY The y position of the non-moving window
     * @param backWidth The width of the non-moving window
     * @param backHeight The height of the non-moving window
     * @param width The width of the window to move
     * @param height The height of the window to move
     * @return The point the moving window should be moved to, so that it shares the centre of the static window.
     */
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

    /**
     * Returns the point a window should be positioned at to have the same centre as this window
     * @param dialogWidth The width of the window to position
     * @param dialogHeight The height of the window to position
     * @return The point the window should be moved to, so that it shares the centre of this window.
     */
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
    private void setIcon() {
        try {
            URL url = Main.class.getResource("/icon.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.err.println("Loading icon failed: " + e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
    private void loadTimetable() {
        this.timetable = new Timetable();
        FileException e = timetable.readFromFile();
        if (e.failed) {
            System.err.println("Failed to read timetable on start: " + e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
    private void initialiseUI() {
        // Make constraints
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        // Allows the panels to expand in both directions fully
        constraints.fill = GridBagConstraints.BOTH;

        // Add close listener
        addCloseListener();

        // Make the top bar
        topBarPanel = makeTopPanel();
        // Add the top bar panel to the main panel
        panel.add(topBarPanel, constraints);
        // Increment constraints
        constraints.gridy++;

        // Make a lower content panel to hold the rest of the window
        contentPanel = new ContentPanel(this);
        // Set the weighty of constraints to 1
        constraints.weighty = 1;
        // Add contentPanel to the main panel
        panel.add(contentPanel, constraints);

        // Add the main panel to the window
        this.add(panel);
        // Go to the homepage
        goToHome();
    }
    private void addCloseListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (contentPanel.closePanel()) {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    // Otherwise, show a dialog
                    int choice = JOptionPane.showConfirmDialog(Main.getWindow(), Main.strings.getString("closeFailed"), Main.strings.getString("errorDialogTitle"), JOptionPane.YES_NO_OPTION);
                    if (choice == 0) {
                        // User chose to close - setCloseOperation
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    } // If no if statement there is no close: operation is nothing on close.
                }
            }
        });
    }
}
