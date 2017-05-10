package com.app.todo.resetPassword.presenter;



public interface ResetPasswordPresenterInterface {
    void showDialog(String message);
    void hideDialog();

    void resetPasswordSuccess(String message);
    void resetPasswordFailure(String message);
}
