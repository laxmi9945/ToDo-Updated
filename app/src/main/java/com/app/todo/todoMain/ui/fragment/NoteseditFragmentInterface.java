package com.app.todo.todoMain.ui.fragment;

import android.view.View;


public interface NoteseditFragmentInterface extends View.OnClickListener {
    void showDialog(String message);
    void hideDialog();
}
