package com.app.todo.registration.interactor;

import android.content.Context;

import com.app.todo.registration.presenter.RegistrationPresenterInterface;

/**
 * Created by bridgeit on 24/4/17.
 */

public class RegistrationInteractor implements RegistrationInterActorInterface{
    Context context;
    RegistrationPresenterInterface registrationPresenterInterface;

    public RegistrationInteractor(Context context, RegistrationPresenterInterface registrationPresenterInterface) {
        this.context = context;
        this.registrationPresenterInterface = registrationPresenterInterface;
    }

    @Override
    public void loginResponse(String Name, String email, String password, String mobileNo) {

    }
}
