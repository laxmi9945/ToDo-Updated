package com.app.todo.resetPassword.ui;


import android.view.View;

public interface ResetPasswordActivityInterface extends View.OnClickListener{
    void showDialog(String message);
    void hideDialog();
    void resetPasswordSuccess(String message);
    void resetPasswordFailure(String message);
}
