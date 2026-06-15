package com.goalday.app.model;

import java.util.HashSet;
import java.util.Set;

public class Habit {
    public long id;
    public String name;
    public String icon;
    public long createdAt;
    /** 用逗号分隔的日期字符串，存储已打卡的 yyyyMMdd */
    public String checkins = "";

    public Habit() {}

    public Habit(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.createdAt = System.currentTimeMillis();
    }

    public Set<Long> getCheckinSet() {
        Set<Long> set = new HashSet<>();
        if (checkins == null || checkins.isEmpty()) return set;
        for (String s : checkins.split(",")) {
            if (s.isEmpty()) continue;
            try {
                set.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {}
        }
        return set;
    }

    public void addCheckin(long date) {
        Set<Long> set = getCheckinSet();
        set.add(date);
        StringBuilder sb = new StringBuilder();
        for (Long d : set) {
            if (sb.length() > 0) sb.append(",");
            sb.append(d);
        }
        this.checkins = sb.toString();
    }

    public boolean isCheckedToday(long today) {
        return getCheckinSet().contains(today);
    }

    /** 计算连续打卡天数（从今天往前数） */
    public int getStreak(long today) {
        Set<Long> set = getCheckinSet();
        int streak = 0;
        long d = today;
        // 倒推到这一周的周一
        while (set.contains(d)) {
            streak++;
            d = DateUtil.prevDay(d);
        }
        return streak;
    }
}
