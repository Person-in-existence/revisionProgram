import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class FileListItem extends JPanel {
    public FileListItem(DocumentMetadata documentMetadata) {
        super(new GridLayout(2,1));
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel(documentMetadata.title());
        titleLabel.setFont(new Font(null, Font.PLAIN, 20));
        titlePanel.add(titleLabel);

        JPanel infoPanel = new JPanel(new GridLayout(1,2));
        JLabel typeLabel = new JLabel(Document.keyMap.get(documentMetadata.documentType()));
        infoPanel.add(typeLabel);
        infoPanel.add(new JLabel(documentMetadata.name()));
        this.add(titlePanel);
        this.add(infoPanel);
        this.setBorder(new FlatBorder());

    }
    public void highlight() {
        FlatBorder border = new FlatBorder();
        border.applyStyleProperty("borderColor", Color.cyan);
        border.applyStyleProperty("focusWidth", 1);
        this.setBorder(border);
    }
    public void unhighlight() {
        this.setBorder(new FlatBorder());
    }
}
