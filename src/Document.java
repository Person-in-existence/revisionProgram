import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumMap;

public abstract class Document {
    public static final EnumMap<DocumentType, String> keyMap;
    static {
        keyMap = new EnumMap<>(DocumentType.class);
        keyMap.put(DocumentType.TEXT, Main.strings.getString("textDocument"));
    }
    public static Document makeFromType(DocumentType type) {
        switch (type) {
            case TEXT:
                return new TextDocument();
        }
        // Default to textdoc - shouldn't be possible but suppresses errors
        return new TextDocument();
    }
    public static String getExtensionByType(DocumentType type) {
        switch (type) {
            case TEXT:
                return TextDocument.fileExtension;
        }
        // Return empty string otherwise
        return "";
    }
    public static DocumentType getTypeByExtension(String extension) {
        switch (extension) {
            case TextDocument.fileExtension:
                return DocumentType.TEXT;
        }
        // Return textdocument if invalid
        return DocumentType.TEXT;
    }
    public abstract EditDocumentPanel makeEditPanel();
    public abstract FileException writeToFile(String filePath);
    public abstract FileException readFromFile(String filePath);
    public static void writeString(String string, DataOutputStream out) throws IOException {
        // get the length of the string
        long length = string.length();
        // Write the length to the output stream
        out.writeLong(length);

        // Write the string itself
        out.writeChars(string);
    }
    public static String readString(DataInputStream in) throws IOException {
        // Read the length of the string
        long length = in.readLong();
        // Read that many characters
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(in.readChar());
        }
        return stringBuilder.toString();
    }
}
