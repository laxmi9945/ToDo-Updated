package com.app.todo.login.ui;

import android.view.View;

import com.app.todo.model.UserInfoModel;

/**
 * Created by bridgeit on 24/4/17.
 */

public interface LoginActivityInterface extends View.OnClickListener {
    void loginSuccess(UserInfoModel userInfoModel, String uid);
    void loginFailure(String message);
    void showProgressDialog(String message);
    void hideProgressDialog();
    void showError(int errorType);
}
