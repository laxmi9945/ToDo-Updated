package com.app.todo.login.interactor;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.app.todo.R;
import com.app.todo.login.presenter.LoginPresenterInterface;
import com.app.todo.model.UserInfoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginInterActor implements LoginInterActorInterface  {
    Context context;
    LoginPresenterInterface loginPresenterInterface;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    UserInfoModel userInfoModel;
    SharedPreferences sharedPreferences;
    public LoginInterActor(Context context, LoginPresenterInterface loginPresenterInterface) {
        this.context = context;
        this.loginPresenterInterface = loginPresenterInterface;
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void loginResponse(String email, String password) {
        loginPresenterInterface.showProgressDialog(context.getString(R.string.login_msg));

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String uid=task.getResult().getUser().getUid();
                    userData(uid);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    private void userData(final String uid) {
        databaseReference.child("userInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfoModel userInfoModel=dataSnapshot.getValue(UserInfoModel.class);
                loginPresenterInterface.loginSuccess(userInfoModel,uid);
                loginPresenterInterface.hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
