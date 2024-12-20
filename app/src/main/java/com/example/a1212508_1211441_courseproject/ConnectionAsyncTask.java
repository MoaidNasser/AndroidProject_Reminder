package com.example.a1212508_1211441_courseproject;

import android.app.Activity;
import android.os.AsyncTask;
import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private DataBaseHelper dbHelper;
    private String userEmail;

    public ConnectionAsyncTask(Activity activity, DataBaseHelper dbHelper, String userEmail) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.userEmail = userEmail;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return HttpManager.getData(params[0]);
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        List<TaskModel> tasks = TasksJsonParser.getObjectFromJson(jsonData);

        if (tasks != null) {
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
                    System.err.println("Failed to save task: " + task.getTitle());
                }
            }
        } else {
            System.err.println("Failed to parse tasks from JSON.");
        }
    }
}
