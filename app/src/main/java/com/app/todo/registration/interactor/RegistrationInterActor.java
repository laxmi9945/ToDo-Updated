package com.app.todo.registration.interactor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.registration.presenter.RegistrationPresenterInterface;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationInterActor implements RegistrationInterActorInterface {
    private final Context context;
    private final RegistrationPresenterInterface registrationPresenterInterface;
    private DatabaseReference databaseReference;
    private final FirebaseAuth firebaseAuth;
    private String uId;

    public RegistrationInterActor(Context context, RegistrationPresenterInterface
            registrationPresenterInterface) {
        this.context = context;
        this.registrationPresenterInterface = registrationPresenterInterface;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void registrationResponse(final UserInfoModel userInfoModel) {

        registrationPresenterInterface.showProgressDialog(context.getString(R.string.registration_msg));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String userRegistrationEmail = userInfoModel.getEmail();
        String userRegistrationPassword = userInfoModel.getPassword();

        firebaseAuth.createUserWithEmailAndPassword(userRegistrationEmail,
                userRegistrationPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    databaseReference.child(Constants.firebase_userInfo).child(task.getResult()
                            .getUser().getUid()).setValue(userInfoModel);
                    firebaseAuth.signInWithEmailAndPassword(userInfoModel.getEmail(),
                            userInfoModel.getPassword())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        uId = firebaseAuth.getCurrentUser().getUid();
                                        registrationPresenterInterface
                                                .registrationSuccess(userInfoModel, uId);
                                    } else {
                                        registrationPresenterInterface.registrationFailure
                                                (context.getString(R.string.after_reg_login_fail_message));
                                    }
                                }
                            });

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                registrationPresenterInterface.hideProgressDialog();
            }
        });
    }

}
