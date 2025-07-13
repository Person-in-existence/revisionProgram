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
    private static Confidence confidence = Confidence.CANCEL;
    private ExitPollDialog() {
        super();

        // Make sure the user can't click anything else
        this.setModal(true);

        this.setSize(Main.dialogWidth, Main.dialogHeight);
        this.setLocation(Main.getWindow().getDialogCentre(this.getWidth(), this.getHeight()));

        JPanel panel = new JPanel();
        panel.add(new JLabel(Main.strings.getString("exitPollPrompt")));

        JPanel topButtonPanel = new JPanel(new GridBagLayout());
        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = new Insets(2, 5, 2, 5);

        JButton well = new JButton(Main.strings.getString("well"));
        well.addActionListener(e->end(Confidence.WELL));
        topButtonPanel.add(well, constraints);
        constraints.gridx++;

        JButton OK = new JButton(Main.strings.getString("ok"));
        OK.addActionListener(e->end(Confidence.OK));
        topButtonPanel.add(OK, constraints);
        constraints.gridx++;

        JButton poorly = new JButton(Main.strings.getString("poorly"));
        poorly.addActionListener(e->end(Confidence.POORLY));
        topButtonPanel.add(poorly, constraints);

        constraints.gridx = 0;
        constraints.gridy++;

        JButton unrevised = new JButton(Main.strings.getString("unrevised"));
        unrevised.addActionListener(e->end(Confidence.UNREVISED));
        bottomButtonPanel.add(unrevised, constraints);
        constraints.gridx++;

        JButton cancel = new JButton(Main.strings.getString("cancel"));
        cancel.addActionListener(e->end(Confidence.CANCEL));
        bottomButtonPanel.add(cancel, constraints);


        panel.add(topButtonPanel);
        panel.add(new JPanel());
        panel.add(bottomButtonPanel);

        this.add(panel);
        this.setVisible(true);

    }

    private void end(Confidence confidence) {
        ExitPollDialog.confidence = confidence;
        this.setVisible(false);
    }
    public static Confidence poll() {
        new ExitPollDialog();

        // Reset confidence back to CANCEL
        Confidence receivedConfidence = confidence;
        confidence = Confidence.CANCEL;
        return receivedConfidence;
    }

    public enum Confidence {
        WELL,
        OK,
        POORLY,
        UNREVISED,
        CANCEL
    }


}
