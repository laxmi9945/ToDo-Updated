package com.app.todo.model;

/**
 * Created by bridgeit on 6/4/17.
 */

public class NotesModel {
    String title;
    String content;
    String date;
    String id;



    public NotesModel() {

    }

    public NotesModel(String date, String content, String id, String title) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.id = id;
    }

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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
