package revisionprogram.documents;

import revisionprogram.MainPanel;
import revisionprogram.documents.textdocuments.TextDocument;
import revisionprogram.files.FileException;

import java.awt.*;
import java.util.Arrays;

public abstract class ViewDocumentPanel extends MainPanel {
    public ViewDocumentPanel() {
        super();
    }
    public ViewDocumentPanel(LayoutManager layout) {
        super(layout);
    }
    public abstract Document getDocument();
    protected abstract Document getOriginalDocument();
    public abstract void setDocument(Document document);
    //public abstract boolean close();
    public boolean close() {
        Document document = getDocument();
        Document originalDocument = getOriginalDocument();
        if (!document.getNextRevision().isEqual(originalDocument.getNextRevision()) | !document.getLastRevised().isEqual(originalDocument.getLastRevised())) {
            FileException e = document.writeToFile();
            if (e.failed) {
                System.err.println(e.getMessage());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
        }
        return true;
    }
}
