package com.app.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatEditText edittextName, edittextemail, edittextpswrd, edittextmobNo;
    AppCompatButton buttonSave;
    AppCompatTextView textView;
    Pattern pattern, pattern2;
    Matcher matcher, matcher2;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabaseReference;
    UserInfoModel userInfoModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        initView();

    }

    public void initView() {
        buttonSave = (AppCompatButton) findViewById(R.id.save_button);
        edittextName = (AppCompatEditText) findViewById(R.id.name_edittext);
        edittextemail = (AppCompatEditText) findViewById(R.id.email_Edittext);
        edittextpswrd = (AppCompatEditText) findViewById(R.id.password_edittext);
        edittextmobNo = (AppCompatEditText) findViewById(R.id.mobilenumber_edittext);
        textView = (AppCompatTextView) findViewById(R.id.allreadyacc_textview);
        edittextmobNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        setClicklistener();
    }

    public void setClicklistener() {
        buttonSave.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                registerUser();

                break;

            case R.id.allreadyacc_textview:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                break;
        }
    }

    public void registerUser() {

        /*pattern = Pattern.compile(Constants.Password_Pattern);
        matcher = pattern.matcher(edittextpswrd.getText().toString());
        pattern2 = Pattern.compile(Constants.Mobile_Pattern);
        matcher2 = pattern2.matcher(edittextmobNo.getText().toString());
        userInfoModel = new UserInfoModel();
        final String userName = edittextName.getText().toString();
        final String userEmail = edittextemail.getText().toString();
        final String userPassword = edittextpswrd.getText().toString();
        final String userMobileNo = edittextmobNo.getText().toString();
       *//* Bundle bundle = new Bundle();
        bundle.putString(Constants.Name, userInfoModel.getName());
        bundle.putString(Constants.Email, userInfoModel.getEmail());
        bundle.putString(Constants.Password, userInfoModel.getPassword());
        bundle.putString(Constants.MobileNo, String.valueOf(userInfoModel.getMobile()));*//*

        if (TextUtils.isEmpty(userName)) {
            edittextName.setError(getString(R.string.field_msg));
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            edittextemail.setError(getString(R.string.field_msg));
            return;

        } else if (!isValidEmail(userEmail)) {
            edittextemail.setError(getString(R.string.format_invalid));

        }
        if (TextUtils.isEmpty(userPassword)) {
            edittextpswrd.setError(getString(R.string.field_msg));
            return;
        } else if (matcher.matches()) {

        } else {
            edittextpswrd.setError(getString(R.string.wrong_format));
            edittextpswrd.requestFocus();
        }
        if (TextUtils.isEmpty(userMobileNo)) {
            edittextmobNo.setError(getString(R.string.field_msg));
            edittextmobNo.requestFocus();
            return;
        } else if (matcher2.matches()) {

        } else {
            edittextmobNo.setError(getString(R.string.wrong_format));
            edittextmobNo.requestFocus();
        }*/
        pattern = Pattern.compile(Constants.Password_Pattern);
        matcher = pattern.matcher(edittextpswrd.getText().toString());
        pattern2 = Pattern.compile(Constants.Mobile_Pattern);
        matcher2 = pattern.matcher(edittextmobNo.getText().toString());
        userInfoModel = new UserInfoModel();
        final String userName = edittextName.getText().toString();
        final String userEmail = edittextemail.getText().toString();
        final String userPassword = edittextpswrd.getText().toString();
        final String userMobileNo = edittextmobNo.getText().toString();
        boolean checkName = false, checkMail = false, checkPassword = false, checkMobNo = false;
        String Name = edittextName.getText().toString();
        String Email = edittextemail.getText().toString();
        String Password = edittextpswrd.getText().toString();
        //Log.i("", "Save: "+Password);
        String MobileNo = edittextmobNo.getText().toString();

        if (Name.isEmpty()) {
            edittextName.setError("First name not entered");
            edittextpswrd.requestFocus();
        } else {
            checkName = true;
        }

        if (Email.isEmpty()) {
            edittextemail.setError("Email should not empty");
            edittextpswrd.requestFocus();
        } else if (!isValidEmail(Email)) {
            edittextemail.setError("Invalid Email");
            edittextpswrd.requestFocus();
        } else {
            checkMail = true;
        }

        if (Password.isEmpty()) {
            edittextpswrd.setError("Password should not empty");
            edittextpswrd.requestFocus();
        } else if (matcher.matches()) {
            checkPassword = true;
        } else {
            edittextpswrd.setError("invalid format");
            edittextpswrd.requestFocus();

        }


        if (MobileNo.isEmpty()) {
            edittextmobNo.setError("Enter your mobile number");
            edittextpswrd.requestFocus();
        } else {
            checkMobNo = true;
        }


        if (checkName && checkMail && checkMobNo && checkPassword)

            progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();
        if (checkName && checkMail && checkMobNo && checkPassword) {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        UserInfoModel userInfoModel1 = new UserInfoModel();
                        userInfoModel1.setName(userName);
                        userInfoModel1.setEmail(userEmail);
                        userInfoModel1.setPassword(userPassword);
                        userInfoModel1.setMobile(userMobileNo);
                        //String id = mDatabaseReference.push().getKey();
                       // Log.i("abc", "onComplete: " + userEmail + userMobileNo + userName + userPassword);
                        mDatabaseReference.child("userInfo").child(task.getResult().getUser().getUid()).setValue(userInfoModel1);
                        // databaseReference= FirebaseDatabase.getInstance().getReference("");
                        //databaseReference.child("userinfo").child(task.getResult().getUser().getUid()).setValue(userInfoModel);
                        Intent intent = new Intent(RegistrationActivity.this, TodoNotesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setMessage(task.getException().getMessage())
                                .setTitle(R.string.login_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    progressDialog.dismiss();
                }
            });

        }
    }

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}



