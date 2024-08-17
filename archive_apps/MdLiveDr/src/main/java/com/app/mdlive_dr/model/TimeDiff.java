package com.app.mdlive_dr.model;

/**
 * Created by Engr G M on 6/6/2017.
 */

public class TimeDiff {

    public long diffSeconds;
    public long diffMinutes;
    public long diffHours;
    public long diffDays;

    public TimeDiff(long diffSeconds, long diffMinutes, long diffHours, long diffDays) {
        this.diffSeconds = diffSeconds;
        this.diffMinutes = diffMinutes;
        this.diffHours = diffHours;
        this.diffDays = diffDays;
    }
}
