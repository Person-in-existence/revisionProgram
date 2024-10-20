import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    public static String[] docTypes = new String[] {"Text"};
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
}