package com.app.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.app.todo.R;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    //SharedPreferences sharedPreferences;
    AppCompatTextView textView;
    Animation animation;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, TodoNotesActivity.class));
        }
        textView = (AppCompatTextView) findViewById(R.id.appCompatTextView);
        animation = new TranslateAnimation(450, 0, 450, 0);
        animation.setDuration(Constants.Splash_textView_animation_time);
        animation.setRepeatMode(Animation.RESTART);
        textView.startAnimation(animation);
        final ImageView imageView = (ImageView) findViewById(R.id.splashimage);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
        imageView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(anim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }, Constants.SplashScreen_TimeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        /*sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("login")) {
            if (sharedPreferences.getString("login", "false").equals("true")) {
                //Log.i("bb", "onCreate: "+sharedPreferences.getString("login","false"));
                Intent intent1 = new Intent(this, TodoNotesActivity.class);
                startActivity(intent1);
                finish();
            }
        }*/

    }

}
