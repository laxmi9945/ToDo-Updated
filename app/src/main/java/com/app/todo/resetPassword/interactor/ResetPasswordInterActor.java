package com.app.todo.resetPassword.interactor;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.resetPassword.presenter.ResetPasswordPresenterInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordInterActor implements ResetPasswordInterActorInterface {
    Context context;
    ResetPasswordPresenterInterface presenterInterface;
    FirebaseAuth firebaseAuth;

    public ResetPasswordInterActor(Context context, ResetPasswordPresenterInterface presenterInterface) {
        this.context = context;
        this.presenterInterface = presenterInterface;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void resetPassword(String email) {
        presenterInterface.showDialog(context.getString(R.string.sending_reset_instruction));
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("abc", "onComplete: hi");
                            Toast.makeText(context,
                                    context.getString(R.string.server_info), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.failed_message), Toast.LENGTH_SHORT)
                                    .show();
                        }
                       // presenterInterface.resetPasswordSuccess();
                        presenterInterface.hideDialog();
                    }
                });
    }
}

