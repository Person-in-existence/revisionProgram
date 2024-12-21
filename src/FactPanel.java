import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FactPanel extends JPanel{
    private JTextArea questionArea;
    private JTextArea answerArea;
    private Fact fact;
    private Dimension size;
    private int textAreaWidth;
    public static final Insets factInsets = new Insets(10,5,10,5);

    public FactPanel(Fact fact, int width) {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        size = new Dimension(width, (int) (width*1.618/6));
        textAreaWidth = (int) (size.width*0.25);
        this.fact = fact;
        // Question panel
        this.add(makeQuestionPanel(), constraints);
        constraints.gridx++;

        // Answer Panel
        this.add(makeAnswerPanel(), constraints);

        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setBorder(new CompoundBorder(new EmptyBorder(5,0,5,0), Borders.defaultBorder())); // Use a compound border to easily add padding
    }
    public void resize(int width) {
        size = new Dimension(width, (int) (width*1.618/6));
        // This should resize the text areas
        textAreaWidth = (int) (size.width*0.25);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.revalidate();
        this.repaint();
    }
    private JPanel makeQuestionPanel() {
        JPanel questionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = factInsets;
        // Make the question label
        JLabel questionLabel = new JLabel(Main.strings.getString("factQuestion"));
        questionLabel.setFont(Main.factFont);
        questionPanel.add(questionLabel, constraints);
        // Increment constraints
        constraints.gridx++;
        constraints.weightx = 1;

        // Override preferred size so that the width can be kept but the height still works properly
        this.questionArea = new JTextArea(fact.question) {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = textAreaWidth;
                return size;
            }
        };
        questionArea.setFont(Main.factFont);
        // Set the question area to wrap at the end of words
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        // Add it to the question panel
        questionPanel.add(questionArea, constraints);

        return questionPanel;
    }

    private JPanel makeAnswerPanel() {
        JPanel answerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.insets = factInsets;
        // Make the answer label
        JLabel answerLabel = new JLabel(Main.strings.getString("factAnswer"));
        answerLabel.setFont(Main.factFont);
        answerPanel.add(answerLabel, constraints);



        // Increment constraints
        constraints.gridx++;
        constraints.weightx = 1;
        // Override preferred size so that the width can be kept but the height still works properly
        this.answerArea = new JTextArea(fact.answer) {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = textAreaWidth;
                return size;
            }
        };
        answerArea.setFont(Main.factFont);



        // Set the answer area to wrap at the end of words
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        // Add it to the answer panel
        answerPanel.add(answerArea, constraints);

        return answerPanel;
    }
    public Fact getFact() {
        String question = questionArea.getText();
        String answer = answerArea.getText();
        return new Fact(question, answer);
    }
}
