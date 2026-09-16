package com.wiolawysopal.studentplanner;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addTaskLauncher;
    private ArrayList<Task> tasks;
    private AppDatabase database;
    private ExecutorService databaseExecutor;
    private TextView emptyTasksTextView;
    private TextView emptyTasksHintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "student_planner_database"
        ).build();

        databaseExecutor = Executors.newSingleThreadExecutor();

        tasks = new ArrayList<>();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        TaskAdapter taskAdapter = new TaskAdapter(tasks);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        emptyTasksTextView = findViewById(R.id.emptyTasksTextView);
        emptyTasksHintTextView = findViewById(R.id.emptyTasksHintTextView);

        databaseExecutor.execute(() -> {
            List<Task> savedTasks = database.taskDao().getAll();

            runOnUiThread(() -> {
                tasks.addAll(savedTasks);
                taskAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        });

        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String taskTitle = result.getData().getStringExtra(AddTaskActivity.EXTRA_TASK_TITLE);
                        Task task = new Task(taskTitle);

                        databaseExecutor.execute(() -> {
                            database.taskDao().insert(task);
                        });

                        tasks.add(task);
                        taskAdapter.notifyItemInserted(tasks.size()-1);
                        updateEmptyState();
                    }
                }
        );
        Button addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> {
           Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
           addTaskLauncher.launch(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateEmptyState() {
        if (tasks.isEmpty()) {
            emptyTasksTextView.setVisibility(View.VISIBLE);
            emptyTasksHintTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTasksTextView.setVisibility(View.GONE);
            emptyTasksHintTextView.setVisibility(View.GONE);
        }
    }
}