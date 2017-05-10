package com.app.todo.resetPassword.presenter;


import android.content.Context;

import com.app.todo.resetPassword.interactor.ResetPasswordInterActor;
import com.app.todo.resetPassword.interactor.ResetPasswordInterActorInterface;
import com.app.todo.resetPassword.ui.ResetPasswordActivityInterface;

public class ResetPasswordPresenter implements ResetPasswordPresenterInterface{
    Context context;
    ResetPasswordActivityInterface viewInterface;
    ResetPasswordInterActorInterface interactor;

    public ResetPasswordPresenter(Context context, ResetPasswordActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor=new ResetPasswordInterActor(context,this);
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
    public void resetPasswordSuccess(String message) {
        viewInterface.resetPasswordSuccess(message);
    }

    @Override
    public void resetPasswordFailure(String message) {
        viewInterface.resetPasswordFailure(message);
    }
}
