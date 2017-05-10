package com.app.todo.todoMain.presenter;

import android.content.Context;
import android.os.Bundle;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ineractor.NotesAddInteractor;
import com.app.todo.todoMain.ineractor.NotesAddInteractorInterface;
import com.app.todo.todoMain.ui.activity.NotesAddActivityInterface;
import com.app.todo.utils.Constants;



public class NotesAddPresenter implements NotesAddPresenterInterface {
    Context context;
    NotesAddActivityInterface viewInterface;

    NotesAddInteractorInterface interactor;

    public NotesAddPresenter(Context context, NotesAddActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new NotesAddInteractor(context, this);
    }

    @Override
    public void addNoteToFirebase(Bundle bundle) {

        NotesModel model = new NotesModel();

        model.setArchieved(false);
        model.setTime(bundle.getString(Constants.currentTimeKey));
        model.setDate(bundle.getString(Constants.currentDateKey));
        model.setTitle(bundle.getString(Constants.titleKey));
        model.setContent(bundle.getString(Constants.descriptionKey));
        model.setReminderDate(bundle.getString(Constants.reminderKey));

        interactor.addNoteToFirebase(model);
    }

    @Override
    public void showDialog(String message) {
        viewInterface.showDialog(message);
    }

    @Override
    public void hideDialog() {
        viewInterface.hideDialog();
    }

    @Override
    public void noteAddSuccess(String message) {
        viewInterface.noteAddSuccess(message);
    }

    @Override
    public void noteAddFailure(String message) {
        viewInterface.noteAddFailure(message);
    }
}