package com.app.todo.todoMain.presenter;

import com.app.todo.model.NotesModel;

import java.util.List;

public interface ReminderFragmentPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getReminderNotes(String uId);
    void gettingReminderSuccess(List<NotesModel> notesModelList);
    void gettingReminderFailure(String message);
}
