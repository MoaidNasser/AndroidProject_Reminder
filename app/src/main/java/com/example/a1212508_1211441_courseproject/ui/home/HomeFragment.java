package com.example.a1212508_1211441_courseproject.ui.home;

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
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;
import com.example.a1212508_1211441_courseproject.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> todayTasksList;
    private List<TaskModel> filteredTasksList; // List to hold filtered tasks
    private DataBaseHelper dbHelper;
    private EditText searchEditText; // Search bar input

    public HomeFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerViewToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchEditText = root.findViewById(R.id.searchEditText); // Initialize search bar

        dbHelper = new DataBaseHelper(getContext());

        // Retrieve user email from Intent
        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        // Get today's date in "yyyy-MM-dd" format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = simpleDateFormat.format(Calendar.getInstance().getTime());

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return root; // Return early if no user is logged in
        }

        boolean allCompleted = false;

        // Fetch today tasks for the logged-in user, sorted by due date (SQL query handles sorting)
        todayTasksList = dbHelper.getAllTodayTasks(loggedInUserEmail, todayDate);
        filteredTasksList = new ArrayList<>(todayTasksList); // Initially show all tasks

        for (int i = 0; i < todayTasksList.size(); i++) {
            if (todayTasksList.get(i).getStatus().equals("Completed"))
                allCompleted = true;
            else
                allCompleted = false;
        }

        if (allCompleted)
            Toast.makeText(getContext(), "Congratulations <3\nYou completed all tasks for today!", Toast.LENGTH_LONG).show();

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

        return root;
    }

    private void filterTasks(String keyword) {
        filteredTasksList.clear();

        if (keyword.isEmpty()) {
            filteredTasksList.addAll(todayTasksList); // Show all tasks if no keyword
        } else {
            for (TaskModel task : todayTasksList) {
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
            todayTasksList.remove(task);
            taskAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }
}