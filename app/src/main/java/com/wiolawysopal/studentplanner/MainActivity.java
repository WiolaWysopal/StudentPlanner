package com.wiolawysopal.studentplanner;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
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
    private TaskAdapter taskAdapter;

    //    0 = Due date
    //    1 = Newest
    //    2 = Oldest
    private int currentSort = 0;

    // 0 = All
    // 1 = Active
    // 2 = Completed
    private int currentFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "student_planner_database"
        ).fallbackToDestructiveMigration().build();

        databaseExecutor = Executors.newSingleThreadExecutor();

        tasks = new ArrayList<>();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskAdapter = new TaskAdapter(
                tasks,
                task -> {
                    Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                    intent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, task.getId());
                    startActivity(intent);
                },
                task -> {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.delete_task_title)
                            .setMessage(R.string.delete_task_message)
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                databaseExecutor.execute(() -> {
                                    database.taskDao().delete(task);

                                    runOnUiThread(() -> {
                                        int position = tasks.indexOf(task);

                                        if (position != -1) {
                                            tasks.remove(position);
                                            taskAdapter.notifyItemRemoved(position);
                                        }

                                        updateEmptyState();
                                    });
                                });
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                },
                (task, completed) -> {
                    task.setCompleted(completed);

                    databaseExecutor.execute(() -> {
                        database.taskDao().update(task);
                    });
                }
        );

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        emptyTasksTextView = findViewById(R.id.emptyTasksTextView);
        emptyTasksHintTextView = findViewById(R.id.emptyTasksHintTextView);

        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String taskTitle = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_TITLE);

                        String taskDescription = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_DESCRIPTION);

                        String taskDueDate = result.getData()
                                .getStringExtra(AddTaskActivity.EXTRA_TASK_DUE_DATE);

                        String taskSubject = result.getData().getStringExtra(AddTaskActivity.EXTRA_TASK_SUBJECT);

                        Task task = new Task(taskTitle, taskDescription, taskDueDate, taskSubject);

                        databaseExecutor.execute(() -> {
                            long taskId = database.taskDao().insert(task);
                            task.setId((int) taskId);

                            runOnUiThread(() -> {
                               tasks.add(task);
                               taskAdapter.notifyItemInserted(tasks.size() - 1);
                               updateEmptyState();
                            });
                        });
                    }
                }
        );

        Button filterTasksButton = findViewById(R.id.filterTasksButton);

        filterTasksButton.setOnClickListener(v -> {
            String[] filterOptions = {
                    getString(R.string.filter_all),
                    getString(R.string.filter_active),
                    getString(R.string.filter_completed)
            };

            new AlertDialog.Builder(this)
                    .setTitle(R.string.filter_by)
                    .setItems(filterOptions, (dialog, which) -> {
                        currentFilter = which;
                        loadTasks();
                    })
                    .show();
        });

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

        Button sortTasksButton = findViewById(R.id.sortTasksButton);

        sortTasksButton.setOnClickListener(v -> {
            String[] sortOptions = {
                    getString(R.string.sort_due_date),
                    getString(R.string.sort_newest),
                    getString(R.string.sort_oldest)
            };

            new AlertDialog.Builder(this)
                    .setTitle(R.string.sort_by)
                    .setItems(sortOptions, (dialog, which) -> {
                        currentSort = which;
                        loadTasks();
                    })
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database == null || databaseExecutor == null || taskAdapter == null) {
            return;
        }

        loadTasks();
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

    private void loadTasks() {
        databaseExecutor.execute(() -> {
            List<Task> savedTasks;

            if (currentSort == 0) {
                savedTasks = database.taskDao().getAllSortedByDueDate();
            } else if (currentSort == 1) {
                savedTasks = database.taskDao().getAllSortedByNewest();
            } else {
                savedTasks = database.taskDao().getAllSortedByOldest();
            }

            List<Task> filteredTasks = new ArrayList<>();

            for (Task task : savedTasks) {
                if (currentFilter == 0
                        || (currentFilter == 1 && !task.isCompleted())
                        || (currentFilter == 2 && task.isCompleted())) {
                    filteredTasks.add(task);
                }
            }

            runOnUiThread(() -> {
                tasks.clear();
                tasks.addAll(filteredTasks);
                taskAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        });
    }

}