package com.goalday.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.goalday.app.model.Diary;
import com.goalday.app.model.Habit;
import com.goalday.app.model.ScheduleItem;
import com.goalday.app.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class GoalDayDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "goalday.db";
    private static final int VERSION = 1;

    public GoalDayDb(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE schedule (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date INTEGER, hour INTEGER, minute INTEGER, " +
                "content TEXT, done INTEGER, created_at INTEGER)");

        db.execSQL("CREATE TABLE todo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "content TEXT, created_at INTEGER)");

        db.execSQL("CREATE TABLE diary (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date INTEGER UNIQUE, content TEXT, mood TEXT, updated_at INTEGER)");

        db.execSQL("CREATE TABLE habit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, icon TEXT, checkins TEXT, created_at INTEGER)");

        // 内置示例
        seedHabits(db);
    }

    private void seedHabits(SQLiteDatabase db) {
        String[][] presets = {
                {"每日阅读", "📚"},
                {"早起", "🌅"},
                {"运动", "🏃"},
                {"喝水 2L", "💧"},
                {"冥想", "🧘"},
                {"练字", "✍️"},
                {"学习英语", "🌍"},
                {"戒糖", "🥗"},
                {"早睡", "🌙"}
        };
        for (String[] p : presets) {
            ContentValues cv = new ContentValues();
            cv.put("name", p[0]);
            cv.put("icon", p[1]);
            cv.put("checkins", "");
            cv.put("created_at", System.currentTimeMillis());
            db.insert("habit", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 未来升级用
    }

    // ============== Schedule ==============

    public long insertSchedule(ScheduleItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", item.date);
        cv.put("hour", item.hour);
        cv.put("minute", item.minute);
        cv.put("content", item.content);
        cv.put("done", item.done ? 1 : 0);
        cv.put("created_at", item.createdAt);
        long id = db.insert("schedule", null, cv);
        item.id = id;
        return id;
    }

    public void updateScheduleDone(long id, boolean done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("done", done ? 1 : 0);
        db.update("schedule", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteSchedule(long id) {
        getWritableDatabase().delete("schedule", "id=?", new String[]{String.valueOf(id)});
    }

    public List<ScheduleItem> getScheduleByDate(long date) {
        List<ScheduleItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,date,hour,minute,content,done,created_at FROM schedule " +
                        "WHERE date=? ORDER BY hour, minute", new String[]{String.valueOf(date)});
        while (c.moveToNext()) {
            ScheduleItem it = new ScheduleItem();
            it.id = c.getLong(0);
            it.date = c.getLong(1);
            it.hour = c.getInt(2);
            it.minute = c.getInt(3);
            it.content = c.getString(4);
            it.done = c.getInt(5) == 1;
            it.createdAt = c.getLong(6);
            list.add(it);
        }
        c.close();
        return list;
    }

    public List<ScheduleItem> getCompletedByDate(long date) {
        List<ScheduleItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,date,hour,minute,content,done,created_at FROM schedule " +
                        "WHERE date=? AND done=1 ORDER BY hour, minute",
                new String[]{String.valueOf(date)});
        while (c.moveToNext()) {
            ScheduleItem it = new ScheduleItem();
            it.id = c.getLong(0);
            it.date = c.getLong(1);
            it.hour = c.getInt(2);
            it.minute = c.getInt(3);
            it.content = c.getString(4);
            it.done = c.getInt(5) == 1;
            it.createdAt = c.getLong(6);
            list.add(it);
        }
        c.close();
        return list;
    }

    public int countAllByDate(long date) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM schedule WHERE date=?", new String[]{String.valueOf(date)});
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    // ============== Todo ==============

    public long insertTodo(TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("content", item.content);
        cv.put("created_at", item.createdAt);
        long id = db.insert("todo", null, cv);
        item.id = id;
        return id;
    }

    public void deleteTodo(long id) {
        getWritableDatabase().delete("todo", "id=?", new String[]{String.valueOf(id)});
    }

    public List<TodoItem> getAllTodos() {
        List<TodoItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,content,created_at FROM todo ORDER BY id DESC", null);
        while (c.moveToNext()) {
            TodoItem it = new TodoItem();
            it.id = c.getLong(0);
            it.content = c.getString(1);
            it.createdAt = c.getLong(2);
            list.add(it);
        }
        c.close();
        return list;
    }

    // ============== Diary ==============

    public void saveDiary(Diary d) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM diary WHERE date=?", new String[]{String.valueOf(d.date)});
        if (c.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("content", d.content);
            cv.put("mood", d.mood);
            cv.put("updated_at", d.updatedAt);
            db.update("diary", cv, "date=?", new String[]{String.valueOf(d.date)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put("date", d.date);
            cv.put("content", d.content);
            cv.put("mood", d.mood);
            cv.put("updated_at", d.updatedAt);
            db.insert("diary", null, cv);
        }
        c.close();
    }

    public Diary getDiaryByDate(long date) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,date,content,mood,updated_at FROM diary WHERE date=?",
                new String[]{String.valueOf(date)});
        Diary d = null;
        if (c.moveToFirst()) {
            d = new Diary();
            d.id = c.getLong(0);
            d.date = c.getLong(1);
            d.content = c.getString(2);
            d.mood = c.getString(3);
            d.updatedAt = c.getLong(4);
        }
        c.close();
        return d;
    }

    // ============== Habit ==============

    public long insertHabit(Habit h) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", h.name);
        cv.put("icon", h.icon);
        cv.put("checkins", h.checkins);
        cv.put("created_at", h.createdAt);
        long id = db.insert("habit", null, cv);
        h.id = id;
        return id;
    }

    public void updateHabit(Habit h) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", h.name);
        cv.put("icon", h.icon);
        cv.put("checkins", h.checkins);
        db.update("habit", cv, "id=?", new String[]{String.valueOf(h.id)});
    }

    public void deleteHabit(long id) {
        getWritableDatabase().delete("habit", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Habit> getAllHabits() {
        List<Habit> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,name,icon,checkins,created_at FROM habit ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Habit h = new Habit();
            h.id = c.getLong(0);
            h.name = c.getString(1);
            h.icon = c.getString(2);
            h.checkins = c.getString(3);
            h.createdAt = c.getLong(4);
            list.add(h);
        }
        c.close();
        return list;
    }
}
