package com.app.todo.resetPassword.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.todo.R;
import com.app.todo.resetPassword.presenter.ResetPasswordPresenter;
import com.app.todo.resetPassword.presenter.ResetPasswordPresenterInterface;
import com.app.todo.utils.CommonChecker;


public class ResetPasswordActivity extends AppCompatActivity implements ResetPasswordActivityInterface {
    AppCompatEditText reset_editText;
    AppCompatButton resetButton, backButton;
    ProgressDialog progressDialog;
    ResetPasswordPresenterInterface presenter;
    Snackbar snackbar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        presenter = new ResetPasswordPresenter(this, this);
        relativeLayout= (RelativeLayout) findViewById(R.id.resetPwd_root_layout);
        reset_editText = (AppCompatEditText) findViewById(R.id.resetpassword_editText);
        resetButton = (AppCompatButton) findViewById(R.id.reset_button);
        backButton = (AppCompatButton) findViewById(R.id.back);
        progressDialog = new ProgressDialog(this);
        resetButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        checkNetwork();
    }

    private void checkNetwork() {

        if (CommonChecker.isNetworkConnected(ResetPasswordActivity.this)) {

        } else {
            snackbar = Snackbar
                    .make(relativeLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackbar.dismiss();

                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            AppCompatTextView textView = (AppCompatTextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_button:
                //presenter.resetPasswordResponse(reset_editText.getText().toString());
                presenter.resetPassword(reset_editText.getText().toString());
                //resetPassword();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(this);

        if (!isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (!isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void resetPasswordSuccess(String message) {

    }

    @Override
    public void resetPasswordFailure(String message) {

    }
}
