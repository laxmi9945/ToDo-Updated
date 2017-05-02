package com.app.todo.registration.presenter;

import android.content.Context;

import com.app.todo.registration.interactor.RegistrationInterActor;
import com.app.todo.registration.interactor.RegistrationInterActorInterface;
import com.app.todo.registration.ui.RegistrationActivityInterface;
import com.app.todo.model.UserInfoModel;


public class RegistrationPresenter implements RegistrationPresenterInterface {
    Context context;
    RegistrationActivityInterface viewInterface;
    RegistrationInterActorInterface registrationInterActorInterface;
    public RegistrationPresenter(Context context, RegistrationActivityInterface viewInterface) {
        this.context=context;
        this.viewInterface=viewInterface;
        registrationInterActorInterface=new RegistrationInterActor(context,this);
    }

    @Override
    public void registrationSuccess(UserInfoModel userInfoModel, String uid) {
        viewInterface.registrationSuccess(userInfoModel,uid);
    }

    @Override
    public void registrationFailure(String message) {

    }

    @Override
    public void showProgressDialog(String message) {
            viewInterface.showProgressDialog(message);
    }

    @Override
    public void hideProgressDialog() {
            viewInterface.hideProgressDialog();
    }


    public void registrationResponse(UserInfoModel userInfoModel) {
        registrationInterActorInterface.registrationResponse(userInfoModel);
    }
}
