package com.app.todo.model;

/**
 * Created by bridgeit on 6/4/17.
 */

public class NotesModel {
    String title;
    String content;
    String date;
    String time;
    int id;

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

}
