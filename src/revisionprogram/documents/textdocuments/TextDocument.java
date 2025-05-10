package revisionprogram.documents.textdocuments;

import java.io.*;
import java.time.LocalDate;

import revisionprogram.documents.*;
import revisionprogram.Main;
import revisionprogram.documents.factdocuments.FactDocument;
import revisionprogram.files.FileException;


public class TextDocument extends Document {
    public static final String fileExtension = "rtd"; // "Revision Text Document"
    public String subject;
    public String content;
    public String title;
    public String fileName;
    public LocalDate lastRevised;
    public LocalDate nextRevision;

    public TextDocument() {
        subject = Main.strings.getString("timetableNoActivitySelected");
        content = "";
        title = "";
        fileName = "";
        lastRevised = LocalDate.now();
        nextRevision = LocalDate.now();
    }

    public TextDocument(String title, String content) {
        this.subject = Main.strings.getString("timetableNoActivitySelected");
        this.content = content;
        this.title = title;
        this.fileName = "";
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
    }

    public TextDocument(String title, String content, String filePath) {
        this.subject = Main.strings.getString("timetableNoActivitySelected");
        this.title = title;
        this.content = content;
        this.fileName = filePath;
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
    }
    public TextDocument(String title, String content, String filePath, LocalDate lastRevised, LocalDate nextRevision, String subject) {
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.fileName = filePath;
        this.lastRevised = lastRevised;
        this.nextRevision = nextRevision;
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
    public void writeContents(DataOutputStream out) throws IOException{
        // Write the content
        writeString(this.content, out);
    }

    public void readContents(DataInputStream in) throws IOException {
        content = Document.readString(in);
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
        return new TextDocument(title, content, fileName, lastRevised, nextRevision, subject);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
