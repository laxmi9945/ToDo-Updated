package com.app.todo.todoMain.presenter;

import com.app.todo.model.NotesModel;

import java.util.List;


public interface TrashFragmentPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getDeleteNote(String uId);
    void noteDeleteSuccess(List<NotesModel> notesModelList);
    void noteDeleteFailure(String message);
}
