package com.app.todo.todoMain.ui.activity;


import com.app.todo.model.NotesModel;

import java.util.List;

public interface TodoMainActivityInterface {
    void showDialog(String message);
    void hideDialog();
    void getNotesListFailure(String message);
    void getNotesListSuccess(List<NotesModel> modelList);
}
