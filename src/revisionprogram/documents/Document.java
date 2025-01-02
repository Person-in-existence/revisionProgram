package revisionprogram.documents;

import revisionprogram.Main;
import revisionprogram.documents.factdocuments.FactDocument;
import revisionprogram.documents.textdocuments.TextDocument;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import revisionprogram.files.FileException;


public abstract class Document {
    public static final EnumMap<DocumentType, String> keyMap;
    static {
        keyMap = new EnumMap<>(DocumentType.class);
        keyMap.put(DocumentType.TEXT, Main.strings.getString("textDocument"));
    }
    public static Document makeFromType(DocumentType type) {
        switch (type) {
            case DocumentType.TEXT:
                return new TextDocument();
            case DocumentType.FACT:
                return new FactDocument();
        }
        // Default to textdoc - shouldn't be possible but suppresses errors
        return new TextDocument();
    }
    public static String getExtensionByType(DocumentType type) {
        switch (type) {
            case DocumentType.TEXT:
                return TextDocument.fileExtension;
            case DocumentType.FACT:
                return FactDocument.fileExtension;
        }
        // Return empty string otherwise
        return "";
    }
    public static DocumentType getTypeByExtension(String extension) {
        switch (extension) {
            case TextDocument.fileExtension:
                return DocumentType.TEXT;
            case FactDocument.fileExtension:
                return DocumentType.FACT;
        }
        // Return textdocument if invalid and write error to console
        System.err.println("revisionprogram.documents.Document.getTypeByExtension: Extension type not recognised: " + extension);
        System.err.println("Returning Text instead");
        return DocumentType.TEXT;
    }
    public abstract EditDocumentPanel makeEditPanel();
    public abstract ViewDocumentPanel makeViewPanel();
    public abstract FileException writeToFile();
    public abstract FileException readFromFile(String filePath);
    public abstract String getTitle();
    public abstract String getFileName();
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

    public static String getNameFromFile(File file) {
        return file.getName().split("\\.")[0];
    }
}
