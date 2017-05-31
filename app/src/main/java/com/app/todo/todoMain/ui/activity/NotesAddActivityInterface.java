package com.app.todo.todoMain.ui.activity;

public interface NotesAddActivityInterface  {

    void showDialog(String message);
    void hideDialog();

    void noteAddSuccess(String message);
    void noteAddFailure(String message);

}