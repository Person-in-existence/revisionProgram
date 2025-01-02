package revisionprogram.documents;

import revisionprogram.MainPanel;

import java.awt.*;

public abstract class ViewDocumentPanel extends MainPanel {
    public ViewDocumentPanel() {
        super();
    }
    public ViewDocumentPanel(LayoutManager layout) {
        super(layout);
    }
    public abstract Document getDocument();
    public abstract void setDocument(Document document);
    public abstract boolean close();
}
