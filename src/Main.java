import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main {
    public static String[] docTypes = new String[] {"Text"};
    public static String saveLocation = "sets/";
    public static double SIZE_SCALER = 0.9;
    public static int windowWidth;
    public static int windowHeight;
    public static int screenWidth;
    public static int screenHeight;
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
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception _) {}
        // Get locale from args
        try {
            String language = args[0];
            String country = args[1];
            Locale locale = Locale.of(language, country);
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
    public static DocumentMetadata[] getDocumentDataByType(DocumentType documentType) {
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
                    /// Check the file extension
                    String[] nameParts = name.split("\\.");
                    if (nameParts.length != 0) {
                        if (!Objects.equals("."/*Add the . back*/ + nameParts[nameParts.length - 1], Document.getExtensionByType(documentType))) {
                            // If the extension is wrong, continue
                            continue;
                        }
                    }

                    // Read the title
                    String title;
                    try {
                        FileInputStream fis = new FileInputStream(listOfFiles[index]);
                        DataInputStream in = new DataInputStream(fis);
                        title = Document.readString(in);
                        // Make the metadata
                        DocumentMetadata metadata = new DocumentMetadata(name, title);
                        // Add it to the list
                        documents[documentIndex] = metadata;
                        // increment the document index
                        documentIndex++;
                    } catch (Exception e) {
                        System.err.println("Title could not be read for file with name " + name);
                        continue;
                    }

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
    }}