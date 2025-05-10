package revisionprogram.documents;

import revisionprogram.Main;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExitPollDialog extends JDialog {
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static Confidence confidence = Confidence.NONE;
    private ExitPollDialog() {
        super();

        // Make sure the user can't click anything else
        this.setModal(true);

        this.setSize(Main.dialogWidth, Main.dialogHeight);
        this.setLocation(Main.getWindow().getDialogCentre(this.getWidth(), this.getHeight()));

        JPanel panel = new JPanel();
        panel.add(new JLabel(Main.strings.getString("exitPollPrompt")));

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = new Insets(2, 5, 2, 5);

        JButton well = new JButton(Main.strings.getString("well"));
        well.addActionListener(e->end(Confidence.WELL));
        buttonPanel.add(well, constraints);
        constraints.gridx++;

        JButton OK = new JButton(Main.strings.getString("ok"));
        OK.addActionListener(e->end(Confidence.OK));
        buttonPanel.add(OK, constraints);
        constraints.gridx++;

        JButton poorly = new JButton(Main.strings.getString("poorly"));
        poorly.addActionListener(e->end(Confidence.POORLY));
        buttonPanel.add(poorly, constraints);

        panel.add(buttonPanel);

        this.add(panel);
        this.setVisible(true);

    }

    private void end(Confidence confidence) {
        ExitPollDialog.confidence = confidence;
        this.setVisible(false);
    }
    public static Confidence poll() {
        new ExitPollDialog();

        // Reset confidence back to NONE
        Confidence receivedConfidence = confidence;
        confidence = Confidence.NONE;
        return receivedConfidence;
    }

    public enum Confidence {
        NONE,
        WELL,
        OK,
        POORLY
    }


}
