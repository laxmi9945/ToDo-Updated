package com.app.todo.todoMain.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;

import com.app.todo.R;

public class DummyActivity extends AppCompatActivity {
    AppCompatEditText titleEditText, contentEdtitext;
    AppCompatTextView reminderTextView;
    String str3, str4;
    int id;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_layout);
        titleEditText = (AppCompatEditText) findViewById(R.id.edit_title);
        contentEdtitext = (AppCompatEditText) findViewById(R.id.edit_content);
        reminderTextView = (AppCompatTextView) findViewById(R.id.reminderEdit_textView);
        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String notes_title = getIntent().getExtras().getString("title");
            String notes_content = getIntent().getExtras().getString("content");
            str3 = getIntent().getExtras().getString("date");
            str4 = getIntent().getExtras().getString("time");
            String reminder_date=getIntent().getExtras().getString("reminder");
            if (bundle.containsKey("id"))
                id = (bundle.getInt("id"));
            //String str5=getArguments().getString("id");
            titleEditText.setText(notes_title);
            contentEdtitext.setText(notes_content);
            reminderTextView.setText(reminder_date);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
