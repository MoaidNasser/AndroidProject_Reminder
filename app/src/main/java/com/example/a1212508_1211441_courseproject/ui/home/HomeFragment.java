package com.example.a1212508_1211441_courseproject.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.ShowTaskActivity;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;
import com.example.a1212508_1211441_courseproject.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> todayTasksList;
    private List<TaskModel> filteredTasksList;
    private DataBaseHelper dbHelper;
    private EditText searchEditText;
    private CheckBox sortByPriorityCheckBox;

    public HomeFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerViewToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchEditText = root.findViewById(R.id.searchEditText);
        sortByPriorityCheckBox = root.findViewById(R.id.sortByPriorityCheckBox);

        dbHelper = new DataBaseHelper(getContext());

        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = simpleDateFormat.format(Calendar.getInstance().getTime());

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return root;
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




        taskAdapter = new TaskAdapter(filteredTasksList, task -> showTaskOptions(task));
        recyclerView.setAdapter(taskAdapter);

        sortByPriorityCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sortTasksByPriority();
            } else {
                filteredTasksList.clear();
                filteredTasksList.addAll(todayTasksList);
                taskAdapter.notifyDataSetChanged();
            }
        });

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
            filteredTasksList.addAll(todayTasksList);
        } else {
            for (TaskModel task : todayTasksList) {
                if (task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTasksList.add(task);
                }
            }
        }

        taskAdapter.notifyDataSetChanged();
    }

    private void sortTasksByPriority() {
        Collections.sort(filteredTasksList, new Comparator<TaskModel>() {
            @Override
            public int compare(TaskModel t1, TaskModel t2) {
                return Integer.compare(getPriorityValue(t1.getPriority()), getPriorityValue(t2.getPriority()));
            }

            private int getPriorityValue(String priority) {
                switch (priority) {
                    case "High":
                        return 1;
                    case "Medium":
                        return 2;
                    case "Low":
                        return 3;
                    default:
                        return 2;
                }
            }
        });

        taskAdapter.notifyDataSetChanged();
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
