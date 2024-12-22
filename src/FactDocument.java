import javax.swing.*;
import java.io.*;
import java.util.Objects;

public class FactDocument extends Document {
    public static final String fileExtension = "rfd";

    public String title;
    public String fileName;
    public Fact[] facts;
    public FactDocument(String title, String fileName, Fact[] facts) {
        this.facts = facts;
        this.title = title;
        this.fileName = fileName;
    }
    public FactDocument() {
        this.facts = new Fact[0];
        this.title = "";
        this.fileName = "";
    }
    @Override
    public EditDocumentPanel makeEditPanel() {
        FactEditDocumentPanel panel = new FactEditDocumentPanel();
        panel.setDocument(this);
        return panel;
    }

    @Override
    public ViewDocumentPanel makeViewPanel() {
        FactViewDocumentPanel panel = new FactViewDocumentPanel();
        panel.setDocument(this);
        return panel;
    }

    @Override
    public FileException writeToFile() {
        try {
            String filePath;
            if (!Objects.equals(this.fileName, "")) {
                filePath = fileName;
            } else {
                filePath = this.title;
                filePath = Main.convertFileName(filePath);
                filePath = Main.accountForDuplicates(filePath, fileExtension, false);
            }
            filePath = Main.saveLocation + filePath + "." + fileExtension;
            System.out.println(filePath);

            // Make a file
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
            writeString(this.title, out);


            /// Write the facts
            // Num facts
            int numFacts = facts.length;
            out.writeInt(numFacts);

            for (Fact fact:facts) {
                // String question
                String question = fact.question;
                writeString(question, out);
                // String answer
                String answer = fact.answer;
                writeString(answer, out);
            }

            return new FileException(false, "");
        } catch (IOException e) {
            return new FileException(true, e.getMessage());
        }

    }

    @Override
    public FileException readFromFile(String filePath) {
        try {
            filePath = Main.saveLocation + filePath;

            File file = new File(filePath);
            // Check for exceptions
            if (!file.exists()) {
                return new FileException(true, Main.strings.getString("noFile"));
            }
            if (file.isDirectory()) {
                return new FileException(true, Main.strings.getString("fileIsDirectory"));
            }
            if (!file.canRead()) {
                return new FileException(true, Main.strings.getString("cantRead"));
            }

            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);

            // Read the title
            title = readString(in);
            System.out.println(title);

            int numFacts = in.readInt();
            facts = new Fact[numFacts];

            for (int index = 0; index < numFacts; index++) {
                String question = readString(in);
                String answer = readString(in);
                facts[index] = new Fact(question, answer);
            }

            fileName = getNameFromFile(file);
            in.close();
            fis.close();
            return new FileException(false, "");
        } catch (Exception e) {
            return new FileException(true, e.getMessage());
        }
    }
    @Override
    public String getFileName() {
        return fileName;
    }
    @Override
    public String getTitle() {
        return title;
    }
}
