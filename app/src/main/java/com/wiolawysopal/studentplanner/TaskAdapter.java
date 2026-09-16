package com.wiolawysopal.studentplanner;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<Task> tasks;
    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitleTextView.setText(task.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitleTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTitleTextView = itemView.findViewById(R.id.taskTitleTextView);
        }

    }
}
