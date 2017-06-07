package com.app.todo.todoMain.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;

import com.app.todo.R;
import com.app.todo.model.NotesModel;

import java.util.ArrayList;
import java.util.List;


public class ReminderNotifyActivity extends Activity {
    AppCompatTextView titleTextView,contentTextView;
    List<NotesModel> allNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        titleTextView= (AppCompatTextView) findViewById(R.id.title_TextView);
        contentTextView= (AppCompatTextView) findViewById(R.id.content_TextView);
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String title = extras.getString("title");
            String content = extras.getString("content");
            System.out.println("data : " +title );
            System.out.println("data : " +content );
            titleTextView.setText(title);
            contentTextView.setText(content);
        }

    }
}
