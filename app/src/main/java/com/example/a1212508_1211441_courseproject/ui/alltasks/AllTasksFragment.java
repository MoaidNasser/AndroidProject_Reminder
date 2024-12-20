package com.example.a1212508_1211441_courseproject.ui.alltasks;

import android.app.AlertDialog;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.EditTaskActivity;
import com.example.a1212508_1211441_courseproject.R;
import com.example.a1212508_1211441_courseproject.ShowTaskActivity;
import com.example.a1212508_1211441_courseproject.TaskAdapter;
import com.example.a1212508_1211441_courseproject.TaskModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> allTasksList;
    private List<TaskModel> filteredTasksList;
    private DataBaseHelper dbHelper;
    private EditText searchEditText;
    private CheckBox sortByPriorityCheckBox;

    public AllTasksFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewAllTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchEditText = rootView.findViewById(R.id.searchEditText);
        sortByPriorityCheckBox = rootView.findViewById(R.id.sortByPriorityCheckBox);

        dbHelper = new DataBaseHelper(getContext());

        Intent intent = getActivity().getIntent();
        String loggedInUserEmail = intent.getStringExtra("email");

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        allTasksList = dbHelper.getAllTasksSortedByDate(loggedInUserEmail);
        filteredTasksList = new ArrayList<>(allTasksList);

        taskAdapter = new TaskAdapter(filteredTasksList, task -> showTaskOptions(task));
        recyclerView.setAdapter(taskAdapter);

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

        sortByPriorityCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sortTasksByPriority();
            } else {
                resetTaskOrder();
            }
        });

        return rootView;
    }

    private void filterTasks(String keyword) {
        filteredTasksList.clear();

        if (keyword.isEmpty()) {
            filteredTasksList.addAll(allTasksList);
        } else {
            for (TaskModel task : allTasksList) {
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
                return t1.getPriority().compareTo(t2.getPriority());
            }
        });
        taskAdapter.notifyDataSetChanged();
    }

    private void resetTaskOrder() {
        filteredTasksList.clear();
        filteredTasksList.addAll(allTasksList);
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
