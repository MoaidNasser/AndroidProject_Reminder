package com.example.a1212508_1211441_courseproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTaskTitle, editTaskDescription;
    private Button editButtonDueDate, editButtonDueTime;
    private Spinner editTaskPriority, editTaskStatus;
    private DataBaseHelper dbHelper;
    private TaskModel task;

    private String[] priorityArray = {"Low", "Medium", "High"};
    private String[] statusArray = {"Pending", "In Progress", "Completed"};
    private String dueDateString, dueTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Initialize views
        editTaskTitle = findViewById(R.id.editTaskTitle);
        editTaskDescription = findViewById(R.id.editTaskDescription);
        editButtonDueDate = findViewById(R.id.editButtonDueDate);
        editButtonDueTime = findViewById(R.id.editButtonDueTime);
        editTaskPriority = findViewById(R.id.editTaskPriority);
        editTaskStatus = findViewById(R.id.editTaskStatus);

        dbHelper = new DataBaseHelper(this);

        // Set spinners
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorityArray);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTaskPriority.setAdapter(priorityAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTaskStatus.setAdapter(statusAdapter);

        // Retrieve the task from the Intent
        Intent intent = getIntent();
        task = new TaskModel();
        task.setId(intent.getIntExtra("taskId", 0));
        task.setTitle(intent.getStringExtra("taskTitle"));
        task.setDescription(intent.getStringExtra("taskDescription"));
        task.setDueDate(intent.getStringExtra("taskDueDate"));
        task.setPriority(intent.getStringExtra("taskPriority"));
        task.setStatus(intent.getStringExtra("taskStatus"));

        // Pre-fill fields
        if (task != null) {
            editTaskTitle.setText(task.getTitle());
            editTaskDescription.setText(task.getDescription());

            String[] dateTime = task.getDueDate().split(" ");
            dueDateString = dateTime[0];
            dueTimeString = dateTime[1];

            editButtonDueDate.setText(dueDateString);
            editButtonDueTime.setText(dueTimeString);

            editTaskPriority.setSelection(getPriorityPosition(task.getPriority()));
            editTaskStatus.setSelection(getStatusPosition(task.getStatus()));
        }

        // Set up Date and Time pickers
        setupDatePicker();
        setupTimePicker();

        // Save button functionality
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTaskChanges());
    }

    private void setupDatePicker() {
        editButtonDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                dueDateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                editButtonDueDate.setText(dueDateString);
            }, year, month, day).show();
        });
    }

    private void setupTimePicker() {
        editButtonDueTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                dueTimeString = String.format("%02d:%02d", hourOfDay, minute1);
                editButtonDueTime.setText(dueTimeString);
            }, hour, minute, true).show();
        });
    }

    private void saveTaskChanges() {
        String title = editTaskTitle.getText().toString();
        String description = editTaskDescription.getText().toString();
        String priority = editTaskPriority.getSelectedItem().toString();
        String status = editTaskStatus.getSelectedItem().toString();

        if (dueDateString == null || dueTimeString == null || title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDateString + " " + dueTimeString);
        task.setPriority(priority);
        task.setStatus(status);

        boolean isUpdated = dbHelper.updateTask(task);
        if (isUpdated) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }

    private int getPriorityPosition(String priority) {
        for (int i = 0; i < priorityArray.length; i++) {
            if (priorityArray[i].equals(priority)) {
                return i;
            }
        }
        return 0;
    }

    private int getStatusPosition(String status) {
        for (int i = 0; i < statusArray.length; i++) {
            if (statusArray[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }
}