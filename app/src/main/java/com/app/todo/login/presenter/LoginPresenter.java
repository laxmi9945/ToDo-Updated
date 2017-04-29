package com.app.todo.login.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.app.todo.login.interactor.LoginInterActor;
import com.app.todo.login.ui.LoginActivityInterface;
import com.app.todo.model.UserInfoModel;
import com.app.todo.utils.CommonChecker;
import com.app.todo.utils.Constants;


public class LoginPresenter implements LoginPresenterInterface {
    Context context;
    LoginActivityInterface viewInterface;

    LoginInterActor loginInterActor;

    public LoginPresenter(Context context, LoginActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        loginInterActor = new LoginInterActor(context, this);
    }

    @Override
    public void loginSuccess(UserInfoModel userInfoModel, String uid) {
        viewInterface.loginSuccess(userInfoModel, uid);
    }

    @Override
    public void loginFailure(String message) {
        viewInterface.loginFailure(message);
    }

    @Override
    public void showProgressDialog(String message) {
        viewInterface.showProgressDialog(message);
    }



    @Override
    public void hideProgressDialog() {
        viewInterface.hideProgressDialog();
    }

    @Override
    public void loginResponse(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            viewInterface.showError(Constants.ErrorType.ERROR_EMPTY_EMAIL);
            return;
        }
        else if (!CommonChecker.isValidEmail(email)) {
            viewInterface.showError(Constants.ErrorType.ERROR_INVALID_EMAIL);
            return;
        }else if (TextUtils.isEmpty(password)) {
            viewInterface.showError(Constants.ErrorType.ERROR_EMPTY_PASSWORD);
            return;
        }else if (!CommonChecker.isNetworkConnected(context)){
            viewInterface.showError(Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION);
            return;
        }else {
            loginInterActor.loginResponse(email,password);
        }
    }
}
