package com.example.a1212508_1211441_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.TaskModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> tasks;
    private OnTaskClickListener listener;

    public TaskAdapter(List<TaskModel> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        TaskModel task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());

        // Format the due date
        String formattedDate = formatDate(task.getDueDate());
        holder.dueDateTextView.setText(formattedDate);

        holder.aSwitch.setClickable(false);

        if (task.getStatus().equals("Completed"))
            holder.aSwitch.setChecked(true);
        else
            holder.aSwitch.setChecked(false);
    }

    private String formatDate(String dueDateTime) {
        try {
            // Assuming the due date time is in "YYYY-MM-DD HH:mm:ss" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dueDateTime);

            // Format the date to a more readable format
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dueDateTime;  // If parsing fails, return the original string
        }
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, dueDateTextView;
        Switch aSwitch;

        public TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTaskTitle);
            dueDateTextView = itemView.findViewById(R.id.textViewDueDate);
            aSwitch = itemView.findViewById(R.id.switchReminder);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTaskClick(tasks.get(getAdapterPosition()));
                }
            });
        }
    }
}
