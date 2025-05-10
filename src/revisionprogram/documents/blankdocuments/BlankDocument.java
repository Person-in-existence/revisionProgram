package revisionprogram.documents.blankdocuments;

import revisionprogram.documents.Document;
import revisionprogram.documents.EditDocumentPanel;
import revisionprogram.documents.ViewDocumentPanel;
import revisionprogram.documents.factdocuments.FactDocument;
import revisionprogram.files.FileException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class BlankDocument extends Document {
    public static final String fileExtension = "rbd"; // Revision Blank Document
    public String title;
    public String subject;
    public String fileName;
    public LocalDate lastRevised;
    public LocalDate nextRevision;
    public BlankString[] blanks;
    public BlankDocument() {
        this.title = "";
        this.subject = "";
        this.fileName = "";
        this.lastRevised = LocalDate.now();
        this.nextRevision = LocalDate.now();
        this.blanks = new BlankString[0];
    }

    public BlankDocument(String title, String subject, String fileName, BlankString[] blanks, LocalDate lastRevised, LocalDate nextRevision) {
        this.title = title;
        this.subject = subject;
        this.fileName = fileName;
        this.lastRevised = lastRevised;
        this.nextRevision = nextRevision;
        this.blanks = blanks;
    }

    @Override
    public EditDocumentPanel makeEditPanel() {
        BlankEditDocumentPanel documentPanel = new BlankEditDocumentPanel();
        documentPanel.setDocument(this);
        return documentPanel;
    }

    @Override
    public ViewDocumentPanel makeViewPanel() {
        BlankViewDocumentPanel documentPanel = new BlankViewDocumentPanel();
        documentPanel.setDocument(this);
        return documentPanel;
    }

    @Override
    public void writeContents(DataOutputStream out) throws IOException {
        // Int numBlankCards
        int numBlankCards = blanks.length;
        out.writeInt(numBlankCards);

        // numBlankCards times:
        for (int index = 0; index < numBlankCards; index++) {
            BlankString currentBlank = blanks[index];
            // String blankString
            writeString(currentBlank.string(), out);

            // int numBlanks
            Blank[] cardBlanks = currentBlank.blanks();
            int numBlanks = cardBlanks.length;
            out.writeInt(numBlanks);

            // numBlanks times
            for (Blank cardBlank : cardBlanks) {
                // int startIndex
                int startIndex = cardBlank.getStart();
                out.writeInt(startIndex);

                // int endIndex
                int endIndex = cardBlank.getEnd();
                out.writeInt(endIndex);
            }
        }
    }
    @Override
    public void readContents(DataInputStream in) throws IOException {
        // int numBlankCards
        int numBlankCards = in.readInt();

        // Make an array of BlankStrings now we know the length
        BlankString[] blankStrings = new BlankString[numBlankCards];

        // numBlankCards times:
        for (int index = 0; index < numBlankCards; index++) {
            // String blankString
            String blankString = readString(in);

            // int numBlanks
            int numBlanks = in.readInt();

            // Make an array now we know how many blanks there are
            Blank[] blanks = new Blank[numBlanks];

            // numBlanks times
            for (int blankIndex = 0; blankIndex < numBlanks; blankIndex++) {
                // int startIndex
                int startIndex = in.readInt();

                // int endIndex
                int endIndex = in.readInt();

                // Make the blank
                blanks[blankIndex] = new Blank(startIndex, endIndex);
            }

            blankStrings[index] = new BlankString(blankString, blanks);
        }
        this.blanks = blankStrings;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
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
        BlankString[] copiedBlanks = new BlankString[blanks.length];
        System.arraycopy(blanks, 0, copiedBlanks, 0, blanks.length);
        return new BlankDocument(subject, title, fileName, copiedBlanks, getLastRevised(), getNextRevision());
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
