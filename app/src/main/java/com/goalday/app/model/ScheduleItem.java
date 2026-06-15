package com.goalday.app.model;

public class ScheduleItem {
    public long id;
    public long date;          // yyyyMMdd
    public int hour;           // 0-23
    public int minute;         // 0-59
    public String content;
    public boolean done;
    public long createdAt;

    public ScheduleItem() {}

    public ScheduleItem(long date, int hour, int minute, String content) {
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.content = content;
        this.done = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
