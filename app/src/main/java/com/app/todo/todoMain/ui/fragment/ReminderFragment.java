package com.app.todo.todoMain.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.ReminderFragmentPresenter;
import com.app.todo.todoMain.presenter.ReminderFragmentPresenterInterface;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ReminderFragment extends Fragment implements ReminderFragmentInterface,
        viewFragment,OnSearchTextChange {
    public static final String TAG = "ReminderTag";
    TodoMainActivity todoMainActivity;
    ReminderFragmentPresenterInterface presenter;
    FirebaseAuth firebaseAuth;
    RecyclerAdapter reminder_adapter;
    RecyclerView mrecyclerView;
    ProgressDialog progressDialog;
    AppCompatTextView reminderTextView;
    AppCompatImageView reminderImageView;
    LinearLayout linearLayout;
    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
    List<NotesModel> allNotes = new ArrayList<>();
    List<NotesModel> filteredNotes = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        initView(view);
        getActivity().setTitle("Reminder");
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getReminderNotes(uId);
        return view;
    }

    private void initView(View view) {
        linearLayout= (LinearLayout) view.findViewById(R.id.reminder_rootLayout);
        mrecyclerView = (RecyclerView) view.findViewById(R.id.reminder_recyclerView);
        reminderTextView= (AppCompatTextView) view.findViewById(R.id.reminder_textView);
        reminderImageView= (AppCompatImageView) view.findViewById(R.id.reminder_event_icon);
        firebaseAuth = FirebaseAuth.getInstance();
        mrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }

    public ReminderFragment(TodoMainActivity todoMainActivity) {
        this.todoMainActivity = todoMainActivity;
        presenter = new ReminderFragmentPresenter(todoMainActivity, this);
    }

    @Override
    public void showDialog(String message) {
        progressDialog=new ProgressDialog(todoMainActivity);
        if (!todoMainActivity.isFinishing()){
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if(!todoMainActivity.isFinishing() && progressDialog !=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void gettingReminderSuccess(List<NotesModel> notesModelList) {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date_time));
        String currentDate = format.format(date.getTime());
        ArrayList<NotesModel> reminderNoteList=new ArrayList<>();
        for (NotesModel notesModel: notesModelList){
            if(notesModel.getReminderDate()!=null){
            if (notesModel.getReminderDate().equals(currentDate) && !(notesModel.isArchived())){
                reminderNoteList.add(notesModel);
            }
        }}
        reminder_adapter= new RecyclerAdapter(todoMainActivity,reminderNoteList, this);
        mrecyclerView.setAdapter(reminder_adapter);

        if(reminderNoteList.size()!=0){
            reminderTextView.setVisibility(View.INVISIBLE);
            reminderImageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.START);
        }else {
            reminderTextView.setVisibility(View.VISIBLE);
            reminderImageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void gettingReminderFailure(String message) {

    }

    @Override
    public void implementFragment() {

    }

    @Override
    public void onSearchTagChange(String searchTag) {
        searchTag = searchTag.toLowerCase();
        if (!TextUtils.isEmpty(searchTag)) {
            ArrayList<NotesModel> newList = new ArrayList<>();
            for (NotesModel model : allNotes) {

                String name = model.getTitle().toLowerCase();
                if (name.contains(searchTag))
                    newList.add(model);
            }
            filteredNotes.clear();
            filteredNotes.addAll(newList);
        } else {
            filteredNotes.clear();
            filteredNotes.addAll(allNotes);
        }
        reminder_adapter.notifyDataSetChanged();
    }
}

