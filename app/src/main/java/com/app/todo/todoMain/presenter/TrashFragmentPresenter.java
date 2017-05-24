package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.interactor.TrashFragmentInteractor;
import com.app.todo.todoMain.interactor.TrashFragmentInteractorInterface;
import com.app.todo.todoMain.ui.fragment.TrashFragmentInterface;

import java.util.List;

public class TrashFragmentPresenter implements TrashFragmentPresenterInterface {
    Context context;
    TrashFragmentInterface viewInterface;
    TrashFragmentInteractorInterface interactor;

    public TrashFragmentPresenter(Context context, TrashFragmentInterface viewInterface){
        this.context=context;
        this.viewInterface=viewInterface;
        interactor=new TrashFragmentInteractor(context,this);
    }

    @Override
    public void showDialog(String message) {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void getDeleteNote(String uId) {
        interactor.getDeleteNote(uId);
    }

    @Override
    public void noteDeleteSuccess(List<NotesModel> notesModelList) {
        viewInterface.deleteSuccess(notesModelList);
    }

    @Override
    public void noteDeleteFailure(String message) {
        viewInterface.deleteFailure(message);
    }
}
