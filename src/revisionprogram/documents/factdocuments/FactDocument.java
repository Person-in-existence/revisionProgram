package revisionprogram.documents.factdocuments;

import java.io.*;
import java.time.LocalDate;
import java.util.Objects;
import revisionprogram.documents.*;
import revisionprogram.files.FileException;
import revisionprogram.Main;

public class FactDocument extends Document {
    public static final String fileExtension = "rfd";

    public String title;
    public String subject;
    public String fileName;
    public Fact[] facts;
    public LocalDate lastRevised;
    public LocalDate nextRevision;
    public FactDocument(String title, String fileName, Fact[] facts) {
        this.subject = Main.strings.getString("timetableNoActivitySelected");
        this.facts = facts;
        this.title = title;
        this.fileName = fileName;
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
    }
    public FactDocument(String subject, String title, String fileName, Fact[] facts) {
        this.subject = subject;
        this.facts = facts;
        this.title = title;
        this.fileName = fileName;
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
    }
    public FactDocument(String subject, String title, String fileName, Fact[] facts, LocalDate lastRevised, LocalDate nextRevision) {
        this.subject = subject;
        this.facts = facts;
        this.title = title;
        this.fileName = fileName;
        this.lastRevised = lastRevised;
        this.nextRevision = nextRevision;
    }
    public FactDocument() {
        this.subject = Main.strings.getString("timetableNoActivitySelected");
        this.facts = new Fact[0];
        this.title = "";
        this.fileName = "";
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
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

            // Write the subject
            Document.writeString(this.subject, out);

            // Write the title
            Document.writeString(this.title, out);

            // Write lastRevised
            Document.writeString(Main.getStringFromDate(this.lastRevised), out);

            // Write nextRevision
            Document.writeString(Main.getStringFromDate(this.nextRevision), out);


            /// Write the facts
            // Num facts
            int numFacts = facts.length;
            out.writeInt(numFacts);

            for (Fact fact:facts) {
                // String question
                String question = fact.question;
                Document.writeString(question, out);
                // String answer
                String answer = fact.answer;
                Document.writeString(answer, out);
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

            // Read the subject
            this.subject = Document.readString(in);

            // Read the title
            title = Document.readString(in);

            // Read lastRevised
            this.lastRevised = Main.getDateFromString(Document.readString(in));

            // Read nextRevision
            this.nextRevision = Main.getDateFromString(Document.readString(in));

            int numFacts = in.readInt();
            facts = new Fact[numFacts];

            for (int index = 0; index < numFacts; index++) {
                String question = Document.readString(in);
                String answer = Document.readString(in);
                facts[index] = new Fact(question, answer);
            }

            fileName = Document.getNameFromFile(file);
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
    public LocalDate getLastRevised() {
        return lastRevised;
    }

    @Override
    public LocalDate getNextRevision() {
        return nextRevision;
    }

    @Override
    public String getTitle() {
        return title;
    }
    public String getSubject() {return subject;}
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
