package com.app.todo.todoMain.ui.activity;

import android.view.View;

import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;


public interface NotesAddActivityInterface extends View.OnClickListener,ColorPickerDialogListener {

    void showDialog(String message);
    void hideDialog();

    void noteAddSuccess(String message);
    void noteAddFailure(String message);

}