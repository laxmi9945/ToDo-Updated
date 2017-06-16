package com.app.todo.todoMain.ui.fragment;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
public class AboutFragment extends Fragment {

    public static final String TAG = "AboutFragment";
    Intent intent;
    long profileId = Long.parseLong("1232912816826387");
    AppCompatTextView fbLink;
    //long profileId=1232912816826387;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view=inflater.inflate(R.layout.fragment_about, container, false);
        getActivity().setTitle("About");
        setHasOptionsMenu(true);

        /*Intent facebookIntent = getOpenFacebookIntent(getActivity());
        startActivity(facebookIntent);*/
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.about,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
