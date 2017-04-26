package com.app.todo.login.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.app.todo.login.ui.LoginActivityInterface;
import com.app.todo.model.UserInfoModel;
import com.app.todo.utils.Constants;


public class LoginPresenter implements LoginPresenterInterface {
    Context context;
    LoginActivityInterface loginActivityInterface;
    @Override
    public void loginSuccess(UserInfoModel userInfoModel, String uid) {

    }

    @Override
    public void loginFailure(String message) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void loginResponse(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            loginActivityInterface.showError(Constants.ErrorType.ERROR_EMPTY_EMAIL);
            return;
        } else if (TextUtils.isEmpty(password)) {
            loginActivityInterface.showError(Constants.ErrorType.ERROR_EMPTY_PASSWORD);
            return;
        }
    }
}
