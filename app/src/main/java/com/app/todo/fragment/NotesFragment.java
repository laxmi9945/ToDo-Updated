package com.app.todo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;

/**
 * Created by bridgeit on 6/4/17.
 */

public class NotesFragment extends Fragment   {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.actvity_recycler, container, false);
        //((TodoNotesActivity) getActivity()).setOnBackPressedListener(this);
        //TodoNotesActivity.OnBackPressedListener
        //toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        return view;
    }

    /* @Override
     public void onResume() {
         super.onResume();
         ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
     }
     @Override
     public void onStop() {
         super.onStop();
         ((AppCompatActivity)getActivity()).getSupportActionBar().show();
     }
 */
    /*@Override
    public void doBack() {
        Intent intent = new Intent(getActivity(), TodoNotesActivity.class);
        startActivity(intent);
        getActivity().finish();
    }*/
}
