package com.example.a1212508_1211441_courseproject;

import android.app.Activity;
import android.os.AsyncTask;
import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private DataBaseHelper dbHelper;
    private String userEmail;

    // Constructor to accept the activity context and database helper
    public ConnectionAsyncTask(Activity activity, DataBaseHelper dbHelper, String userEmail) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.userEmail = userEmail; // Email of the logged-in user
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Update UI to indicate the connection process has started
    }

    @Override
    protected String doInBackground(String... params) {
        // Fetch data from the URL (passed as the first parameter)
        return HttpManager.getData(params[0]);
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        // Parse JSON data into a list of tasks
        List<TaskModel> tasks = TasksJsonParser.getObjectFromJson(jsonData);

        if (tasks != null) {
            // Store tasks in the database
            for (TaskModel task : tasks) {
                boolean isAdded = dbHelper.addTask(
                        userEmail,
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueDate(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getReminder()
                );

                if (!isAdded) {
                    // Log or handle the error if a task fails to save
                    System.err.println("Failed to save task: " + task.getTitle());
                }
            }
        } else {
            // Log or handle the error if JSON parsing failed
            System.err.println("Failed to parse tasks from JSON.");
        }

        // Update UI to indicate the connection process has completed
    }
}
