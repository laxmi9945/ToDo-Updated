package com.app.todo.login.interactor;

import android.content.Context;

/**
 * Created by bridgeit on 22/4/17.
 */

public class LoginInterActor  {
    Context context;
    LoginInterActorInterface loginInterActorInterface;

    public LoginInterActor(Context context, LoginInterActorInterface loginInterActorInterface) {
        this.context = context;
        this.loginInterActorInterface = loginInterActorInterface;

    }
}
