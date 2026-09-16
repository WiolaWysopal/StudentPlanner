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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addTaskLauncher;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        tasks = new ArrayList<>();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        TaskAdapter taskAdapter = new TaskAdapter(tasks);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        TextView emptyTasksTextView = findViewById(R.id.emptyTasksTextView);
        TextView emptyTasksHintTextView = findViewById(R.id.emptyTasksHintTextView);

        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String taskTitle = result.getData().getStringExtra("TASK_TITLE");
                        Task task = new Task(taskTitle);
                        tasks.add(task);
                        taskAdapter.notifyItemInserted(tasks.size()-1);
                        emptyTasksTextView.setVisibility(View.GONE);
                        emptyTasksHintTextView.setVisibility(View.GONE);
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
}