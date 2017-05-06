package com.app.todo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.todoMain.ui.TodoNotesActivity;

/**
 * Created by bridgeit on 6/4/17.
 */

public class NotesFragment extends Fragment   {

    public static final String TAG = "NotesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.actvity_recycler, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TodoNotesActivity)getActivity()).setToolbarTitle("Notes");
    }

}
