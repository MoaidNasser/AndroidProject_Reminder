package com.example.a1212508_1211441_courseproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TasksJsonParser {

    // Parses JSON data into a list of Task objects
    public static List<TaskModel> getObjectFromJson(String json) {
        List<TaskModel> tasks = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Create a Task object and set its fields
                TaskModel task = new TaskModel(
                        jsonObject.getString("title"),
                        jsonObject.getString("description"),
                        jsonObject.getString("due_date_time"),
                        jsonObject.getString("priority"),
                        jsonObject.getString("status"),
                        jsonObject.getInt("reminder")
                );

                // Add the task to the list
                tasks.add(task);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tasks;
    }
}
