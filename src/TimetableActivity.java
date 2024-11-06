/**
 * A record of an activity, in a timetable. Contains the name of the activity, and the index for a parent's configuredActivities array.
 * @param name The name of the activity
 * @param activityIndex The index in a parent timetable's configuredActivities array. Note that this can be equal to the length of the array - in this case it should be treated as "None"
 */
public record TimetableActivity(String name, int activityIndex) {}
