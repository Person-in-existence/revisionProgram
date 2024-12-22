import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FactRevisionPanel extends JPanel {
    private Fact fact;
    private JTextArea answerArea;
    private int answerAreaWidth;

    public FactRevisionPanel(Fact fact, int width) {
        super(new GridBagLayout());

        answerAreaWidth = width >> 1; // Half the width

        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;

        this.fact = fact;

        this.add(makeQuestionPanel(), constraints);

        // Change constraints
        constraints.weighty = 1;
        constraints.gridy++;
        this.add(makeAnswerPanel(), constraints);

        Dimension size = new Dimension(width, (int) (width*1.618/6));

        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);

        this.setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0), Borders.defaultBorder())); // Use a compound border to easily add padding



    }
    private JPanel makeQuestionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();

        // Make the question label
        JLabel questionLabel = new JLabel(fact.question);
        questionLabel.setFont(Main.factFont);
        panel.add(questionLabel, constraints);

        // Change constraints
        constraints.gridx++;
        constraints.weightx = 1;
        // Add a padding panel
        panel.add(new JPanel(), constraints);

        return panel;
    }

    private JPanel makeAnswerPanel() {
        JPanel panel = new JPanel();

        answerArea = new SizedTextArea(answerAreaWidth);
        answerArea.setFont(Main.factFont);
        panel.add(answerArea);

        return panel;
    }

    public String getAnswer() {
        return answerArea.getText();
    }
    public void setAnswer(String answer) {
        answerArea.setText(answer);
    }
    public Fact getFact() {
        return fact;
    }


}
