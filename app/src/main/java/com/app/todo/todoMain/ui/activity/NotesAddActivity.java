package com.app.todo.todoMain.ui.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.todoMain.presenter.NotesAddPresenter;
import com.app.todo.todoMain.presenter.NotesAddPresenterInterface;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesAddActivity extends BaseActivity implements NotesAddActivityInterface {
    private AppCompatImageButton imageButton;
    private AppCompatTextView dateTextView;
    private AppCompatTextView timeTextView;
    private AppCompatTextView reminderTextView;
    private AppCompatEditText titleEdittext;
    private AppCompatEditText contentEdittext;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private Date date;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener datePicker;
    private static final String TAG = "NetworkStateReceiver";

    NotesAddPresenterInterface presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesadd);
        firebaseAuth = FirebaseAuth.getInstance();

        initView();
        date = new Date();
        CharSequence sequence = DateFormat.format(getString(R.string.date_time), date.getTime());
        CharSequence sequence2 = DateFormat.format(getString(R.string.time), date.getTime());

        dateTextView.setText(sequence);
        timeTextView.setText(sequence2);
        sharedPreferences = this.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        datePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }

    @Override
    public void initView() {

        presenter = new NotesAddPresenter(this, this);

        myCalendar = Calendar.getInstance();
        imageButton = (AppCompatImageButton) findViewById(R.id.back_button);
        dateTextView = (AppCompatTextView) findViewById(R.id.recenttime_textView);
        timeTextView = (AppCompatTextView) findViewById(R.id.time_textView);
        titleEdittext = (AppCompatEditText) findViewById(R.id.title_edittext);
        contentEdittext = (AppCompatEditText) findViewById(R.id.content_edittext);
        reminderTextView = (AppCompatTextView) findViewById(R.id.reminderDate);

        setClicklistener();



    }

    @Override
    public void setClicklistener() {
        imageButton.setOnClickListener(this);
        dateTextView.setOnClickListener(this);
        titleEdittext.setOnClickListener(this);
        contentEdittext.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_item_details_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_pushpin:

                Toast.makeText(this, getString(R.string.pined), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            case R.id.action_reminder:

                new DatePickerDialog(this, datePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                return super.onOptionsItemSelected(item);

            case R.id.action_save:


                Bundle bundle = new Bundle();
                bundle.putString(Constants.currentTimeKey, timeTextView.getText().toString());
                bundle.putString(Constants.titleKey, titleEdittext.getText().toString());
                bundle.putString(Constants.descriptionKey, contentEdittext.getText().toString());
                bundle.putString(Constants.currentDateKey, dateTextView.getText().toString());
                bundle.putString(Constants.reminderKey, reminderTextView.getText().toString());

                presenter.addNoteToFirebase(bundle);

                finish();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateLabel() {

        String myFormat = "MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        reminderTextView.setText(sdf.format(myCalendar.getTime()));
        Calendar current = Calendar.getInstance();
        if ((myCalendar.compareTo(current) <= 0)) {
            //The set Date/Time already passed
            new DatePickerDialog(this, datePicker, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.invalid_date),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.reminder_set) + reminderTextView.getText().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                Log.i("abc", "onClick: ");
                Intent intent = new Intent(this, TodoMainActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }

    ProgressDialog progressDialog;

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(this);

        if(!isFinishing()){
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if(!isFinishing() && progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void noteAddSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteAddFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}