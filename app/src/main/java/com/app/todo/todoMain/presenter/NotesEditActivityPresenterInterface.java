package com.app.todo.todoMain.presenter;

/**
 * Created by bridgeit on 7/6/17.
 */

public interface NotesEditActivityPresenterInterface {
    void showDialog(String message);
    void hideDialog();

    void noteEditSuccess(String message);
    void noteEditFailure(String message);
}
