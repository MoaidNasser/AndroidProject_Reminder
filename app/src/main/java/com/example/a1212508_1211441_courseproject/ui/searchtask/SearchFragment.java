package com.example.a1212508_1211441_courseproject.ui.searchtask;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.ShowTaskActivity;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;

import java.util.Calendar;
import java.util.List;


public class SearchFragment extends Fragment {


    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> allTasksList;

    private Button buttonSearch;
    private Button buttonStartDate;
    private Button buttonEndDate;


    private DataBaseHelper dbHelper; // Database Helper
    private String startDateString, endDateString;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        buttonStartDate = rootView.findViewById(R.id.buttonStartDate);
        buttonEndDate = rootView.findViewById(R.id.buttonEndDate);
        buttonSearch = rootView.findViewById(R.id.buttonSearch);

        recyclerView = rootView.findViewById(R.id.recyclerViewSearchedTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DataBaseHelper(getContext()); // Initialize database helper

        // Retrieve user email from Intent
        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return rootView; // Return early if no user is logged in
        }

        // Set up listeners for start and end date pickers
        setupStartDatePicker();
        setupEndDatePicker();

        buttonSearch.setOnClickListener(v -> {
            // Ensure dates are selected
            if (startDateString == null || endDateString == null) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch tasks for the logged-in user, sorted by due date (SQL query handles sorting)
            allTasksList = dbHelper.getAllTasksBetween(loggedInUserEmail, startDateString, endDateString);

            // Initialize the adapter and set it to the RecyclerView
            taskAdapter = new TaskAdapter(allTasksList, new TaskAdapter.OnTaskClickListener() {
                @Override
                public void onTaskClick(TaskModel task) {
                    // Handle task click: show delete and edit options
                    showTaskOptions(task);
                }
            });
            recyclerView.setAdapter(taskAdapter);


        });

        return rootView;
    }

    private void setupStartDatePicker() {
        buttonStartDate.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        // Format the selected date
                        startDateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        buttonStartDate.setText(startDateString);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupEndDatePicker() {
        buttonEndDate.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        // Format the selected date
                        endDateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        buttonEndDate.setText(endDateString);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void showTaskOptions(TaskModel task) {
        Intent showTaskIntent = new Intent(getContext(), ShowTaskActivity.class);

        showTaskIntent.putExtra("taskId", task.getId());
        showTaskIntent.putExtra("taskTitle", task.getTitle());
        showTaskIntent.putExtra("taskDescription", task.getDescription());
        showTaskIntent.putExtra("taskDueDate", task.getDueDate());
        showTaskIntent.putExtra("taskPriority", task.getPriority());
        showTaskIntent.putExtra("taskStatus", task.getStatus());

        startActivity(showTaskIntent);

    }

}