package revisionprogram.documents;

import revisionprogram.MainPanel;
import revisionprogram.files.FileException;
import revisionprogram.scheduledrevision.ScheduledRevisionManager;

import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

public abstract class ViewDocumentPanel extends MainPanel {
    public ViewDocumentPanel() {
        super();
    }
    public ViewDocumentPanel(LayoutManager layout) {
        super(layout);
    }
    protected abstract Document getDocument();
    public abstract void setDocument(Document document);
    public boolean close() {
        Document document = getDocument();
        if (document.readyToAdvance()) {
            // Change revision on document
            // Poll
            ExitPollDialog.Confidence confidence = ExitPollDialog.poll();
            // Check for cancel and unrevised
            if (confidence == ExitPollDialog.Confidence.CANCEL) {
                // For cancel, return false to go back
                return false;
            } else if (confidence == ExitPollDialog.Confidence.UNREVISED) {
                // For unrevised, return true; we want to exit without saving
                return true;
            }

            // Get the next revision
            LocalDate nextRevision = ScheduledRevisionManager.getNextRevision(document.getLastRevised(), document.getNextRevision(), confidence);
            // Change the times on the document (Make a copy)
            document.setLastRevised(LocalDate.now());
            document.setNextRevision(nextRevision);

            FileException e = document.writeToFile();
            if (e.failed) {
                System.err.println(e.getMessage());
                System.err.println(Arrays.toString(e.getStackTrace()));
                return false;
            }
        }
        return true;
    }
}
