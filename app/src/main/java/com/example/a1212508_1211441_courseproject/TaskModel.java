package com.example.a1212508_1211441_courseproject;

public class TaskModel {

    private int id;
    private String email;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String status;
    private int reminder;

    // Priority and Status options as arrays
    public static final String[] PRIORITIES = {"Low", "Medium", "High"};
    public static final String[] STATUSES = {"Pending", "In Progress", "Completed"};

    // Constructor to set all default values
    public TaskModel() {

        this.title = "title";
        this.description = "description";
        this.dueDate = "dueDate";
        this.priority = "priority";
        this.status = "Medium";
        this.reminder = 0;
    }

    public TaskModel(int id, String email, String title, String description, String dueDate, String priority, String status, int reminder) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.reminder = reminder;
    }

    public TaskModel( String title, String description, String dueDate, String priority, String status, int reminder) {

        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.reminder = reminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }
}
