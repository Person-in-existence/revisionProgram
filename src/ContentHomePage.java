import javax.swing.*;
import java.awt.*;

public class ContentHomePage extends MainPanel {
    public ContentHomePage(Window window) {
        super(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;

        //JLabel label = new JLabel("Insert Home Page Here");
        // Add a files panel
        FilesPanel filesPanel = new FilesPanel(window);
        this.add(filesPanel, constraints);
        constraints.gridx++;
        // Add a timetable panel
        this.add(new TimetablePanel(), constraints);
        //label.setFont(new Font(null, Font.PLAIN, 50));
        //this.add(label);
    }
    @Override
    public boolean close() {
        return true;
    }
}
