import javax.swing.*;
import java.awt.*;

public abstract class EditDocumentPanel extends MainPanel {
    public EditDocumentPanel(LayoutManager layout) {
        super(layout);
    }
    public abstract Document getDocument();
    public abstract void setDocument(Document document);
    public abstract boolean hasChanged();
    public abstract boolean close();

}
