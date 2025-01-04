package revisionprogram.timetable;

import revisionprogram.Day;
import revisionprogram.Main;
import revisionprogram.documents.Document;
import revisionprogram.files.FileException;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Timetable {
    private static final String saveLocation = "timetable";
    public Day[] days;
    public String[] configuredActivities;
    public LocalDate startDate;

    public Timetable(LocalDate startDate, int startDayIndex, Day[] days, String[] configuredActivities) {
        this.days = days;
        this.configuredActivities = configuredActivities;

        // Subtract the dayIndex so that startDayIndex is always 0
        startDate = startDate.minusDays(startDayIndex);
        this.startDate = startDate;
    }
    public Timetable() {
        this.days = new Day[0];
        this.configuredActivities = new String[0];
        this.startDate = LocalDate.now();
    }

    public FileException writeToFile() {
        // Create the file and check for possible errors
        try {
            File file = new File(saveLocation);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return new FileException(true, Main.strings.getString("createFileFail"));
                }
            }
            if (!file.canWrite()) {
                return new FileException(true, Main.strings.getString("cannotWriteToFile"));
            }
            if (file.isDirectory()) {
                return new FileException(true, Main.strings.getString("fileIsDirectory"));
            }

            // Create the file streams
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fos);

            // String startDate
            String startDate = Main.getStringFromDate(this.startDate);
            Document.writeString(startDate, out);

            // long numActivities
            int numActivities = configuredActivities.length;
            out.writeInt(numActivities);

            // Activities
            for (int index = 0; index < numActivities; index++) {
                Document.writeString(configuredActivities[index], out);
            }


            // int numDays
            int numDays = days.length;
            out.writeInt(numDays);

            for (int index = 0; index < numDays; index++) {
                // For every day in days
                // string dayName
                String dayName = days[index].name;
                Document.writeString(dayName, out);
                // int numActivities
                int numDayActivities = days[index].activities.length;
                out.writeInt(numDayActivities);

                for (int activitiesIndex = 0; activitiesIndex < numDayActivities; activitiesIndex++) {
                    // String activity name
                    Document.writeString(days[index].activities[activitiesIndex].name(), out);
                    // long activitiesIndex
                    int activityIndex = days[index].activities[activitiesIndex].activityIndex();
                    out.writeInt(activityIndex);
                }
            }

            out.close();
            fos.close();

        } catch (Exception e) {
            System.err.println("Error");
            e.printStackTrace();
            return new FileException(true, e.getMessage());
        }
        return new FileException(false, "");
    }
    public FileException readFromFile() {
        try {
            // Make the file and check that it exists
            File file = new File(saveLocation);
            if (!file.exists()) {
                return new FileException(true, Main.strings.getString("noFile"));
            }
            if (!file.canRead()) {
                return new FileException(true, Main.strings.getString("cantRead"));
            }
            if (file.isDirectory()) {
                return new FileException(true, Main.strings.getString("fileIsDirectory"));
            }

            // Make the streams
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);

            /// String startDate
            String startDate = Document.readString(in);
            this.startDate = Main.getDateFromString(startDate);

            /// Activity Types
            int numActivities = in.readInt();
            configuredActivities = new String[numActivities];
            for (int index = 0; index < numActivities; index++) {
                configuredActivities[index] = Document.readString(in);
            }

            /// Timetable
            int numDays = in.readInt();
            days = new Day[numDays];

            for (int index = 0; index < numDays; index++) {
                String dayName = Document.readString(in);
                int numDayActivities = in.readInt();
                TimetableActivity[] activities = new TimetableActivity[numDayActivities];
                for (int activityIndex = 0; activityIndex < numDayActivities; activityIndex++) {
                    String activityName = Document.readString(in);
                    int activityTypeIndex = in.readInt();
                    activities[activityIndex] = new TimetableActivity(activityName, activityTypeIndex);
                }
                days[index] = new Day(activities, dayName);
            }


            in.close();
            fis.close();
            return new FileException(false, "");
        } catch (Exception e) {
            return new FileException(true, e.getMessage());
        }

    }
    public int getIndexOnDay(LocalDate day) {
        if (days.length != 0) {
            long between = ChronoUnit.DAYS.between(this.startDate, day);
            int index = (int) (between % days.length);
            return index;
        } else {
            return -1;
        }
    }

    public TimetableActivity[] getDayActivities(int dayIndex) {
        Day day = days[dayIndex];
        return day.activities;
    }
    public String[] getActivities() {
        return configuredActivities;
    }
    public int getCurrentDay() {
        return getIndexOnDay(LocalDate.now());
    }
    public String[] getDayActivityNames(int dayIndex) {
        TimetableActivity[] dayActivities = getDayActivities(dayIndex);
        String[] names = new String[dayActivities.length];
        for (int index = 0; index < dayActivities.length; index++) {
            int configuredActivitiesIndex = dayActivities[index].activityIndex();
            names[index] = configuredActivities[configuredActivitiesIndex];
        }
        return names;
    }

}
