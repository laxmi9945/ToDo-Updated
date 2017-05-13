package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.interactor.TodoMainActivityInteractor;
import com.app.todo.todoMain.ui.activity.TodoMainActivityInterface;

import java.util.List;

public class TodoMainActivityPresenter implements TodoMainActivityPresenterInterface {
    Context context;
    TodoMainActivityInterface viewInterface;
    TodoMainActivityInteractor interactor;
    public TodoMainActivityPresenter(Context context, TodoMainActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor=new TodoMainActivityInteractor(context,this);
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
    public void getNoteList(String uId) {
        interactor.getNoteList(uId);
    }

    @Override
    public void getNoteListSuccess(List<NotesModel> modelList) {
        viewInterface.getNotesListSuccess(modelList);
    }

    @Override
    public void getNoteListFailure(String message) {
        viewInterface.getNotesListFailure(message);
    }


}
