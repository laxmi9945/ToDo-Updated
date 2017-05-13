package com.app.todo.todoMain.presenter;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.interactor.ReminderFragmentInteractor;
import com.app.todo.todoMain.interactor.ReminderFragmentInteractorInterface;
import com.app.todo.todoMain.ui.fragment.ReminderFragmentInterface;

import java.util.List;


public class ReminderFragmentPresenter implements ReminderFragmentPresenterInterface {
    ReminderFragmentInterface viewInterface;
    ReminderFragmentInteractorInterface interactor;
    Context context;

    public ReminderFragmentPresenter(Context context,ReminderFragmentInterface viewInterface){
        this.context=context;
        this.viewInterface=viewInterface;
        interactor=new ReminderFragmentInteractor(context,this);


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
    public void getReminderNotes(String uId) {
        interactor.getReminderNotes(uId);
    }

    @Override
    public void gettingReminderSuccess(List<NotesModel> notesModelList) {
        viewInterface.gettingReminderSuccess(notesModelList);
    }

    @Override
    public void gettingReminderFailure(String message) {
        viewInterface.gettingReminderFailure(message);
    }

}
