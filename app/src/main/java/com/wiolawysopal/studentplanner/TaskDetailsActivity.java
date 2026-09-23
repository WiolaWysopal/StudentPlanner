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

    private TextView taskTitleTextView;
    private TextView taskDescriptionTextView;
    private TextView taskDueDateTextView;

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
                        String updatedDescription = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_DESCRIPTION);

                        String updatedDueDate = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_DUE_DATE);
                        currentTask.setTitle(updatedTitle);
                        currentTask.setDescription(updatedDescription);
                        currentTask.setDueDate(updatedDueDate);
                        databaseExecutor.execute(() -> {
                            database.taskDao().update(currentTask);

                            runOnUiThread(this::displayTaskDetails);
                        });
                    }
                }
        );
        taskTitleTextView =
                findViewById(R.id.taskDetailsTaskTitleTextView);

        taskDescriptionTextView =
                findViewById(R.id.taskDetailsDescriptionTextView);

        taskDueDateTextView =
                findViewById(R.id.taskDetailsDueDateTextView);

        Button editTaskButton =
                findViewById(R.id.editTaskButton);

        editTaskButton.setOnClickListener(v -> {
            if (currentTask == null) {
                return;
            }
            Intent intent = new Intent(TaskDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra(
                    AddTaskActivity.EXTRA_EDIT_TASK_TITLE,
                    currentTask.getTitle()
            );

            intent.putExtra(
                    AddTaskActivity.EXTRA_EDIT_TASK_DESCRIPTION,
                    currentTask.getDescription()
            );

            intent.putExtra(
                    AddTaskActivity.EXTRA_EDIT_TASK_DUE_DATE,
                    currentTask.getDueDate()
            );

            editTaskLauncher.launch(intent);
        });

        databaseExecutor.execute(() -> {
            currentTask = database.taskDao().getById(taskId);
            if (currentTask != null) {
                runOnUiThread(this::displayTaskDetails);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void displayTaskDetails() {
        if (currentTask == null) {
            return;
        }

        taskTitleTextView.setText(currentTask.getTitle());

        String description = currentTask.getDescription();
        if (description == null || description.isEmpty()) {
            taskDescriptionTextView.setText(R.string.no_task_description);
        } else {
            taskDescriptionTextView.setText(description);
        }

        String dueDate = currentTask.getDueDate();
        if (dueDate == null || dueDate.isEmpty()) {
            taskDueDateTextView.setText(R.string.no_task_due_date);
        } else {
            taskDueDateTextView.setText(dueDate);
        }
    }
}