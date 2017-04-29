package com.app.todo.login.presenter;

import com.app.todo.model.UserInfoModel;

/**
 * Created by bridgeit on 24/4/17.
 */

public interface RegistrationPresenterInterface {
    void registrationSuccess(UserInfoModel userInfoModel, String uid);
    void registrationFailure(String message);
    void showProgressDialog(String s);
    void hideProgressDialog();
    void registrationResponse(UserInfoModel userInfoModel);
}
