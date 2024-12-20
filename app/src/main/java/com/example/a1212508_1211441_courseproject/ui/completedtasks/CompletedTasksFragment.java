package com.example.a1212508_1211441_courseproject.ui.completedtasks;

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

import java.util.ArrayList;
import java.util.List;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.ShowTaskActivity;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;

public class CompletedTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> completedTasksList;
    private List<TaskModel> filteredTasksList; // List to hold filtered tasks
    private DataBaseHelper dbHelper;
    private EditText searchEditText; // Search bar input

    public CompletedTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewCompletedTasks);
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

        // Fetch tasks for the logged-in user, sorted by due date (SQL query handles sorting)
        completedTasksList = dbHelper.getAllCompletedTasks(loggedInUserEmail);
        filteredTasksList = new ArrayList<>(completedTasksList); // Initially show all tasks

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
            filteredTasksList.addAll(completedTasksList); // Show all tasks if no keyword
        } else {
            for (TaskModel task : completedTasksList) {
                if (task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTasksList.add(task);
                }
            }
        }

        taskAdapter.notifyDataSetChanged(); // Update the RecyclerView
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