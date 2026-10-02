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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    // null = All subjects
    private String currentSubject = null;

    // 0 = All dates
    // 1 = Today
    // 2 = Upcoming
    // 3 = Overdue
    // 4 = No due date
    private int currentDueDateFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);

        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "student_planner_database"
        ).fallbackToDestructiveMigration().build();

        databaseExecutor = Executors.newSingleThreadExecutor();

        tasks = new ArrayList<>();

        if (savedInstanceState != null) {
            currentSort = savedInstanceState.getInt("current_sort", 0);
            currentFilter = savedInstanceState.getInt("current_filter", 0);
            currentSubject = savedInstanceState.getString("current_subject");
            currentDueDateFilter = savedInstanceState.getInt("current_due_date_filter", 0);
        }

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

                        if (currentFilter != 0) {
                            runOnUiThread(this::loadTasks);
                        }
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

        Button dueDateFilterButton = findViewById(R.id.dueDateFilterButton);

        dueDateFilterButton.setOnClickListener(v -> {
            String[] dueDateFilterOptions = {
                    getString(R.string.filter_all_dates),
                    getString(R.string.filter_today),
                    getString(R.string.filter_upcoming),
                    getString(R.string.filter_overdue),
                    getString(R.string.filter_no_due_date)
            };

            new AlertDialog.Builder(this)
                    .setTitle(R.string.filter_by_due_date)
                    .setItems(dueDateFilterOptions, (dialog, which) -> {
                        currentDueDateFilter = which;
                        loadTasks();
                    })
                    .show();
        });

        Button subjectFilterButton = findViewById(R.id.subjectFilterButton);

        subjectFilterButton.setOnClickListener(v -> {
            databaseExecutor.execute(() -> {
                List<Task> allTasks = database.taskDao().getAllSortedByDueDate();

                List<String> subjects = new ArrayList<>();

                for (Task task : allTasks) {
                    String subject = task.getSubject();

                    if (subject != null
                            && !subject.trim().isEmpty()
                            && !subjects.contains(subject)) {
                        subjects.add(subject);
                    }
                }

                runOnUiThread(() -> {
                    List<String> options = new ArrayList<>();
                    options.add(getString(R.string.all_subjects));
                    options.addAll(subjects);

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.filter_by_subject)
                            .setItems(options.toArray(new String[0]), (dialog, which) -> {
                                if (which == 0) {
                                    currentSubject = null;
                                } else {
                                    currentSubject = options.get(which);
                                }

                                loadTasks();
                            })
                            .show();
                });
            });
        });

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("current_sort", currentSort);
        outState.putInt("current_filter", currentFilter);
        outState.putString("current_subject", currentSubject);
        outState.putInt("current_due_date_filter", currentDueDateFilter);
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
                boolean matchesStatus =
                        currentFilter == 0
                                || (currentFilter == 1 && !task.isCompleted())
                                || (currentFilter == 2 && task.isCompleted());

                boolean matchesSubject =
                        currentSubject == null
                                || currentSubject.equals(task.getSubject());

                boolean matchesDueDate = matchesDueDateFilter(task);

                if (matchesStatus && matchesSubject && matchesDueDate) {
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

    private boolean matchesDueDateFilter(Task task) {
        String dueDate = task.getDueDate();

        if (currentDueDateFilter == 0) {
            return true;
        }

        if (currentDueDateFilter == 4) {
            return dueDate == null || dueDate.trim().isEmpty();
        }

        if (dueDate == null || dueDate.trim().isEmpty()) {
            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateFormat.setLenient(false);

        try {
            Date taskDate = dateFormat.parse(dueDate);
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if (taskDate == null || today == null) {
                return false;
            }

            if (currentDueDateFilter == 1) {
                return taskDate.equals(today);
            }

            if (currentDueDateFilter == 2) {
                return taskDate.after(today);
            }

            if (currentDueDateFilter == 3) {
                return taskDate.before(today);
            }

        } catch (ParseException e) {
            return false;
        }

        return true;
    }

}