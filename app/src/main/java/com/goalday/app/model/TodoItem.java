package com.goalday.app.model;

public class TodoItem {
    public long id;
    public String content;
    public long createdAt;

    public TodoItem() {}

    public TodoItem(String content) {
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }
}
