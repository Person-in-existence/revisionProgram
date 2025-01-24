package revisionprogram.scheduledrevision;

import revisionprogram.Borders;
import revisionprogram.Main;

import javax.swing.*;
import java.awt.*;

public class RevisionPanel extends JPanel {
    private DocumentDataPanel dataPanel;
    public RevisionPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        // Make the upper panel
        this.add(makeUpperPanel(), constraints);
        constraints.gridy++;

        // Make the list panel
        dataPanel = new DocumentDataPanel(ScheduledRevisionManager.getRevisionList());

        constraints.weighty = 1;
        this.add(dataPanel, constraints);

        this.setBorder(Borders.defaultBorder());


    }
    private JPanel makeUpperPanel() {
        JPanel upperPanel = new JPanel();
        JLabel label = new JLabel(Main.strings.getString("scheduledRevisionPrompt"));
        label.setFont(Main.textContentFont);
        upperPanel.add(label);
        return upperPanel;
    }

}
