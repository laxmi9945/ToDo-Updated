package com.app.todo.registration.presenter;

import com.app.todo.model.UserInfoModel;

/**
 * Created by bridgeit on 24/4/17.
 */

public interface RegistrationPresenterInterface {
    void loginSuccess(UserInfoModel userInfoModel, String uid);
    void loginFailure(String message);
    void showProgressDialog();
    void hideProgressDialog();
    void loginResponse(String email,String password);
}
