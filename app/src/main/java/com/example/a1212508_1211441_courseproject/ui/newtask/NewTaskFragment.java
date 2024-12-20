package com.example.a1212508_1211441_courseproject.ui.newtask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.R;

import java.util.Calendar;

public class NewTaskFragment extends Fragment {

    private EditText editTextTaskTitle, editTextTaskDescription;
    private Button buttonDueDate, buttonDueTime;
    private Spinner spinnerPriority;
    private CheckBox checkBoxReminder, checkBoxCompletionStatus;
    private Button buttonSaveTask;

    private DataBaseHelper dbHelper; // Database Helper
    private String dueDateString, dueTimeString;

    public NewTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_task, container, false);

        // Initialize views
        editTextTaskTitle = rootView.findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = rootView.findViewById(R.id.editTextTaskDescription);
        buttonDueDate = rootView.findViewById(R.id.buttonDueDate);
        buttonDueTime = rootView.findViewById(R.id.buttonDueTime);
        spinnerPriority = rootView.findViewById(R.id.spinnerPriority);
        checkBoxReminder = rootView.findViewById(R.id.checkBoxReminder);
        checkBoxCompletionStatus = rootView.findViewById(R.id.checkBoxCompletionStatus);
        buttonSaveTask = rootView.findViewById(R.id.buttonSaveTask);

        dbHelper = new DataBaseHelper(getContext()); // Initialize database helper

        // Retrieve user email from Intent
        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        if (loggedInUserEmail == null || loggedInUserEmail.isEmpty()) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        // Setup spinner for priority selection
        setupPrioritySpinner();

        // Set up listeners for date and time pickers
        setupDatePicker();
        setupTimePicker();

        // Handle save button click
        buttonSaveTask.setOnClickListener(v -> {
            // Collect input values
            String taskTitle = editTextTaskTitle.getText().toString().trim();
            String taskDescription = editTextTaskDescription.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            boolean reminder = checkBoxReminder.isChecked();
            boolean isCompleted = checkBoxCompletionStatus.isChecked();

            // Ensure date and time are selected
            if (dueDateString == null || dueTimeString == null || taskTitle.isEmpty() || taskDescription.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = isCompleted ? "Completed" : "Pending";

            // Append reminder note if needed
            if (reminder) {
                taskDescription += " [Reminder Set]";
            }


            // Save task to the database with the logged-in user's email and completion status
            boolean isInserted = dbHelper.addTask(
                    loggedInUserEmail.toString(),
                    taskTitle.toString(),
                    taskDescription.toString(),
                    (dueDateString + " " + dueTimeString).toString(),
                    priority.toString(),
                    status.toString(),
                    reminder ? 1 : 0  // Set reminder to 1 if checked, otherwise 0
            );

            if (isInserted) {
                Toast.makeText(getContext(), "Task saved successfully!", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(getContext(), "Failed to save task", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void setupPrioritySpinner() {
        // Define priority levels directly in the code
        String[] priorityLevels = {"High", "Medium", "Low"};

        // Set up the Spinner with an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                priorityLevels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void setupDatePicker() {
        buttonDueDate.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        // Format the selected date
                        dueDateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        buttonDueDate.setText(dueDateString);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupTimePicker() {
        buttonDueTime.setOnClickListener(v -> {
            // Get current time
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Open TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute1) -> {
                        // Format the selected time
                        dueTimeString = String.format("%02d:%02d", hourOfDay, minute1);
                        buttonDueTime.setText(dueTimeString);
                    }, hour, minute, true);
            timePickerDialog.show();
        });
    }

    private void clearInputs() {
        editTextTaskTitle.setText("");
        editTextTaskDescription.setText("");
        buttonDueDate.setText("Select Due Date");
        buttonDueTime.setText("Select Due Time");
        checkBoxReminder.setChecked(false);
        checkBoxCompletionStatus.setChecked(false);
    }
}
