package revisionprogram.documents.factdocuments;

import java.io.*;
import java.time.LocalDate;

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

    public void writeContents(DataOutputStream out) throws IOException{
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
    }

    @Override
    public void readContents(DataInputStream in) throws IOException {
        int numFacts = in.readInt();
        facts = new Fact[numFacts];

        for (int index = 0; index < numFacts; index++) {
            String question = Document.readString(in);
            String answer = Document.readString(in);
            facts[index] = new Fact(question, answer);
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public LocalDate getLastRevised() {
        return lastRevised;
    }

    @Override
    public void setLastRevised(LocalDate lastRevised) {
        this.lastRevised = lastRevised;
    }

    @Override
    public LocalDate getNextRevision() {
        return nextRevision;
    }

    @Override
    public void setNextRevision(LocalDate nextRevision) {
        this.nextRevision = nextRevision;
    }

    @Override
    public Document copy() {
        // Copy facts
        Fact[] copiedFacts = new Fact[facts.length];
        System.arraycopy(facts, 0, copiedFacts, 0, facts.length);
        return new FactDocument(subject, title, fileName, copiedFacts, lastRevised, nextRevision);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {return subject;}
    public void setSubject(String subject) {
        this.subject = subject;
    }
    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
