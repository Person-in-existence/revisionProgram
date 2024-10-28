import java.io.*;

public class TextDocument extends Document {
    public String content;
    public String title;

    public TextDocument() {
        content = "";
        title = "";
    }

    public TextDocument(String title, String content) {
        this.content = content;
        this.title = title;
    }

    @Override
    public EditDocumentPanel makeEditPanel() {
        TextEditDocumentPanel panel = new TextEditDocumentPanel();
        panel.setDocument(this);
        return panel;
    }

    @Override
    public FileWriteException writeToFile(String filePath) {
        try {
            // Create a file class
            File file = new File(filePath);
            /// Check for exceptions
            // If the file doesn't exist, create it
            if (!file.exists()) {
                boolean success = file.createNewFile();
                if (!success) {
                    return new FileWriteException(true, Main.strings.getString("createFileFail"));
                }
            }
            if (file.isDirectory()) {
                return new FileWriteException(true, Main.strings.getString("fileIsDirectory"));
            }
            if (!file.canWrite()) {
                return new FileWriteException(true, Main.strings.getString("cannotWriteToFile"));
            }

            /// Write to the file
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fos);

            // Write the title
            writeString(this.title, out);

            // Write the content
            writeString(this.content, out);

            // Close the output stream
            out.close();
            fos.close();

            /// Check that it has worked
            // Title
            FileInputStream fis = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fis);
            String readTitle = readString(in);

            // Content
            String readContent = readString(in);

            // If they aren't correct, return an error!
            if (!(readTitle.equals(title) & readContent.equals(content))) {
                return new FileWriteException(true, "An unexpected error occurred while writing to the file. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new FileWriteException(true, e.getMessage());
        }
        return new FileWriteException(false, "");
    }

    @Override
    public void readFromFile(String filePath) {

    }

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
