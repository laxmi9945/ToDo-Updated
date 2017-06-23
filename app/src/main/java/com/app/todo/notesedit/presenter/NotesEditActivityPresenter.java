package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.todoMain.interactor.NotesEditActivityInteractor;
import com.app.todo.todoMain.interactor.NotesEditActivityInteractorInterface;
import com.app.todo.todoMain.ui.activity.NotesEditActivityInterface;

/**
 * Created by bridgeit on 7/6/17.
 */

public class NotesEditActivityPresenter implements NotesEditActivityPresenterInterface {
    Context context;
    NotesEditActivityInterface viewInterface;
    NotesEditActivityInteractorInterface interactor;
    public NotesEditActivityPresenter(Context context, NotesEditActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new NotesEditActivityInteractor(context, this);
    }
    @Override
    public void showDialog(String message) {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void noteEditSuccess(String message) {

    }

    @Override
    public void noteEditFailure(String message) {

    }
}
