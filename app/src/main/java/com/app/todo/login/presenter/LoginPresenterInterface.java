package com.app.todo.login.presenter;

import com.app.todo.model.UserInfoModel;

public interface LoginPresenterInterface {
    void loginSuccess(UserInfoModel userInfoModel, String uid);

    void loginFailure(String message);

    void showProgressDialog(String message);

    void hideProgressDialog();

    void loginResponse(String email, String password);


}
