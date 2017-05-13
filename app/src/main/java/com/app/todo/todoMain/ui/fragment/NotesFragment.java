package com.app.todo.todoMain.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;


public class NotesFragment extends Fragment   {

    public static final String TAG = "NotesFragment";
    TodoMainActivity todoMainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.actvity_recycler, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TodoMainActivity)getActivity()).setToolbarTitle("Notes");
    }

}
