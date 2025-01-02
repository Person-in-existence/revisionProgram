package revisionprogram.documents.textdocuments;

import java.io.*;
import java.util.Objects;
import revisionprogram.documents.*;
import revisionprogram.Main;
import revisionprogram.files.FileException;

public class TextDocument extends Document {
    public static final String fileExtension = "rtd"; // "Revision Text revisionprogram.documents.Document"
    public String content;
    public String title;
    public String fileName;

    public TextDocument() {
        content = "";
        title = "";
        fileName = "";
    }

    public TextDocument(String title, String content) {
        this.content = content;
        this.title = title;
        this.fileName = "";
    }

    public TextDocument(String title, String content, String filePath) {
        this.title = title;
        this.content = content;
        this.fileName = filePath;
    }

    @Override
    public EditDocumentPanel makeEditPanel() {
        TextEditDocumentPanel panel = new TextEditDocumentPanel();
        panel.setDocument(this);
        return panel;
    }
    public ViewDocumentPanel makeViewPanel() {
        TextViewDocumentPanel panel = new TextViewDocumentPanel();
        panel.setDocument(this);
        return panel;
    }

    @Override
    public FileException writeToFile() {
        try {
            /// Add the root and extension to the filePath
            // Change the file name to remove spaces etc
            String filePath;
            if (!Objects.equals(this.fileName, "")) {
                filePath = fileName;
            } else {
                filePath = this.title;
                System.out.println(filePath);
                filePath = Main.convertFileName(filePath);
                filePath = Main.accountForDuplicates(filePath, fileExtension, false);
            }

            System.out.println(filePath);
            filePath = Main.saveLocation + filePath + "." + fileExtension;
            System.out.println(filePath);

            // Create a file class
            File file = new File(filePath);
            /// Check for exceptions
            // If the file doesn't exist, create it
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

            /// Write to the file
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fos);

            // Write the title
            Document.writeString(this.title, out);

            // Write the content
            Document.writeString(this.content, out);

            // Close the output stream
            out.close();
            fos.close();

            /// Check that it has worked
            // Title
            FileInputStream fis = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fis);
            String readTitle = Document.readString(in);

            // Content
            String readContent = Document.readString(in);

            // If they aren't correct, return an error!
            if (!(readTitle.equals(title) & readContent.equals(content))) {
                System.err.println("Incorrect read");
                System.err.println(title);
                System.err.println(readTitle);
                System.err.println(content);
                System.err.println(readContent);
                return new FileException(true, "An unknown error occurred while writing to the file. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new FileException(true, e.getMessage());
        }
        return new FileException(false, "");
    }

    @Override
    public FileException readFromFile(String filePath) {
        try {

            /// Add the root to the filePath
            filePath = Main.saveLocation + filePath;
            // Create a file class
            File file = new File(filePath);
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
            /// Read from the file
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);

            // Read the title
            title = Document.readString(in);
            // Read the content
            content = Document.readString(in);
            // Set the filename
            fileName = Document.getNameFromFile(file);


            // Close the streams
            in.close();
            fis.close();
            // Return no exception
            return new FileException(false, "");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new FileException(true, e.getMessage());
        }
    }
    @Override
    public String getFileName() {
        return fileName;
    }
    public String getTitle() {
        return title;
    }

}
