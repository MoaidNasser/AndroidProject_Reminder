package com.example.a1212508_1211441_courseproject;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShowTaskActivity extends AppCompatActivity {

    private TaskModel task;
    private DataBaseHelper dbHelper;

    private TextView taskTitle, taskDescription, taskDueDate, taskPriority, taskStatus;
    private Button editButton, deleteButton, shareButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);

        dbHelper = new DataBaseHelper(this);

        // Initialize views
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        taskDueDate = findViewById(R.id.taskDate);
        taskPriority = findViewById(R.id.taskPriority);
        taskStatus = findViewById(R.id.taskStatus);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        shareButton = findViewById(R.id.shareButton);
        backButton = findViewById(R.id.backButton);

        // Retrieve the task from the Intent
        Intent intent = getIntent();
        int task_id = intent.getIntExtra("taskId", 0);
        String task_title = intent.getStringExtra("taskTitle");
        String task_desc = intent.getStringExtra("taskDescription");
        String task_date = intent.getStringExtra("taskDueDate");
        String task_p = intent.getStringExtra("taskPriority");
        String task_s = intent.getStringExtra("taskStatus");

        task = new TaskModel();
        task.setId(task_id);
        task.setTitle(task_title);
        task.setDescription(task_desc);
        task.setDueDate(task_date);
        task.setPriority(task_p);
        task.setStatus(task_s);

        taskTitle.setText(task_title);
        taskDescription.setText(task_desc);
        taskDueDate.setText(task_date);
        taskStatus.setText(task_s);
        taskPriority.setText(task_p);

        editButton.setOnClickListener(v -> editTask(task));

        deleteButton.setOnClickListener(v -> deleteTask(task));

        backButton.setOnClickListener(v -> backToHome());

        String taskDetails = "Description:\n" + task_desc + "\nDue Date: " + task_date
                + "\nPriority: " + task_p + "\nStatus: " + task_s;
        shareButton.setOnClickListener(v -> shareViaEmail(taskDetails));
    }

    private void editTask(TaskModel task) {
        Intent intent = new Intent(ShowTaskActivity.this, EditTaskActivity.class);

        intent.putExtra("taskId", task.getId());
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskDueDate", task.getDueDate());
        intent.putExtra("taskPriority", task.getPriority());
        intent.putExtra("taskStatus", task.getStatus());

        startActivity(intent);
    }

    private void deleteTask(TaskModel task) {
        boolean isDeleted = dbHelper.deleteTask(task.getId());

        if (isDeleted) {
            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToHome() {
        finish();
    }

    private void shareViaEmail(String taskDetails) {
        Intent gmailIntent =new Intent();
        gmailIntent.setAction(Intent.ACTION_SENDTO);
        gmailIntent.setType("message/rfc822");
        gmailIntent.setData(Uri.parse("mailto:"));
        gmailIntent.putExtra(Intent.EXTRA_SUBJECT,taskTitle.getText().toString());
        gmailIntent.putExtra(Intent.EXTRA_TEXT,taskDetails);
        startActivity(gmailIntent);
    }
}