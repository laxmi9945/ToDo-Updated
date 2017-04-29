package com.app.todo.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.ui.TodoNotesActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NoteseditFragment extends Fragment implements View.OnClickListener {
    AppCompatEditText titleEditText, contentEdtitext;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TodoNotesActivity todoNotesActivity;

    public NoteseditFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoNotesActivity) getActivity()).showOrHideFab(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_todoitemsdetails, container, false);
        databaseReference = firebaseDatabase.getReference().child("userData");

        titleEditText = (AppCompatEditText) view.findViewById(R.id.edit_title);
        contentEdtitext = (AppCompatEditText) view.findViewById(R.id.edit_content);
        AppCompatButton button = (AppCompatButton) view.findViewById(R.id.save_btn);

        button.setOnClickListener(this);
        Bundle bundle = getArguments();

        if (bundle != null) {

            String str1 = getArguments().getString("title");
            String str2 = getArguments().getString("content");
            titleEditText.setText(str1);
            contentEdtitext.setText(str2);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:

              /*  DataBaseUtility dataBaseUtility = new DataBaseUtility(getActivity());
                NotesModel notesModel = new NotesModel(titleEditText.getText().toString(), contentEdtitext.getText().toString(),);
                //ContentValues contentValues=new ContentValues();
                dataBaseUtility.updateNote(notesModel);
                todoNotesActivity.setData(notesModel);
                getActivity().getFragmentManager().popBackStackImmediate();*/
                getActivity().finish();
                break;
        }
    }

}
