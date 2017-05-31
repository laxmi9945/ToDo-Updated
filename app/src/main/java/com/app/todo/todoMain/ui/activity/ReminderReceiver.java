package com.app.todo.todoMain.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.app.todo.R;


public class ReminderReceiver extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy);
        Bundle bundle=getIntent().getExtras();
        /*String venName = bundle.getString("name");
        Toast.makeText(this,venName, Toast.LENGTH_SHORT).show();*/
    }
    /*@Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "running", Toast.LENGTH_SHORT).show();
    }*/
}
