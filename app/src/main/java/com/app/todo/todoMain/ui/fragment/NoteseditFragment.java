package com.app.todo.todoMain.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class NoteseditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NoteseditFragment";
    AppCompatEditText titleEditText, contentEdtitext;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    NotesModel notesModel;
    String uId;
    int id;
    Date date;
    String str3,str4;
    CharSequence sequence,sequence2;
    AppCompatTextView dateTextView,timeTextView;
    public NoteseditFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_todoitemsdetails, container, false);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference().child("userData");
        titleEditText = (AppCompatEditText) view.findViewById(R.id.edit_title);
        contentEdtitext = (AppCompatEditText) view.findViewById(R.id.edit_content);
        AppCompatButton button = (AppCompatButton) view.findViewById(R.id.save_btn);
        uId = firebaseAuth.getCurrentUser().getUid();
        date = new Date();
        sequence = DateFormat.format(getString(R.string.date_time), date.getTime());
        sequence2 = DateFormat.format(getString(R.string.time), date.getTime());
       /* dateTextView = (AppCompatTextView)view.findViewById(R.id.recenttime_textView);
        timeTextView = (AppCompatTextView)view.findViewById(R.id.time_textView);*/
        button.setOnClickListener(this);
        Bundle bundle = getArguments();

        if (bundle != null) {
            String str1 = getArguments().getString("title");
            String str2 = getArguments().getString("content");
            str3=getArguments().getString("date");
            str4=getArguments().getString("time");
            if (bundle.containsKey("id"))
                id = (bundle.getInt("id"));
            //String str5=getArguments().getString("id");

            titleEditText.setText(str1);
            contentEdtitext.setText(str2);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                //startActivity(new Intent(getActivity(),TodoMainActivity.class));
                DataBaseUtility dataBaseUtility=new DataBaseUtility(getActivity());
                notesModel=new NotesModel();
                notesModel.setTitle(titleEditText.getText().toString());
                notesModel.setContent(contentEdtitext.getText().toString());
                notesModel.setDate(str3);
                notesModel.setTime(str4);
                notesModel.setId(id);
                dataBaseUtility.updateNote(notesModel);
               try{
                   databaseReference
                           .child(uId).child(str3).child(String.valueOf(notesModel.getId()))
                           .setValue(notesModel);
               }catch(Exception e){
                   Log.i(TAG, "onClick: "+e);
               }
                getActivity().getFragmentManager().popBackStackImmediate();

                break;

        }
    }


}
