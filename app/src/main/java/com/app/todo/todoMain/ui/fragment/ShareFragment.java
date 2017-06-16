package com.app.todo.todoMain.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.localdatabase.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.ShareFragmentPresenter;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ShareFragment extends Fragment implements ShareFragmentInterface, OnSearchTextChange{
    public static final String TAG = "ShareFragment";
    RecyclerAdapter recyclerAdapter;
    List<NotesModel> allNotes = new ArrayList<>();
    List<NotesModel> filteredNotes = new ArrayList<>();
    ShareFragmentPresenter presenter;
    String uId;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    NotesModel notesModel = new NotesModel();
    ArrayList<NotesModel> notesModel2 = new ArrayList<>();
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    View view;
    SharedPreferences sharedPreferences;
    boolean isGrid = false;
    RecyclerView sharerecyclerView;
    // This is a handle so that we can call methods on our service
    AppCompatTextView sharetextView;
    boolean click=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        // Create a new service client and bind our activity to this service
        dataBaseUtility = new DataBaseUtility(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharerecyclerView= (RecyclerView) view.findViewById(R.id.shareFragment_recyclerView);
        sharetextView= (AppCompatTextView) view.findViewById(R.id.list_empty_textView);
        presenter = new ShareFragmentPresenter(getContext(), this);
        presenter.getNoteList(uId);
        setHasOptionsMenu(true);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys,
                Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isList", false)) {
            isGrid = false;

            sharerecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = true;
            sharerecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
        }

        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);
        sharerecyclerView.setAdapter(recyclerAdapter);
        ((TodoMainActivity)getActivity()).setSearchTagListener(this);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((TodoMainActivity) getActivity()).setToolbarTitle("Share Notes");
        Log.i(TAG, "onResume:Notes  ");
    }

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        if (isAdded()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void getNotesListSuccess(List<NotesModel> modelList) {
        filteredNotes.clear();
        allNotes.clear();
        for (NotesModel note : modelList) {
            if (!note.isArchived() && !note.isDeleted()) {
                allNotes.add(note);
            }
        }
        filteredNotes.addAll(allNotes);
        Log.i(TAG, "getNotesListSuccess: " + allNotes.size());
        recyclerAdapter.notifyDataSetChanged();
        if (filteredNotes.size() != 0) {
            sharetextView.setVisibility(View.INVISIBLE);

        } else {
            sharetextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getNotesListFailure(String message) {

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dataBaseUtility.getDatafromDB();
        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);
        sharerecyclerView.setAdapter(recyclerAdapter);

    }

    private List<NotesModel> getWithoutArchiveItems() {
        ArrayList<NotesModel> todoHomeDataModel = new ArrayList<>();
        for (NotesModel note : allNotes) {
            if (!note.isArchived()) {
                todoHomeDataModel.add(note);

                Log.i(TAG, "getWithoutArchiveItems: " + todoHomeDataModel);
            }
        }
        return todoHomeDataModel;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeview:
                toggle(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void toggle(MenuItem item) {

        if (!isGrid) {
            sharerecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isList", true);
            isGrid = true;
            edit.apply();

        } else {
            sharerecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_straggered);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isList", false);
            isGrid = false;
            edit.apply();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }

    private List<NotesModel> filter(List<NotesModel> models, String query) {
        query = query.toLowerCase();
        final List<NotesModel> filteredModelList = new ArrayList<>();
        for (NotesModel model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onSearchTagChange(String searchTag) {

        searchTag = searchTag.toLowerCase();

        if (!TextUtils.isEmpty(searchTag)) {
            ArrayList<NotesModel> newList = new ArrayList<>();
            for (NotesModel model : allNotes) {
                //recyclerAdapter.setFilter(allNotes);
                Log.i(TAG, "onQueryTextChange: "+allNotes);
                String name = model.getTitle().toLowerCase();
                if (name.contains(searchTag))
                    newList.add(model);
                Log.i(TAG, "onSearchTagChange: " + newList);

            }
            filteredNotes.clear();
            filteredNotes.addAll(newList);

        } else {
            filteredNotes.clear();
            filteredNotes.addAll(allNotes);
        }
        recyclerAdapter.notifyDataSetChanged();
        /*final List<NotesModel> filteredModelList = filter(allNotes, newText);

        recyclerAdapter.setFilter(filteredModelList);*/
    }
}
