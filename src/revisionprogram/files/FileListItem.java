package revisionprogram.files;

import com.formdev.flatlaf.ui.FlatBorder;
import revisionprogram.Borders;
import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.components.panellist.ListCard;
import revisionprogram.documents.Document;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FileListItem extends ListCard {
    private final DocumentMetadata documentMetadata;
    public FileListItem(DocumentMetadata documentMetadata) {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();

        this.documentMetadata = documentMetadata;

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(0,0,3,0));
        JLabel titleLabel = new JLabel(documentMetadata.title());
        titleLabel.setFont(Main.textContentFont);
        titlePanel.add(titleLabel);
        this.add(titlePanel, constraints);

        constraints.gridy++;

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints infoConstraints = Main.makeConstraints();

        JLabel typeLabel = new JLabel(Document.keyMap.get(documentMetadata.documentType()));
        infoPanel.add(typeLabel, infoConstraints);

        // Add a separation panel
        infoConstraints.gridx++;
        infoConstraints.weightx = 1;
        infoPanel.add(new JPanel(), infoConstraints);
        infoConstraints.weightx = 0;

        // Add the name label
        infoConstraints.gridx++;
        infoPanel.add(new JLabel(documentMetadata.name()), infoConstraints);

        // Add a separation panel
        infoConstraints.gridx++;
        infoConstraints.weightx = 1;
        infoPanel.add(new JPanel(), infoConstraints);
        infoConstraints.weightx = 0;

        // Add the subject label
        infoConstraints.gridx++;
        JLabel subjectLabel = new JLabel(documentMetadata.subject());
        infoPanel.add(subjectLabel);

        this.add(infoPanel, constraints);
    }
    public DocumentMetadata getData() {
        return documentMetadata;
    }
}
