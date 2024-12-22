import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class FactViewDocumentPanel extends ViewDocumentPanel {
    private FactDocument originalDocument;
    private final DocumentTitlePanel titlePanel;
    private JPanel contentPanel;
    private ArrayList<FactRevisionPanel> panels;
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
        contentPanel = makeContentPanel(originalDocument.facts);
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

    private JPanel makeLowerPanel() {
        JPanel panel = new JPanel();
        panel.add(new ScrollingPanel(contentPanel));
        return panel;
    }

    private JPanel makeContentPanel(Fact[] facts) {
        JPanel panel = new JPanel();
        // Give the panel a box layout
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        return panel;
    }

    private void addFact(Fact fact, String answer) {
        FactRevisionPanel factPanel = new FactRevisionPanel(fact, (int) (this.getWidth()*0.66));
        factPanel.setAnswer(answer);
        contentPanel.add(factPanel);
        panels.add(factPanel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void refresh() {
        String[] answers = getAnswers();
        // Empty the list of panels
        contentPanel.removeAll();
        // Empty panels itself
        panels = new ArrayList<>();
        // Add new panels for all of the answers and facts
        for (int index = 0; index < answers.length; index++) {
            addFact(originalDocument.facts[index], answers[index]);
        }

    }

    @Override
    public Document getDocument() {
        return originalDocument;
    }

    private String[] getAnswers() {
        String[] answers = new String[panels.size()];
        for (int index = 0; index < panels.size(); index++) {
            answers[index] = panels.get(index).getAnswer();
        }
        return answers;
    }

    @Override
    public void setDocument(Document document) {
        originalDocument = (FactDocument) document;
        titlePanel.setText(document.getTitle());

        Fact[] facts = originalDocument.facts;
        for (Fact fact : facts) {
            addFact(fact, ""); // Add a fact with no answer
        }

    }

    @Override
    public boolean close() {
        return true;
    }
}
