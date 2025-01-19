package revisionprogram.documents;

import revisionprogram.Main;
import revisionprogram.documents.blankdocuments.BlankDocument;
import revisionprogram.documents.factdocuments.FactDocument;
import revisionprogram.documents.textdocuments.TextDocument;

import java.io.*;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Objects;

import revisionprogram.files.FileException;


public abstract class Document {
    public static final EnumMap<DocumentType, String> keyMap;
    static {
        keyMap = new EnumMap<>(DocumentType.class);
        keyMap.put(DocumentType.TEXT, Main.strings.getString("textDocument"));
        keyMap.put(DocumentType.FACT, Main.strings.getString("factDocument"));
        keyMap.put(DocumentType.BLANK, Main.strings.getString("blankDocument"));
    }
    public static Document makeFromType(DocumentType type) {
        switch (type) {
            case TEXT:
                return new TextDocument();
            case FACT:
                return new FactDocument();
            case BLANK:
                return new BlankDocument();
        }
        // Default to textdoc - shouldn't be possible but suppresses errors
        return new TextDocument();
    }
    public static String getExtensionByType(DocumentType type) {
        switch (type) {
            case TEXT:
                return TextDocument.fileExtension;
            case FACT:
                return FactDocument.fileExtension;
            case BLANK:
                return BlankDocument.fileExtension;
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
            case BlankDocument.fileExtension:
                return DocumentType.BLANK;
        }
        // Return textdocument if invalid and write error to console
        System.err.println("revisionprogram.documents.Document.getTypeByExtension: Extension type not recognised: " + extension);
        System.err.println("Returning Text instead");
        return DocumentType.TEXT;
    }

    public static String makeFilePath(String title, String fileName, String fileExtension) {
        String filePath;
        if (!Objects.equals(fileName, "")) {
            filePath = fileName;
        } else {
            filePath = title;
            filePath = Main.convertFileName(filePath);
            filePath = Main.accountForDuplicates(filePath, fileExtension, false);
            filePath += "." + fileExtension;
        }

        filePath = Main.saveLocation + filePath;
        return filePath;
    }

    public abstract EditDocumentPanel makeEditPanel();
    public abstract ViewDocumentPanel makeViewPanel();
    public FileException writeToFile() {
        try {
            String filePath = makeFilePath(getTitle(), getFileName(), getFileExtension());

            File file = new File(filePath);

            FileException e = doWriteChecks(file);
            if (e.failed) {
                return e;
            }

            DataOutputStream out = makeOutputStreamFromFile(file);

            // Write the header
            writeHeader(out);

            // Write the contents
            writeContents(out);

            out.close();
            return new FileException(false, "");
        } catch (Exception e) {
            return new FileException(true, e.getMessage());
        }
    }
    public abstract void writeContents(DataOutputStream out) throws IOException;
    public FileException readFromFile(String fileName) {
        try {
            // Set the fileName
            setFileName(fileName);
            File file = makeReadFile(fileName);
            FileException e = doReadChecks(file);
            if (e.failed) {
                return e;
            }
            /// Read from the file
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);

            // Read the header
            readHeader(in);

            // Read the content
            readContents(in);

            // Close the stream
            in.close();
            // Return no exception
            return new FileException(false, "");
        } catch (Exception e) {
            e.printStackTrace();
            return new FileException(true, e.getMessage());
        }
    }
    public abstract void readContents(DataInputStream in) throws IOException;
    public abstract String getFileExtension();
    public abstract String getTitle();
    public abstract void setTitle(String title);
    public abstract String getSubject();
    public abstract void setSubject(String subject);
    public abstract String getFileName();
    public abstract void setFileName(String fileName);
    public abstract LocalDate getLastRevised();
    public abstract void setLastRevised(LocalDate lastRevised);
    public abstract LocalDate getNextRevision();
    public abstract void setNextRevision(LocalDate nextRevision);
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
    public FileException doWriteChecks(File file) throws IOException {
        if (!file.exists()) {
            boolean success = file.createNewFile();
            if (!success) {
                return new FileException(true, Main.strings.getString("createFileFail"));
            }
        }
        if (file.isDirectory()) {
            return new FileException(true, Main.strings.getString("fileIsDirectory"));
        }
        if (!file.canWrite()) {
            return new FileException(true, Main.strings.getString("cannotWriteToFile"));
        }
        return new FileException(false, "");
    }
    public FileException doReadChecks(File file) {
        /// Check that the file exists and can be read
        if (!file.exists()) {
            System.err.println(file.getPath());
            return new FileException(true, Main.strings.getString("noFile"));
        }
        if (file.isDirectory()) {
            return new FileException(true, Main.strings.getString("fileIsDirectory"));
        }
        if (!file.canRead()) {
            return new FileException(true, Main.strings.getString("cantRead"));
        }
        return new FileException(false, "");
    }
    public DataOutputStream makeOutputStreamFromFile(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        return new DataOutputStream(fos);
    }
    protected void writeHeader(DataOutputStream out) throws IOException {
        // Write the subject
        writeString(getSubject(), out);

        // Write the title
        writeString(getTitle(), out);

        // Write lastRevised
        writeString(Main.getStringFromDate(getLastRevised()), out);

        // Write nextRevised
        writeString(Main.getStringFromDate(getNextRevision()), out);
    }
    protected void readHeader(DataInputStream in) throws IOException {
        // Read the subject
        String subject = readString(in);

        // Read the title
        String title = readString(in);

        // Read lastRevised
        LocalDate lastRevised = Main.getDateFromString(readString(in));

        // Read nextRevision
        LocalDate nextRevision = Main.getDateFromString(readString(in));

        /// Set the data on the document
        setSubject(subject);
        setTitle(title);
        setLastRevised(lastRevised);
        setNextRevision(nextRevision);
    }
    protected File makeReadFile(String fileName) {
        /// Add the root to the fileName
        String filePath = Main.saveLocation + fileName;
        // Create a file class
        return new File(filePath);

    }
}
