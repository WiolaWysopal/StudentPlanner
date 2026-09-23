package com.wiolawysopal.studentplanner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

import androidx.room.Room;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;

public class TaskDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "TASK_ID";
    private AppDatabase database;
    private ExecutorService databaseExecutor;

    private ActivityResultLauncher<Intent> editTaskLauncher;
    private Task currentTask;

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
        ).fallbackToDestructiveMigration().build();
        databaseExecutor = Executors.newSingleThreadExecutor();
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && currentTask != null) {
                        String updatedTitle = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_TITLE);
                        currentTask.setTitle(updatedTitle);
                        databaseExecutor.execute(() -> {
                            database.taskDao().update(currentTask);

                            runOnUiThread(() -> {
                                TextView taskTitleTextView = findViewById(R.id.taskDetailsTaskTitleTextView);
                                taskTitleTextView.setText(currentTask.getTitle());
                            });
                        });


                    }
                }
        );
        TextView taskTitleTextView = findViewById(R.id.taskDetailsTaskTitleTextView);

        Button editTaskButton = findViewById(R.id.editTaskButton);

        editTaskButton.setOnClickListener(v -> {
            if (currentTask == null) {
                return;
            }
            Intent intent = new Intent(TaskDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra(
                    AddTaskActivity.EXTRA_EDIT_TASK_TITLE,
                    currentTask.getTitle()
            );

            editTaskLauncher.launch(intent);
        });

        databaseExecutor.execute(() -> {
            currentTask = database.taskDao().getById(taskId);
            if (currentTask != null) {
                runOnUiThread(() -> {
                    taskTitleTextView.setText(currentTask.getTitle());
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