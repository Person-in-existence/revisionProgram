package revisionprogram.documents;

import revisionprogram.Main;
import revisionprogram.files.FileException;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import revisionprogram.MainPanel;

public abstract class EditDocumentPanel extends MainPanel {
    public EditDocumentPanel(LayoutManager layout) {
        super(layout);
    }
    protected abstract Document getOriginalDocument();
    protected abstract String getTitle();
    public abstract Document getDocument();
    public abstract void setDocument(Document document);
    public abstract boolean hasChanged();
    //public abstract boolean close();
    public boolean close() {
        System.out.println("revisionprogram.documents.textdocuments.TextEditDocumentPanel close");
        // If the document has not changed, it is OK to close (return true)
        if (!hasChanged()) {
            System.out.println("No change");
            return true;
        }
        // TODO: STANDARDISE FILE NAME SYSTEM ETC AND MAKE IT POSSIBLE TO CHANGE IT WHEN THE TITLE CHANGES? Maybe ignore old filepath - would need to track old file though - or save it with a timestamp name
        // Check whether the document has a filename
        if (!Objects.equals(getOriginalDocument().getFileName(), "")) {
            // If it does, save it with a new document
            FileException e = getDocument().writeToFile();
            if (e.failed) {
                // Pop up a dialog and return false
                JOptionPane.showMessageDialog(this, e.getMessage(), Main.strings.getString("fileErrorTitle"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            // Make a filePath for the file
            String filePath = getTitle();
            // TODO: HANDLE EMPTY TITLE
            FileException e = getDocument().writeToFile();
            if (e.failed) {
                System.err.println("File Write failed");
                System.err.println(e.getMessage());

                e.printStackTrace();
                // Pop up a dialog and return false
                JOptionPane.showMessageDialog(this, e.getMessage(), Main.strings.getString("fileErrorTitle"), JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }
        // Otherwise, TODO: HANDLE CLOSE WITH UNSAVED CHANGES - DIALOG AND SAVE ETC
        // TODO: REMOVE RETURN TRUE CASE
        return true;
    }
}
