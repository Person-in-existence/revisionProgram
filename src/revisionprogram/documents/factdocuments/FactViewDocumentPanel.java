package revisionprogram.documents.factdocuments;

import com.formdev.flatlaf.ui.FlatBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import revisionprogram.Main;
import revisionprogram.ScrollingPanel;
import revisionprogram.documents.*;
import revisionprogram.scheduledrevision.ScheduledRevisionManager;

public class FactViewDocumentPanel extends ViewDocumentPanel {
    private FactDocument originalDocument;
    private final DocumentTitlePanel titlePanel;
    private JPanel contentPanel;
    private ScrollingPanel scrollPanel;
    private ArrayList<FactRevisionPanel> panels;
    private JLabel totalScore;
    public FactViewDocumentPanel() {
        super(new GridBagLayout());
        GridBagConstraints constraints = Main.makeConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;

        titlePanel = new DocumentTitlePanel(false);
        this.add(titlePanel, constraints);

        // Change constraints
        constraints.weighty = 1;
        constraints.gridy++;

        originalDocument = new FactDocument();

        panels = new ArrayList<>();
        contentPanel = makeContentPanel();
        this.add(makeCentrePanel(), constraints);

        // Change constraints
        constraints.weighty = 0;
        constraints.gridy++;
        // Add the bottom (check button) panel
        this.add(makeLowerPanel(), constraints);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refresh();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });


    }

    private JPanel makeCentrePanel() {
        JPanel panel = new JPanel();
        scrollPanel = new ScrollingPanel(contentPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPanel);
        // Make the scroll panel have the right size
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = new Dimension(scrollPanel.getPreferredSize().width, panel.getHeight());
                scrollPanel.setSize(size);
            }
        });
        return panel;
    }

    private JPanel makeLowerPanel() {
        JPanel panel = new JPanel();
        // Give the panel a border so that it doesn't look wierd when scrolling
        panel.setBorder(new FlatBorder());
        JButton checkButton = new JButton(Main.strings.getString("factCheck"));
        checkButton.setFocusable(false);
        checkButton.addActionListener(_->check());
        panel.add(checkButton);

        // Make a total score counter
        totalScore = new JLabel();
        panel.add(totalScore);

        return panel;
    }

    private void check() {
        for (FactRevisionPanel panel : panels) {
            panel.check();
        }
        updateTotalScore();
    }

    private JPanel makeContentPanel() {
        JPanel panel = new JPanel();
        // Give the panel a box layout
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        return panel;
    }

    private void addFact(Fact fact, String answer) {
        FactRevisionPanel factPanel = new FactRevisionPanel(fact, (int) (this.getWidth()*0.66), this);
        factPanel.setAnswer(answer);
        contentPanel.add(factPanel);
        panels.add(factPanel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void refresh() {
        // Reset the border around the scrolling panel
        scrollPanel.refresh();
        // Get the answers and whether the panel was checked
        String[] answers = getAnswers();
        boolean[] checked = getChecked();
        // Empty the list of panels
        contentPanel.removeAll();
        // Empty panels itself
        panels = new ArrayList<>();
        // Add new panels for all of the answers and facts
        for (int index = 0; index < answers.length; index++) {
            addFact(originalDocument.facts[index], answers[index]);
            // Check the panel if it was checked before
            if (checked[index]) {
                panels.get(index).check();
            }
        }

    }

    @Override
    public Document getDocument() {
        LocalDate date = ScheduledRevisionManager.getDaysToNextRevision(originalDocument.lastRevised, originalDocument.nextRevision);
        if (date == originalDocument.nextRevision) {
            return originalDocument;
        }
        return new FactDocument(originalDocument.subject, originalDocument.title, originalDocument.fileName, originalDocument.facts, LocalDate.now(), date);
    }

    private String[] getAnswers() {
        String[] answers = new String[panels.size()];
        for (int index = 0; index < panels.size(); index++) {
            answers[index] = panels.get(index).getAnswer();
        }
        return answers;
    }

    private boolean[] getChecked() {
        boolean[] checked = new boolean[panels.size()];
        for (int index = 0; index < panels.size(); index++) {
            checked[index] = panels.get(index).getChecked();
        }
        return checked;
    }

    @Override
    public void setDocument(Document document) {
        originalDocument = (FactDocument) document;
        titlePanel.setText(document.getTitle());
        titlePanel.setSubject(document.getSubject());

        Fact[] facts = originalDocument.facts;
        for (Fact fact : facts) {
            addFact(fact, ""); // Add a fact with no answer
        }

    }



    protected Document getOriginalDocument() {
        return originalDocument;
    }

    protected void updateTotalScore() {
        int total = 0;
        for (FactRevisionPanel panel: panels) {
            if (panel.isCorrect()) {
                total++;
            }
        }
        int maxScore = panels.size();
        totalScore.setText(total + "/" + maxScore);
    }
}
