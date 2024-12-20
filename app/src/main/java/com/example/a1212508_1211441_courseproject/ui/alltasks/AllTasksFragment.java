package com.example.a1212508_1211441_courseproject.ui.alltasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;

import java.util.ArrayList;
import java.util.List;


public class AllTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> allTasksList;
    private List<TaskModel> filteredTasksList; // List to hold filtered tasks
    private DataBaseHelper dbHelper;
    private EditText searchEditText; // Search bar input

    public AllTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewAllTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchEditText = rootView.findViewById(R.id.searchEditText); // Initialize search bar

        dbHelper = new DataBaseHelper(getContext());

        // Retrieve user email from Intent
        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return rootView; // Return early if no user is logged in
        }

        // Fetch tasks for the logged-in user
        allTasksList = dbHelper.getAllTasksSortedByDate(loggedInUserEmail);
        filteredTasksList = new ArrayList<>(allTasksList); // Initially show all tasks

        // Initialize the adapter with the filtered list
        taskAdapter = new TaskAdapter(filteredTasksList, task -> showTaskOptions(task));
        recyclerView.setAdapter(taskAdapter);

        // Set up search bar listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void filterTasks(String keyword) {
        filteredTasksList.clear();

        if (keyword.isEmpty()) {
            filteredTasksList.addAll(allTasksList); // Show all tasks if no keyword
        } else {
            for (TaskModel task : allTasksList) {
                if (task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTasksList.add(task);
                }
            }
        }

        taskAdapter.notifyDataSetChanged(); // Update the RecyclerView
    }


    private void showTaskOptions(TaskModel task) {
        // Create an AlertDialog to display task details and options to Delete or Edit
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Set task details as message in the dialog
        builder.setTitle("Task Details")
                .setMessage("Title: " + task.getTitle() + "\n" +
                        "Description: " + task.getDescription() + "\n" +
                        "Due Date: " + task.getDueDate() + "\n" +
                        "Priority: " + task.getPriority() + "\n" +
                        "Status: " + task.getStatus()
                )
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the edit function (start EditTaskActivity with the task data)
                        editTask(task);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the delete function
                        deleteTask(task);
                    }
                })
                .setNeutralButton("Cancel", null)  // Close the dialog
                .show();
    }

    private void editTask(TaskModel task) {
        // Create an Intent to start EditTaskActivity
        Intent intent = new Intent(getContext(), EditTaskActivity.class);

        // Pass task data to the EditTaskActivity
        intent.putExtra("taskId", task.getId());
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskDueDate", task.getDueDate());
        intent.putExtra("taskPriority", task.getPriority());
        intent.putExtra("taskStatus", task.getStatus());

        // Start EditTaskActivity
        startActivity(intent);
    }

    private void deleteTask(TaskModel task) {
        // Delete the task from the database
        boolean isDeleted = dbHelper.deleteTask(task.getId());

        if (isDeleted) {
            Toast.makeText(getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
            // Update the RecyclerView by removing the task
            allTasksList.remove(task);
            taskAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }
}