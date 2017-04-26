package com.app.todo.login.interactor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.app.todo.login.presenter.LoginPresenterInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bridgeit on 22/4/17.
 */

public class LoginInterActor implements LoginInterActorInterface  {
    Context context;
    LoginPresenterInterface loginPresenterInterface;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    public LoginInterActor(Context context, LoginPresenterInterface loginPresenterInterface) {
        this.context = context;
        this.loginPresenterInterface = loginPresenterInterface;
    }

    @Override
    public void loginResponse(String email, String password) {
        loginPresenterInterface.showProgressDialog();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("userInfo");
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });
    }
}
