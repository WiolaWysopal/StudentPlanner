package com.wiolawysopal.studentplanner;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    List<Task> getAll();
}
