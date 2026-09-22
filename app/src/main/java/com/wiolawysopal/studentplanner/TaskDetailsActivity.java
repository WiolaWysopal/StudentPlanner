package com.wiolawysopal.studentplanner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

import androidx.room.Room;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "TASK_ID";
    private AppDatabase database;
    private ExecutorService databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        int taskId = getIntent().getIntExtra(EXTRA_TASK_ID, -1);
        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "student_planner_database"
        ).build();
        databaseExecutor = Executors.newSingleThreadExecutor();
        TextView taskTitleTextView = findViewById(R.id.taskDetailsTaskTitleTextView);
        databaseExecutor.execute(() -> {
            Task task = database.taskDao().getById(taskId);

            if (task != null) {
                runOnUiThread(() -> {
                    taskTitleTextView.setText(task.getTitle());
                });
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}