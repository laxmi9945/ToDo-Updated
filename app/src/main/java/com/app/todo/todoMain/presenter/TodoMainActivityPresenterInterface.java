package com.app.todo.todoMain.presenter;


import com.app.todo.model.NotesModel;

import java.util.List;

public interface TodoMainActivityPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getNoteList(String uId);
    void getNoteListSuccess(List<NotesModel> modelList);
    void getNoteListFailure(String message);
}
