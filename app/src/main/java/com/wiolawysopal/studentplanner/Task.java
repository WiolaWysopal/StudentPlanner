package com.wiolawysopal.studentplanner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public Task(String title) {
        this.title = title;
    };

    public String getTitle() {
        return title;
    };

    public void setTitle(String title) {
        this.title = title;
    }
}
