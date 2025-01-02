package revisionprogram;

import revisionprogram.timetable.TimetableActivity;

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
}
