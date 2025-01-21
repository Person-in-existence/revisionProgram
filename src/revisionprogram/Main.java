package revisionprogram;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import revisionprogram.documents.Document;
import revisionprogram.documents.DocumentType;
import revisionprogram.files.FileException;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main {
    public static final Font titleFont = new Font("Arial", Font.PLAIN, 30);
    public static final Font textContentFont = new Font("Arial", Font.PLAIN, 16);
    public static final Font factFont = new Font("Arial", Font.PLAIN, 24);
    public static final String[] docTypes = new String[] {"Text", "Fact", "Blank"};
    public static final String saveLocation = "sets/";
    public static final double SIZE_SCALER = 0.9;
    public static final int scrollSpeed = 20;
    public static final Color factCorrectColour = new Color(50, 206, 50);
    public static final Color factIncorrectColour = new Color(229, 47, 47);
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static Locale defaultLocale = Locale.getDefault();
    public static int windowWidth;
    public static int windowHeight;
    public static int screenWidth;
    public static int screenHeight;
    private static Window window;

    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        windowWidth = (int) (screenSize.width*SIZE_SCALER);
        windowHeight = (int) (screenSize.height*SIZE_SCALER);
    }
    public static ResourceBundle strings;
    public static int dialogWidth = 300;
    public static int dialogHeight = 150;

    public static void main(String[] args) {
        // Get the preferred settings (light/dark mode)
        Settings settings = new Settings();

        if (settings.darkMode) {
            setDarkMode();
        } else {
            setLightMode();
        }
        try {
            Locale locale = Locale.getDefault();
            strings = ResourceBundle.getBundle("Strings", locale);
        } catch (Exception e) {
            System.err.println("Getting locale failed, using UK");
            strings = ResourceBundle.getBundle("Strings", Locale.UK);
        }

        // Check saveLocation folder exists, otherwise create it
        File saveRoot = new File(saveLocation);
        if (!saveRoot.exists()) {
            saveRoot.mkdirs();
        }
        Window window = new Window();
    }

    protected static void setWindow(Window window) {Main.window = window;}
    public static DocumentType getTypeFromIndex(int index) {
        try {
            return DocumentType.values()[index];
        } catch (Exception e) {
            System.err.println("Exception encountered while converting index to document type: " + e.getCause());
            System.err.println("Reverting to TEXT type - Stack trace below: ");
            e.printStackTrace();
            return DocumentType.TEXT;
        }
    }

    public static DocumentMetadata[] getDocumentData() {
        // Make the root file
        File root = new File(saveLocation);
        // Get the array of all files
        File[] listOfFiles = root.listFiles();
        // Check the list of files is not null
        if (listOfFiles != null) {
            int documentIndex = 0;
            DocumentMetadata[] documents = new DocumentMetadata[listOfFiles.length];
            for (int index = 0; index < listOfFiles.length; index++) {
                // Check it's a file
                if (listOfFiles[index].isFile()) {
                    // Get the title and name
                    String name = listOfFiles[index].getName();

                    // Read the title
                    documentIndex = getFileTitleAndDate(listOfFiles, documentIndex, documents, index, name);

                }
            }
            // Trim the list by copying it to a smaller one
            // Make the new array
            DocumentMetadata[] finalDocuments = new DocumentMetadata[documentIndex];
            // Copy it
            System.arraycopy(documents, 0, finalDocuments,0, documentIndex);
            // Return the array
            return finalDocuments;
        }
        return new DocumentMetadata[]{};
    }

    public static DocumentErrorPackage makeDocumentFromData(DocumentMetadata documentMetadata) {
        Document d = Document.makeFromType(documentMetadata.documentType());
        FileException e = d.readFromFile(documentMetadata.name());
        if (e.failed) {
            System.err.println("Error reading from file");
            System.err.println(Arrays.toString(e.getStackTrace()));
            System.err.println(e.getMessage());
        }
        return new DocumentErrorPackage(d, e);
    }

    private static int getFileTitleAndDate(File[] listOfFiles, int documentIndex, DocumentMetadata[] documents, int index, String name) {
        String title;
        String dateString;
        try {
            FileInputStream fis = new FileInputStream(listOfFiles[index]);
            DataInputStream in = new DataInputStream(fis);
            String subject = Document.readString(in);
            title = Document.readString(in);

            // Get the extension to get the document type
            String[] parts = name.split("\\.");
            DocumentType type;
            if (parts.length != 0) {
                type = Document.getTypeByExtension(parts[parts.length-1]);
            } else {
                return documentIndex;
            }
            dateString = Document.readString(in);
            LocalDate date = Main.getDateFromString(dateString);

            String nextRevisionString = Document.readString(in);
            LocalDate nextRevision = Main.getDateFromString(nextRevisionString);

            // Make the metadata
            DocumentMetadata metadata = new DocumentMetadata(subject, name, title, date, nextRevision, type);
            // Add it to the list
            documents[documentIndex] = metadata;
            // increment the document index
            documentIndex++;
        } catch (Exception e) {
            System.err.println("Metadata could not be read for file with name " + name);
            return documentIndex;
        }
        return documentIndex;
    }

    public static boolean setDarkMode() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            return true;
        } catch (Exception e) {
            System.err.println("Setting dark mode failed");
            return false;
        }
    }
    public static boolean setLightMode() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            return true;
        } catch (Exception e) {
            System.err.println("Setting light mode failed");
            return false;
        }
    }
    public static Color getPanelForeground() {
        return UIManager.getColor("Panel.foreground");
    }
    public static Color getPanelBackground() {
        return UIManager.getColor("Panel.background");
    }
    public static Color getButtonForeground() {
        return UIManager.getColor("Button.foreground");
    }
    public static Color getButtonBackground() {
        return UIManager.getColor("Button.background");
    }
    public static Color getLabelForeground() {
        return UIManager.getColor("Label.foreground");
    }
    public static Color getLabelBackground() {
        return UIManager.getColor("Label.background");
    }
    public static Color getTextFieldForeground() {
        return UIManager.getColor("TextField.foreground");
    }
    public static Color getTextFieldBackground() {
        return UIManager.getColor("TextField.background");
    }
    public static Color getTextPaneForeground() {
        return UIManager.getColor("TextPane.foreground");
    }
    public static Color getTextPaneBackground() {
        return UIManager.getColor("TextPane.background");
    }
    public static void setComponentColour(Component c) {
        if (c instanceof JPanel) {
            c.setForeground(getPanelForeground());
            c.setBackground(getPanelBackground());
        }
        else if (c instanceof JButton) {
            c.setForeground(getButtonForeground());
            c.setBackground(getButtonBackground());
            ((JButton)c).setBorder(new FlatButtonBorder());
        }
        else if (c instanceof JLabel) {
            c.setForeground(getLabelForeground());
            c.setBackground(getLabelBackground());
        }
        else if (c instanceof JTextField) {
            c.setForeground(getTextFieldForeground());
            c.setBackground(getTextFieldBackground());
        }
        else if (c instanceof JTextPane) {
            c.setForeground(getTextPaneForeground());
            c.setBackground(getTextPaneBackground());
        }
    }
    public static String convertFileName(String fileName) {
        StringBuilder newFileName = new StringBuilder();
        // Name it "Untitled" if it is empty (as no set file name is an empty string, so an empty file name will break it)
        if (Objects.equals(fileName, "")) {
            return Main.strings.getString("untitledFileName");
        }
        for (int index = 0; index < fileName.length(); index++) {
            char character = fileName.charAt(index);
            // Filter illegal chars
            if (character == ' ' | character == '<' | character == '>' | character == ':' | character == '"' | character == '/' | character == '\\' | character == '|' | character == '&' | character == '%' | character == '~') {
                newFileName.append("_");
            } else {
                newFileName.append(character);
            }
        }
        return newFileName.toString();
    }
    public static String accountForDuplicates(String fileName, String extension, boolean previousFileNameExists) {
        if (previousFileNameExists) {
            return fileName;
        }
        String fileRoot = fileName + "_";
        File toCheck = new File(Main.saveLocation + fileName + "." + extension);
        int version = 1;
        if (toCheck.exists()) {
            version++;
            String tempFileName = fileRoot + version;

            toCheck = new File(Main.saveLocation + tempFileName + "." + extension);
        } else {
            return fileName;
        }
        while (toCheck.exists()) {
            version++;
            String tempFileName = fileRoot + version;
            toCheck = new File(Main.saveLocation + tempFileName + "." + extension);
        }
        return fileRoot + version;
    }
    public static GridBagConstraints makeConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        return constraints;
    }
    public static String getCurrentDateString() {
        LocalDate date = LocalDate.now();
        return date.format(dateTimeFormatter);
    }
    public static LocalDate getDateFromString(String dateString) {
         return LocalDate.parse(dateString, dateTimeFormatter);
    }
    public static String getStringFromDate(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

    public static String getUserStyleDateString(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(defaultLocale);
        return date.format(dateTimeFormatter);
    }
    public static Window getWindow() {
        return window;
    }


}