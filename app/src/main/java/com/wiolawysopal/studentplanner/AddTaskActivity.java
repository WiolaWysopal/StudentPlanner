package com.wiolawysopal.studentplanner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;

import android.content.Intent;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
        EditText taskTitleEditText = findViewById(R.id.taskTitleEditText);
        Button saveTaskButton = findViewById(R.id.saveTaskButton);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        saveTaskButton.setOnClickListener(v -> {
            String taskTitle = taskTitleEditText.getText().toString().trim();
            if (taskTitle.isEmpty()) {
                taskTitleEditText.setError(getString(R.string.task_title_required));
                return;
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("TASK_TITLE", taskTitle);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}