package revisionprogram.files;

import com.formdev.flatlaf.ui.FlatBorder;
import revisionprogram.Borders;
import revisionprogram.DocumentMetadata;
import revisionprogram.documents.Document;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
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
        this.setBorder(defaultBorder());

    }
    public void highlight() {
        this.setBorder(highlightedBorder());
    }
    public void unhighlight() {
        this.setBorder(defaultBorder());
    }
    private static Border defaultBorder() {
        return new CompoundBorder(new FlatBorder(), new EmptyBorder(1,1,1,1));
    }
    private static Border highlightedBorder() {
        return Borders.highlightedBorder();
    }
}
