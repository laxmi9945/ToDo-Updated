package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.interactor.NotesFragmentInteractor;
import com.app.todo.todoMain.ui.fragment.NotesFragmentInterface;

import java.util.List;



public class NotesFragmentPresenter implements NotesFragmentPresenterInterface {
    Context context;
    NotesFragmentInterface viewInterface;
    NotesFragmentInteractor interactor;

    public NotesFragmentPresenter(Context context, NotesFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor=new NotesFragmentInteractor(context,this);
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
