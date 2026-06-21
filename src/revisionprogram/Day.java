package revisionprogram;

import revisionprogram.timetable.TimetableActivity;

import java.util.ArrayList;

public class Day {
    public TimetableActivity[] activities;
    public String name;
    public Day(TimetableActivity[] activities, String name) {
        this.activities = activities;
        this.name = name;
    }
    public Day() {
        this.activities = new TimetableActivity[0];
        this.name = "";
    }

    public void subjectDeleted(int index) {
        ArrayList<TimetableActivity> newActivities = new ArrayList<>();
        for (TimetableActivity oldActivity : activities) {
            if (oldActivity.activityIndex() < index) {
                // Unaffected
                newActivities.add(oldActivity);
            } else if (oldActivity.activityIndex() > index) {
                // Take 1 away from the activity
                TimetableActivity activity = new TimetableActivity(oldActivity.name(), oldActivity.activityIndex() - 1);
                newActivities.add(activity);
            }
            // Don't add the activity if the subject was deleted
        }

        // Set activities to be the edited version
        activities = newActivities.toArray(new TimetableActivity[]{});

    }
}
