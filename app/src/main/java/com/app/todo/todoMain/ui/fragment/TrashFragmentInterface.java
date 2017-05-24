package com.app.todo.todoMain.ui.fragment;

import com.app.todo.model.NotesModel;

import java.util.List;

public interface TrashFragmentInterface {
    void showDialog(String message);
    void hideDialog();

    void deleteSuccess(List<NotesModel> modelList);
    void deleteFailure(String message);
}
