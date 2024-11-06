import java.io.*;
import java.util.ArrayList;

public class Timetable {
    private static final String saveLocation = "timetable";
    public Day[] days;
    public String[] configuredActivities;
    public Timetable(Day[] days, String[] configuredActivities) {
        this.days = days;
        this.configuredActivities = configuredActivities;
    }
    public Timetable() {
        this.days = new Day[0];
        this.configuredActivities = new String[0];
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
}
