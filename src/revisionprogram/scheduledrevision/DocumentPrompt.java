package revisionprogram.scheduledrevision;

import java.time.LocalDate;

public record DocumentPrompt(String name, int subjectIndex, LocalDate date) {
}
