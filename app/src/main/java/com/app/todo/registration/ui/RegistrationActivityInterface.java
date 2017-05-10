package com.app.todo.registration.ui;

import android.view.View;

import com.app.todo.model.UserInfoModel;



public interface RegistrationActivityInterface extends View.OnClickListener{
    void registrationSuccess(UserInfoModel userInfoModel, String uid);
    void registrationFailure(String message);
    void showProgressDialog(String message);
    void hideProgressDialog();

}