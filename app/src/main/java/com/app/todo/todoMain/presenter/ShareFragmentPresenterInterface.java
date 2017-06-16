package com.app.todo.todoMain.presenter;

import com.app.todo.model.NotesModel;

import java.util.List;

/**
 * Created by bridgeit on 9/6/17.
 */

public interface ShareFragmentPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getNoteList(String uId);
    void getNoteListSuccess(List<NotesModel> modelList);
    void getNoteListFailure(String message);
}
