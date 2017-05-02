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
import com.app.todo.registration.presenter.RegistrationPresenter;
import com.app.todo.model.UserInfoModel;
import com.app.todo.ui.TodoNotesActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityInterface{
    AppCompatEditText edittextName, edittextemail, edittextpswrd, edittextmobNo;
    AppCompatButton buttonSave;
    AppCompatTextView textView;
    Pattern pattern, pattern2;
    Matcher matcher, matcher2;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabaseReference;
    UserInfoModel userInfoModel;

    RegistrationPresenter registrationPresenter;
    String Name,Email, Password, MobileNo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        registrationPresenter=new RegistrationPresenter(this,this);
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
        setClicklistener();

       // registrationPresenter = new RegistrationPresenter(this, this);
    }

    public void setClicklistener() {
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

    public boolean registerUser() {
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
            edittextpswrd.setError("Password too simple! It must have atleast 1 numeric or special character");
            edittextpswrd.requestFocus();

        }


        if (MobileNo.isEmpty()) {
            edittextmobNo.setError("Enter your mobile number");
            edittextpswrd.requestFocus();

        } else {
            checkMobNo = true;
        }
        return checkName && checkMail && checkMobNo && checkPassword;
    }


        /*if (checkName && checkMail && checkMobNo && checkPassword) {

            progressDialog.setMessage(getString(R.string.registering));
            progressDialog.show();

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
    }*/

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void registrationSuccess(UserInfoModel userInfoModel, String uid) {
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegistrationActivity.this, TodoNotesActivity.class);
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



