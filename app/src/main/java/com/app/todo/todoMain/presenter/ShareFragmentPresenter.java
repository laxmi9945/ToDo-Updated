package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.interactor.ShareFragmentInteractor;
import com.app.todo.todoMain.ui.fragment.ShareFragmentInterface;

import java.util.List;

/**
 * Created by bridgeit on 9/6/17.
 */

public class ShareFragmentPresenter implements ShareFragmentPresenterInterface {
    Context context;
    ShareFragmentInterface viewInterface;
    ShareFragmentInteractor interactor;

    public ShareFragmentPresenter(Context context, ShareFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor=new ShareFragmentInteractor(context,this);
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
