import javax.swing.*;
import java.awt.*;

public class ContentHomePage extends MainPanel {
    private FilesPanel filesPanel;
    private TimetablePanel timetablePanel;
    public ContentHomePage(Window window) {
        super(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;

        //JLabel label = new JLabel("Insert Home Page Here");
        // Add a files panel
        filesPanel = new FilesPanel(window);
        this.add(filesPanel, constraints);
        constraints.gridx++;
        // Add a timetable panel
        timetablePanel = new TimetablePanel();
        this.add(timetablePanel, constraints);
        //label.setFont(new Font(null, Font.PLAIN, 50));
        //this.add(label);
    }
    @Override
    public void refresh(){
        filesPanel.refreshUI();
    }
    @Override
    public boolean close() {
        timetablePanel.close();
        return true;
    }
}
