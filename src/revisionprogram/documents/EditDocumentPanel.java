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
    public abstract boolean doSave();
    //public abstract boolean close();
    public boolean close() {
        System.out.println("EditDocumentPanel close");
        // If the document has not changed, it is OK to close (return true)
        if (!(hasChanged() & doSave())) {
            System.out.println("No change");
            return true;
        }

        return writeFile();
    }
    private boolean writeFile() {
        // Check whether the document has a filename
        FileException e = getDocument().writeToFile();
        if (e.failed) {
            // Pop up a dialog and return false
            Main.showErrorDialog(e.getMessage());
            return false;
        }

        return true;
    }
}
