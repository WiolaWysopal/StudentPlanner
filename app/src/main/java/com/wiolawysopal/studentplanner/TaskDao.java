package com.wiolawysopal.studentplanner;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    List<Task> getAll();

    @Query("SELECT * FROM tasks ORDER BY " +
            "CASE WHEN dueDate IS NULL OR dueDate = '' THEN 1 ELSE 0 END, " +
            "substr(dueDate, 7, 4) || substr(dueDate, 4, 2) || substr(dueDate, 1, 2) ASC")
    List<Task> getAllSortedByDueDate();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getById(int id);
}
