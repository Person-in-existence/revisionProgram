import javax.swing.*;

public abstract class Document {
    public static Document makeFromType(DocumentType type) {
        switch (type) {
            case TEXT:
                return new TextDocument();
        }
        // Default to textdoc - shouldn't be possible but suppresses errors
        return new TextDocument();
    }
    public abstract JPanel makePanel();
    public abstract void writeToFile(String filePath);
    public abstract void readFromFile(String filePath);
}
