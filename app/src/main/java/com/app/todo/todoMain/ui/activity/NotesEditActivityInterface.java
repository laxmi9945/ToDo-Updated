package com.app.todo.todoMain.ui.activity;

public interface NotesEditActivityInterface {
    void showDialog(String message);
    void hideDialog();
    void noteEditSuccess(String message);
    void noteEditFailure(String message);
}
