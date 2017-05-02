package com.app.todo.registration.presenter;

import com.app.todo.model.UserInfoModel;


public interface RegistrationPresenterInterface {
    void registrationSuccess(UserInfoModel userInfoModel, String uid);
    void registrationFailure(String message);
    void showProgressDialog(String s);
    void hideProgressDialog();
    void registrationResponse(UserInfoModel userInfoModel);
}
