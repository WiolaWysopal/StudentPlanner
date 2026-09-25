package com.wiolawysopal.studentplanner;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.graphics.Paint;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<Task> tasks;
    private OnTaskClickListener listener;
    private OnTaskLongClickListener longClickListener;
    private OnTaskCompletionChangeListener completionChangeListener;
    public TaskAdapter(ArrayList<Task> tasks, OnTaskClickListener listener, OnTaskLongClickListener longClickListener, OnTaskCompletionChangeListener completionChangeListener) {
        this.tasks = tasks;
        this.listener = listener;
        this.longClickListener = longClickListener;
        this.completionChangeListener = completionChangeListener;
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

    public interface  OnTaskCompletionChangeListener {
        void onTaskCompletionChanged(Task task, boolean completed);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitleTextView.setText(task.getTitle());

        if (task.isCompleted()) {
            holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.taskCompletedCheckBox.setOnCheckedChangeListener(null);
        holder.taskCompletedCheckBox.setChecked(task.isCompleted());

        holder.taskCompletedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            completionChangeListener.onTaskCompletionChanged(task, isChecked);
            notifyItemChanged(holder.getBindingAdapterPosition());
        });

        String subject = task.getSubject();
        if (subject == null || subject.isEmpty()) {
            holder.taskSubjectTextView.setVisibility(View.GONE);
        } else {
            holder.taskSubjectTextView.setVisibility(View.VISIBLE);
            holder.taskSubjectTextView.setText(subject);
        }
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
        TextView taskSubjectTextView;
        CheckBox taskCompletedCheckBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTitleTextView = itemView.findViewById(R.id.taskTitleTextView);
            taskSubjectTextView = itemView.findViewById(R.id.taskSubjectTextView);
            taskCompletedCheckBox = itemView.findViewById(R.id.taskCompletedCheckBox);
        }
    }
}
