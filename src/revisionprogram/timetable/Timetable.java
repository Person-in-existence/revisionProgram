package revisionprogram.timetable;

import revisionprogram.Day;
import revisionprogram.Main;
import revisionprogram.documents.Document;
import revisionprogram.files.FileException;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class Timetable {
    private static final String saveLocation = Main.saveRoot + "timetable";
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
                    // int activitiesIndex
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
            int index = (int) ((between % days.length)+days.length) % days.length; // Make sure it is not negative
            return index;
        } else {
            return -1;
        }
    }

    public TimetableActivity[] getDayActivities(int dayIndex) {
        if (dayIndex == -1) {
            return new TimetableActivity[0];
        }
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
    public String[] getConfiguredActivities() {
        return configuredActivities;
    }

    /**
     * Adds a subject to the timetable, then saves the changes to file.
     * For internal use - the Main.addSubject() method should be used for general use - it ensure the UI is consistent.
     *
     * @param name The name of the new subject to add
     * @return A file exception from the writeToFile() method
     */
    public FileException addSubject(String name) {
        String[] newConfiguredActivities = new String[configuredActivities.length + 1];

        // Copy the old configured activities into the start of newConfiguredActivities
        System.arraycopy(configuredActivities, 0, newConfiguredActivities, 0, configuredActivities.length);

        // Add the name to the new version
        newConfiguredActivities[newConfiguredActivities.length-1] = name;

        configuredActivities = newConfiguredActivities;

        // Save changes to file
        return writeToFile();
    }

    /**
     * Deletes a subject from the timetable, then saves the changes to file.
     * Any activities with the deleted subject will also be deleted.
     * For internal use - the Main.deleteSubject() method should be used for general use - this only updates the timetable.
     *
     * @param name The name of the subject to delete
     * @return A file exception from the writeToFile() method, for whether saving to file was successful. A file exception with false will be returned if the subject could not be found.
     */
    public FileException deleteSubject(String name) {
        // Must adjust all indices in days


        ArrayList<String> newConfiguredActivities = new ArrayList<>();
        // Get the index of the subject to remove
        int subjectIndex = -1;
        for (int index = 0; index < configuredActivities.length; index++) {
            if (Objects.equals(configuredActivities[index], name)) {
                subjectIndex = index;
            } else {
                newConfiguredActivities.add(configuredActivities[index]);
            }
        }
        // Check the subject actually exists
        if (subjectIndex < 0) {
            System.out.println("Call to delete subject " + name + " in timetable found no such subject. Returned without edits");
            return new FileException(false, "");
        }

        // Update all the days in the timetable
        for (Day day : days) {
            day.subjectDeleted(subjectIndex);
        }

        configuredActivities = newConfiguredActivities.toArray(new String[0]);

        // Save the changes to file
        return writeToFile();
    }
}
