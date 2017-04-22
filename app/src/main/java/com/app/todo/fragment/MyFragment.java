package com.app.todo.fragment;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.ui.TodoNotesActivity;

public class MyFragment extends Fragment implements View.OnClickListener {
    AppCompatEditText titleEditText, contentEdtitext;
    FloatingActionButton floatingActionButton;
    TodoNotesActivity todoNotesActivity;

    public MyFragment() {

    }

    public MyFragment(TodoNotesActivity todoNotesActivity) {
        this.todoNotesActivity = todoNotesActivity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_todoitemsdetails, container, false);
        titleEditText = (AppCompatEditText) view.findViewById(R.id.edit_title);
        contentEdtitext = (AppCompatEditText) view.findViewById(R.id.edit_content);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_button);
        AppCompatButton button = (AppCompatButton) view.findViewById(R.id.save_btn);
        todoNotesActivity.floatingActionButton.setVisibility(View.INVISIBLE);
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
                 Intent intent = new Intent(getActivity(), TodoNotesActivity.class);
                startActivity(intent);
                getActivity().finish();
                DataBaseUtility dataBaseUtility=new DataBaseUtility(getActivity());
                //NotesModel notesModel=new NotesModel(titleEditText.getText().toString(),contentEdtitext.getText().toString());
                ContentValues contentValues=new ContentValues();
                //NotesModel notesModel=new NotesModel;

                //dataBaseUtility.updateNote(notesModel);
                //todoNotesActivity.setData(notesModel);
                getActivity().getFragmentManager().popBackStackImmediate();
                break;
        }
    }

}
