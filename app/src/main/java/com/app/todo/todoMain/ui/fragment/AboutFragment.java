package com.app.todo.todoMain.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class AboutFragment extends Fragment {

    public static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view=inflater.inflate(R.layout.fragment_about, container, false);
        getActivity().setTitle("About");
        setHasOptionsMenu(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }

}
