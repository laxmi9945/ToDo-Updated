package com.app.todo.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    AppCompatEditText reset_editText;
    AppCompatButton resetButton, backButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        firebaseAuth = FirebaseAuth.getInstance();
        reset_editText = (AppCompatEditText) findViewById(R.id.resetpassword_editText);
        resetButton = (AppCompatButton) findViewById(R.id.reset_button);
        backButton = (AppCompatButton) findViewById(R.id.back);
        progressDialog = new ProgressDialog(this);
        resetButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_button:
                resetPassword();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void resetPassword() {
        String email = reset_editText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), getString(R.string.enter_registered_email), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.sending_reset_instruction));
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("abc", "onComplete: hi");
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.server_info), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.failed_message), Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                });
    }
}
