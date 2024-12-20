package com.example.a1212508_1211441_courseproject;

public class TaskModel {

    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String status;
    private int reminder;

    // Priority and Status options as arrays
    public static final String[] PRIORITIES = {"Low", "Medium", "High"};
    public static final String[] STATUSES = {"Pending", "In Progress", "Completed"};

    // Constructor to accept all required fields
    public TaskModel(int id, String title, String description, String dueDate, String priority, String status, int reminder) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.reminder = reminder;
    }

    // Constructor to set all default values
    public TaskModel() {
        this.id = 0;
        this.title = "title";
        this.description = "description";
        this.dueDate = "dueDate";
        this.priority = "priority";
        this.status = "Pending";
        this.reminder = 0;
    }

    // Getters for all fields
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public int getReminder() {
        return reminder;
    }

    // Setters for all fields
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        if (isValidPriority(priority)) {
            this.priority = priority;
        } else {
            this.priority = "Low"; // Default to Low if invalid priority
        }
    }

    public void setStatus(String status) {
        if (isValidStatus(status)) {
            this.status = status;
        } else {
            this.status = "Pending"; // Default to Pending if invalid status
        }
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    // Validation methods for priority and status
    private boolean isValidPriority(String priority) {
        for (String validPriority : PRIORITIES) {
            if (validPriority.equals(priority)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidStatus(String status) {
        for (String validStatus : STATUSES) {
            if (validStatus.equals(status)) {
                return true;
            }
        }
        return false;
    }
}
