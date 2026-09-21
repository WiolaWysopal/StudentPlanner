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
    private OnTaskClickListener listener;
    private OnTaskLongClickListener longClickListener;
    public TaskAdapter(ArrayList<Task> tasks, OnTaskClickListener listener, OnTaskLongClickListener longClickListener) {
        this.tasks = tasks;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface  OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitleTextView.setText(task.getTitle());
        holder.itemView.setOnClickListener(v -> {
            listener.onTaskClick(task);
        });
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onTaskLongClick(task);
            return true;
        });
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
