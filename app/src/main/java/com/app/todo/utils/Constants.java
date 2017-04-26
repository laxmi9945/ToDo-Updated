package com.app.todo.utils;

/**
 * Created by bridgeit on 28/3/17.
 */

public class Constants {
    public static String keys="laxmi";
    public static String values="value";
    public static String Name="name";
    public static String Email="email";
    public static String Password="password";
    public static String MobileNo="mobileNo";
    public static String title_data="Title_data";
    public static String content_data="Content_data";
    public static String date_data="date_data";
    public static int SplashScreen_TimeOut=3000;
    public static int Splash_textView_animation_time=2000;
    public static String Mobile_Pattern = "\\\\d{3}-\\\\d{7}";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String Password_Pattern="^(?=.*[a-z])(?=.*[0-9]).{5,12}$";
    //public static String Password_Pattern="^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{5,12}$";
    public static String fb_first_name="first_name";
    public static String fb_last_name="last_name";
    public static String fb_email="email";
    public static String fb_profile_pic="profile_pic";

    public interface ErrorType{
        public static final int NO_INTERNET_CONNECTION=0;
        public static final int ERROR_INVALID_EMAIL=1;
        public static final int ERROR_INVALID_PASSWORD=2;
        public static final int ERROR_EMPTY_EMAIL=3;
        public static final int ERROR_EMPTY_PASSWORD=4;
    }
}
