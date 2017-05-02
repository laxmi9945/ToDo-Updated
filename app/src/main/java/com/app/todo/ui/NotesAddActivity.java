package com.app.todo.ui;

import android.app.DatePickerDialog;
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
import com.app.todo.database.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotesAddActivity extends BaseActivity implements View.OnClickListener {
    AppCompatImageButton imageButton;
    AppCompatTextView dateTextView, timeTextView, reminderTextView;
    AppCompatEditText titleEdittext, contentEdittext;
    public List<NotesModel> data = new ArrayList<>();
    DataBaseUtility database;
    private DatabaseReference mDatabaseReference;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    Date date;
    private static final String TAG = "NetworkStateReceiver";

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
    }

    @Override
    public void initView() {

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
                /*DialogFragment newFragment = new FragmentDatePicker();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                new DatePickerDialog(this, datePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return super.onOptionsItemSelected(item);

            case R.id.action_save:

                //database = new DataBaseUtility(this);
                final NotesModel model = new NotesModel();

                final String userId;
                String titleData = titleEdittext.getText().toString();
                String contentData = contentEdittext.getText().toString();
                final String recentDateData = dateTextView.getText().toString();
                final String recentTimeData2 = timeTextView.getText().toString();
                String reminder_Date = reminderTextView.getText().toString();
                userId = firebaseAuth.getCurrentUser().getUid();
                model.setTitle(titleData);
                model.setContent(contentData);
                model.setDate(recentDateData);
                model.setTime(recentTimeData2);
                model.setReminderDate(reminder_Date);
                //
                try {
                    mDatabaseReference.child("userData")
                            .addValueEventListener(new ValueEventListener() {
                                NotesModel notesModel = model;

                                GenericTypeIndicator<ArrayList<NotesModel>> typeIndicator = new GenericTypeIndicator<ArrayList<NotesModel>>() {
                                };

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int index = 0;
                                    ArrayList<NotesModel> notesModel_ArrayList = new ArrayList<NotesModel>();
                                    //notesModel_ArrayList = dataSnapshot.getValue(typeIndicator);
                                    if(dataSnapshot.hasChild(userId)){
                                        notesModel_ArrayList.addAll(dataSnapshot.child(userId)
                                                .child(recentDateData)
                                                .getValue(typeIndicator));
                                    }

                                    index = notesModel_ArrayList.size();
                                    if (notesModel != null) {
                                        //int index = (int) dataSnapshot.child(String.valueOf(model.getId())).getChildrenCount();
                                        if (index != 0) {
                                            model.setId(index);
                                            mDatabaseReference.child("userData")
                                                    .child(userId)
                                                    .child(recentDateData).child(String.valueOf(model.getId()))
                                                    .setValue(model);
                                        } else {
                                            model.setId(index);
                                            mDatabaseReference.child("userData")
                                                    .child(userId)
                                                    .child(recentDateData).child(String.valueOf(model.getId()))
                                                    .setValue(model);
                                        }
                                        notesModel = null;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }catch (Exception e){

                }
                finish();
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        reminderTextView.setText(sdf.format(myCalendar.getTime()));
        Toast.makeText(this, "Reminder set:" + reminderTextView.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                Log.i("abc", "onClick: ");
                Intent intent = new Intent(this, TodoNotesActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }

}
