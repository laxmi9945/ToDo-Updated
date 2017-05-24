package com.app.todo.todoMain.presenter;

import com.app.todo.model.NotesModel;

import java.util.List;


public interface ArchiveFragmentPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getArchiveNote(String uId);
    void noteArchiveSuccess(List<NotesModel> notesModelList);
    void noteArchiveFailure(String message);
}
