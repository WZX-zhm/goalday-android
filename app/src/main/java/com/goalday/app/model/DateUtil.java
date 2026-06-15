package com.goalday.app.model;

import java.util.Calendar;

public class DateUtil {
    /** 把当前时间序列化为 yyyyMMdd 整数 */
    public static long today() {
        return toKey(System.currentTimeMillis());
    }

    public static long toKey(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return c.get(Calendar.YEAR) * 10000L
                + (c.get(Calendar.MONTH) + 1) * 100L
                + c.get(Calendar.DAY_OF_MONTH);
    }

    public static long prevDay(long key) {
        return toKey(parseTime(key) - 24L * 60 * 60 * 1000);
    }

    public static long nextDay(long key) {
        return toKey(parseTime(key) + 24L * 60 * 60 * 1000);
    }

    public static long parseTime(long key) {
        int y = (int) (key / 10000);
        int m = (int) ((key / 100) % 100) - 1;
        int d = (int) (key % 100);
        Calendar c = Calendar.getInstance();
        c.set(y, m, d, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** 0=周日，1=周一 ... 6=周六 */
    public static int dayOfWeek(long key) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(parseTime(key));
        int d = c.get(Calendar.DAY_OF_WEEK) - 1; // Sunday=0
        return d;
    }

    /** 周一对应的 key */
    public static long mondayOfWeek(long key) {
        int dow = dayOfWeek(key);
        int offset = (dow == 0) ? 6 : dow - 1; // 周一为起点
        long cur = parseTime(key);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cur);
        c.add(Calendar.DAY_OF_MONTH, -offset);
        return toKey(c.getTimeInMillis());
    }

    public static String formatDate(long key) {
        int y = (int) (key / 10000);
        int m = (int) ((key / 100) % 100);
        int d = (int) (key % 100);
        return y + "年" + m + "月" + d + "日";
    }

    public static String formatWeekCN(long key) {
        String[] arr = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        return arr[dayOfWeek(key)];
    }

    /** 中文短：06.13 */
    public static String formatShort(long key) {
        int m = (int) ((key / 100) % 100);
        int d = (int) (key % 100);
        return String.format("%02d.%02d", m, d);
    }
}
