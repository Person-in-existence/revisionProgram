package revisionprogram.scheduledrevision;

import revisionprogram.DocumentMetadata;
import revisionprogram.Main;
import revisionprogram.documents.Document;
import revisionprogram.timetable.Timetable;
import revisionprogram.timetable.TimetableActivity;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

public class ScheduledRevisionManager {
    public static final String saveLocation = Main.saveRoot + "documentprompts";
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

    public static LocalDate getNextRevision(LocalDate previousRevision, LocalDate nextRevision) {
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

    private static TimetableActivity[] readActivities(DataInputStream in) throws IOException{
        // Read numActivities1
        int numActivities1 = in.readInt();
        TimetableActivity[] activities = new TimetableActivity[numActivities1];
        for (int index = 0; index < numActivities1; index++) {
            String activityName = Document.readString(in);
            int subjectIndex = in.readInt();
            activities[index] = new TimetableActivity(activityName, subjectIndex);
        }
        return activities;
    }
    private static void writeActivities(DocumentPrompt[] activities, DataOutputStream out) throws IOException {
        int numActivities = activities.length;
        out.writeInt(numActivities);

        for (DocumentPrompt activity: activities) {
            Document.writeString(activity.name(), out);

            out.writeInt(activity.subjectIndex());
        }
    }

    public static DocumentPrompt[] getPrompts() {
        try {
            File file = new File(saveLocation);
            if (!file.exists()) {
                file.createNewFile();
            }

            LocalDate currentDate = LocalDate.now();

            DataInputStream in = new DataInputStream(new FileInputStream(file));

            // Read the date
            String date = Document.readString(in);
            LocalDate readDate = Main.getDateFromString(date);

            TimetableActivity[] readDay1Activities = readActivities(in);
            TimetableActivity[] readDay2Activities = readActivities(in);
            TimetableActivity[] day1Activities;
            TimetableActivity[] day2Activities;
            if (currentDate.isEqual(readDate)) {
                // day 1 is day 1 and day 2 is day 2
                day1Activities = readDay1Activities;
                day2Activities = readDay2Activities;
            } else if (currentDate.minusDays(1).isEqual(readDate)) {
                // 1 day before - take day1Activities and today's timetable
                // Today's timetable
                Timetable timetable = Main.getWindow().getTimetable();
                day1Activities = timetable.getDayActivities(timetable.getCurrentDay());
                day2Activities = readDay1Activities;

            } else {

                // 2 days before - we can disregard both lists and get from timetable
                Timetable timetable = Main.getWindow().getTimetable();
                day1Activities = timetable.getDayActivities(timetable.getCurrentDay());
                day2Activities = timetable.getDayActivities(timetable.getIndexOnDay(LocalDate.now().minusDays(1)));
            }

            return makePromptsFromActivities(day1Activities,day2Activities);
        } catch (IOException e) {
            System.err.println("Something went wrong while reading scheduledRevisionManager");
            System.err.println(e.getMessage());
            System.err.println(e.getClass());
            System.err.println(Arrays.toString(e.getStackTrace()));

            // Get the default full thing anyway
            Timetable timetable = Main.getWindow().getTimetable();
            TimetableActivity[] day1Activities = timetable.getDayActivities(timetable.getCurrentDay());
            TimetableActivity[] day2Activities = timetable.getDayActivities(timetable.getIndexOnDay(LocalDate.now().minusDays(1)));
            return makePromptsFromActivities(day1Activities,day2Activities);

        }
    }
    private static DocumentPrompt[] makePromptsFromActivities(TimetableActivity[] day1Activities, TimetableActivity[] day2Activities) {
        DocumentPrompt[] prompts = new DocumentPrompt[day2Activities.length+day1Activities.length];
        LocalDate currentDate = LocalDate.now();
        LocalDate thisDate = currentDate.minusDays(1);
        // Do the later day first
        for (int index = 0; index < day2Activities.length; index++) {
            TimetableActivity activity = day2Activities[index];
            prompts[index] = new DocumentPrompt(activity.name(), activity.activityIndex(), thisDate);
        }

        // Do day 1

        for (int index = 0; index < day1Activities.length; index++) {
            int promptsIndex = index + day2Activities.length;
            TimetableActivity activity = day1Activities[index];
            prompts[promptsIndex] = new DocumentPrompt(activity.name(), activity.activityIndex(), currentDate);
        }

        return prompts;
    }
    public static void updatePromptFile(ArrayList<DocumentPrompt> prompts) {
        // Sort the prompts into two days
        ArrayList<DocumentPrompt> currentDay = new ArrayList<>();
        ArrayList<DocumentPrompt> previousDay = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate yesterdayDate = currentDate.minusDays(1);
        for (DocumentPrompt prompt: prompts) {
            if (currentDate.isEqual(prompt.date())) {
                currentDay.add(prompt);
            } else if (yesterdayDate.isEqual(prompt.date())) {
                previousDay.add(prompt);
            }
            // Otherwise do nothing
        }

        try {
            File file = new File(saveLocation);
            if (!file.exists()) {
                file.createNewFile();
            }

            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            // Write the current day
            Document.writeString(Main.getStringFromDate(currentDate), out);

            writeActivities(currentDay.toArray(new DocumentPrompt[0]), out);
            writeActivities(previousDay.toArray(new DocumentPrompt[0]), out);

            out.close();
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getClass());
            System.err.println(e.getStackTrace());
            System.err.println(e.getMessage());
        }
    }
}
