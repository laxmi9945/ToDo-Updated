package com.app.todo.todoMain.ui.fragment;

import android.view.View;

import com.app.todo.model.NotesModel;

import java.util.List;

/**
 * Created by bridgeit on 13/5/17.
 */

public interface NotesFragmentInterface extends View.OnClickListener {
    void showDialog(String message);

    void hideDialog();

    void getNotesListSuccess(List<NotesModel> modelList);

    void getNotesListFailure(String message);
}
