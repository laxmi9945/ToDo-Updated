package com.app.todo.todoMain.ui.activity;

import android.view.View;


public interface NotesAddActivityInterface extends View.OnClickListener {

    void showDialog(String message);
    void hideDialog();

    void noteAddSuccess(String message);
    void noteAddFailure(String message);

}