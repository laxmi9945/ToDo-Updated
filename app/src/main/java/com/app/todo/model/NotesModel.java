package com.app.todo.model;

public class NotesModel {
    private String title;
    private String content;
    private String date;
    private String time;
    private String reminderDate;
    private int id;
    private boolean isArchieved;
    /*private boolean isDeleted;


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }*/



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public NotesModel() {

    }

    /*

    public NotesModel(String time,String date, String content, int id, String title) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.id = id;
    }
*/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public boolean isArchieved() {
        return isArchieved;
    }

    public void setArchieved(boolean archieved) {
        isArchieved = archieved;
    }

}
