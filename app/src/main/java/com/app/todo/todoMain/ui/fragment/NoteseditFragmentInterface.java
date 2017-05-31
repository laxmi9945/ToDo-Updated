package com.app.todo.todoMain.ui.fragment;

import android.view.View;

import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;


public interface NoteseditFragmentInterface extends View.OnClickListener,ColorPickerDialogListener {
    void showDialog(String message);
    void hideDialog();
}
