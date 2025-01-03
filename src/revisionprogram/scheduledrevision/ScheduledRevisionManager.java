package revisionprogram.scheduledrevision;

import revisionprogram.DocumentMetadata;
import revisionprogram.Main;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ScheduledRevisionManager {
    public static final int daysBeforeThreshold = 3;
    public static DocumentMetadata[] getRevisionList() {
        DocumentMetadata[] data = Main.getDocumentData();
        ArrayList<DocumentMetadata> toRevise = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (DocumentMetadata documentMetadata: data) {
            if (documentMetadata.nextRevision().isBefore(now) | ChronoUnit.DAYS.between(now, documentMetadata.nextRevision())==0) {
                toRevise.add(documentMetadata);
            }
        }
        System.out.println(toRevise);
        return toRevise.toArray(new DocumentMetadata[0]);

    }

    public static LocalDate getDaysToNextRevision(LocalDate previousRevision, LocalDate nextRevision) {
     // If it is far away from that revision (more than 3 days), dont change it at all
        if (LocalDate.now().plusDays(ScheduledRevisionManager.daysBeforeThreshold).isBefore(nextRevision)) {
            return nextRevision;
        }

        long difference = ChronoUnit.DAYS.between(previousRevision, nextRevision);
        if (difference < 1) {
            // 1 day
            return LocalDate.now().plusDays(1);
        } else if (difference < 7) {
            // 7 days
            return LocalDate.now().plusDays(7);
        } else if (difference < 30) {
            return LocalDate.now().plusDays(30);
        } else {
            return LocalDate.now().plusMonths(1);
        }

    }
}
