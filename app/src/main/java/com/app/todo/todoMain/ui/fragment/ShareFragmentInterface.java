package com.app.todo.todoMain.ui.fragment;

import com.app.todo.model.NotesModel;

import java.util.List;

/**
 * Created by bridgeit on 9/6/17.
 */

public interface ShareFragmentInterface {
    void showDialog(String message);

    void hideDialog();

    void getNotesListSuccess(List<NotesModel> modelList);

    void getNotesListFailure(String message);
}
