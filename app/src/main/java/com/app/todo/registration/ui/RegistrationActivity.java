package com.app.todo.registration.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.registration.presenter.RegistrationPresenter;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityInterface{
    private AppCompatEditText edittextName;
    private AppCompatEditText edittextemail;
    private AppCompatEditText edittextpswrd;
    private AppCompatEditText edittextmobNo;
    private AppCompatButton buttonSave;
    private AppCompatTextView textView;
    private Pattern pattern;
    private Pattern pattern2;
    private Matcher matcher;
    private Matcher matcher2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private UserInfoModel userInfoModel;

    private RegistrationPresenter registrationPresenter;
    private String Name;
    private String Email;
    private String Password;
    private String MobileNo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        registrationPresenter=new RegistrationPresenter(this,this);
        progressDialog = new ProgressDialog(this);
        initView();

    }

    private void initView() {
        buttonSave = (AppCompatButton) findViewById(R.id.save_button);
        edittextName = (AppCompatEditText) findViewById(R.id.name_edittext);
        edittextemail = (AppCompatEditText) findViewById(R.id.email_Edittext);
        edittextpswrd = (AppCompatEditText) findViewById(R.id.password_edittext);
        edittextmobNo = (AppCompatEditText) findViewById(R.id.mobilenumber_edittext);
        textView = (AppCompatTextView) findViewById(R.id.allreadyacc_textview);
        setClicklistener();

    }

    private void setClicklistener() {
        buttonSave.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                if(registerUser()) {
                    userInfoModel = new UserInfoModel();
                    userInfoModel.setName(Name);
                    userInfoModel.setEmail(Email);
                    userInfoModel.setPassword(Password);
                    userInfoModel.setMobile(MobileNo);
                    registrationPresenter.registrationResponse(userInfoModel);

                }
                break;

            case R.id.allreadyacc_textview:
                finish();
                break;
        }
    }

    private boolean registerUser() {
        boolean checkName = false, checkMail = false, checkPassword = false, checkMobNo = false;
        pattern = Pattern.compile(Constants.Password_Pattern);
        matcher = pattern.matcher(edittextpswrd.getText().toString());
        pattern2 = Pattern.compile(Constants.Mobile_Pattern);
        matcher2 = pattern.matcher(edittextmobNo.getText().toString());
        Name = edittextName.getText().toString();
        Email = edittextemail.getText().toString();
        Password = edittextpswrd.getText().toString();
        //Log.i("", "Save: "+Password);
        MobileNo = edittextmobNo.getText().toString();


        if (Name.isEmpty()) {
            edittextName.setError(getString(R.string.first_name_field));
            edittextpswrd.requestFocus();

        } else {
            checkName = true;
        }

        if (Email.isEmpty()) {
            edittextemail.setError(getString(R.string.email_field_condition));
            edittextpswrd.requestFocus();

        } else if (!isValidEmail(Email)) {
            edittextemail.setError(getString(R.string.invalid_email));
            edittextpswrd.requestFocus();

        } else {
            checkMail = true;
        }

        if (Password.isEmpty()) {
            edittextpswrd.setError(getString(R.string.password_field_condition));
            edittextpswrd.requestFocus();

        } else if (matcher.matches()) {
            checkPassword = true;
        } else {
            edittextpswrd.setError(getString(R.string.password_hint));
            edittextpswrd.requestFocus();

        }


        if (MobileNo.isEmpty()) {
            edittextmobNo.setError(getString(R.string.enter_mobile));
            edittextpswrd.requestFocus();

        } else {
            checkMobNo = true;
        }
        return checkName && checkMail && checkMobNo && checkPassword;
    }

    private static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void registrationSuccess(UserInfoModel userInfoModel, String uid) {

        Intent intent = new Intent(RegistrationActivity.this, TodoMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void registrationFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(String message) {
        if(!isFinishing()&& progressDialog!=null){
            progressDialog.setMessage(message);
           // progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
            if (!isFinishing()){
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }
    }
}



