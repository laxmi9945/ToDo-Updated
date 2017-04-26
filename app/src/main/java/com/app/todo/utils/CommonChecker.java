package com.app.todo.utils;

/**
 * Created by bridgeit on 25/4/17.
 */

public class CommonChecker {

    

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
