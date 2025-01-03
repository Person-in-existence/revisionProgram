package revisionprogram;

import revisionprogram.files.FilesPanel;
import revisionprogram.scheduledrevision.RevisionPanel;
import revisionprogram.timetable.TimetablePanel;

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

        // Add a files panel
        filesPanel = new FilesPanel(window);
        this.add(filesPanel, constraints);
        constraints.gridx++;

        // Add the revision scheduler
        RevisionPanel revisionPanel = new RevisionPanel();
        this.add(revisionPanel, constraints);
        constraints.gridx++;

        // Add a timetable panel
        timetablePanel = new TimetablePanel();
        this.add(timetablePanel, constraints);
        constraints.gridx++;


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
