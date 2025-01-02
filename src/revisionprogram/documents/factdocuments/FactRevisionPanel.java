package revisionprogram.documents.factdocuments;

import revisionprogram.Borders;
import revisionprogram.Main;
import revisionprogram.SizedTextArea;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class FactRevisionPanel extends JPanel {
    private Fact fact;
    private JPanel correctIndicator;
    private SizedTextArea answerArea;
    private int answerAreaWidth;
    private boolean checked = false;
    private boolean correct = false; // Start correct as false
    private JPanel correctAnswerPanel;
    private JLabel correctAnswerLabel;
    private FactViewDocumentPanel parent;

    public FactRevisionPanel(Fact fact, int width, FactViewDocumentPanel parent) {
        super(new GridBagLayout());

        // Set parent
        this.parent = parent;

        answerAreaWidth = width >> 1; // Half the width

        GridBagConstraints constraints = Main.makeConstraints();
        constraints.fill = GridBagConstraints.BOTH;

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

        return panel;
    }

    private JPanel makeAnswerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        // Set the weighty to 1 so that the correct answer panel doesnt take up much space
        constraints.weighty = 1;
        constraints.weightx = 1;
        answerArea = new SizedTextArea(answerAreaWidth);
        answerArea.setFont(Main.factFont);
        panel.add(answerArea, constraints);

        // Add the correct indicator
        constraints.weightx = 0;
        constraints.gridx++;
        correctIndicator = new JPanel();
        int size =  answerArea.getPreferredSize().height; // Get the size of the area, so it is a square
        updateIndicatorSize(size);
        // Add a mouse listener to the correctIndicator so that it toggles whether this fact is correct when pressed
        correctIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (checked) {
                    toggleCorrect();
                }
            }
        });
        panel.add(correctIndicator, constraints);
        // Add a listener to the answer area so that when it changes size the correct indicator changes size too
        answerArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateIndicatorSize(answerArea.getHeight());
            }
        });
        // Make a panel for the correct answer to go
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weighty = 0;


        correctAnswerPanel = new JPanel();

        correctAnswerLabel = new JLabel();
        correctAnswerLabel.setFont(Main.factFont);
        correctAnswerPanel.add(correctAnswerLabel);

        panel.add(correctAnswerPanel, constraints);


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

    public boolean check() {
        checked = true; // Set checked to true so it can be accessed later
        answerArea.setWidth(answerAreaWidth-answerArea.getHeight());
        correct = Objects.equals(getAnswer(), fact.answer);
        updateIndicatorColour();
        answerArea.setEditable(false);
        correctAnswerLabel.setText(fact.answer);

        return correct;
    }
    private void updateIndicatorSize(int height) {
        Dimension size = new Dimension(height*2, height); // Get the size of the area, so it is a square
        // If the width would be too big, scale it down
        if (height*2 > (this.getPreferredSize().width/8)) {
            size.width = this.getPreferredSize().width/8;
        }
        correctIndicator.setMaximumSize(size);
        correctIndicator.setMinimumSize(size);
        correctIndicator.setPreferredSize(size);
    }

    private void updateIndicatorColour() {
        if (correct) {
            // Correct
            correctIndicator.setBackground(Main.factCorrectColour);
        } else {
            correctIndicator.setBackground(Main.factIncorrectColour);
        }
    }

    public boolean getChecked() {
        return checked;
    }
    public boolean isCorrect() {
        return correct;
    }
    public void toggleCorrect() {
        correct = !correct;
        updateIndicatorColour();
        parent.updateTotalScore();
    }

}
