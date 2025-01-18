package revisionprogram.documents.blankdocuments;

import revisionprogram.documents.Document;
import revisionprogram.documents.EditDocumentPanel;
import revisionprogram.documents.ViewDocumentPanel;
import revisionprogram.files.FileException;

import java.time.LocalDate;

public class BlankDocument extends Document {
    public static final String fileExtension = "rbd"; // Revision Blank Document
    public String title;
    public String subject;
    public String fileName;
    public LocalDate lastRevised;
    public LocalDate nextRevision;
    public BlankDocument() {
        this.title = "";
        this.subject = "";
        this.fileName = "";
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
    }

    public BlankDocument(String title, String subject, String fileName, LocalDate lastRevised, LocalDate nextRevision) {
        this.title = title;
        this.subject = subject;
        this.fileName = fileName;
        this.lastRevised = lastRevised;
        this.nextRevision = nextRevision;
    }

    @Override
    public EditDocumentPanel makeEditPanel() {
        BlankEditDocumentPanel documentPanel = new BlankEditDocumentPanel();
        documentPanel.setDocument(this);
        return documentPanel;
    }

    @Override
    public ViewDocumentPanel makeViewPanel() {
        return null;
    }

    @Override
    public FileException writeToFile() {
        return null;
    }

    @Override
    public FileException readFromFile(String filePath) {
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
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
}
