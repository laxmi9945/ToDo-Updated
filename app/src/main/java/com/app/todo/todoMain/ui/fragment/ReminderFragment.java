package com.app.todo.todoMain.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    NotesModel notesModel = new NotesModel();
    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();
    List<NotesModel> allNotes = new ArrayList<>();
    List<NotesModel> filteredNotes = new ArrayList<>();
    DatabaseReference databaseReference;
    String uId;
    Snackbar snackbar;
    boolean isView = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        setHasOptionsMenu(true);
        initView(view);
        getActivity().setTitle("Reminder");
        ((TodoMainActivity)getActivity()).setSearchTagListener(this);
       // uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getReminderNotes(uId);
        return view;
    }

    private void initView(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        linearLayout= (LinearLayout) view.findViewById(R.id.reminder_rootLayout);
        mrecyclerView = (RecyclerView) view.findViewById(R.id.reminder_recyclerView);
        reminderTextView= (AppCompatTextView) view.findViewById(R.id.reminder_textView);
        reminderImageView= (AppCompatImageView) view.findViewById(R.id.reminder_event_icon);
        mrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        initSwipeView();

    }
    void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    try{
                        notesModel = allNotes.get(position);
                        databaseReference.child(uId).child(notesModel.getDate())
                                .child(String.valueOf(notesModel.getId())).child("deleted")
                                .setValue(true);
                        snackbar = Snackbar
                                .make(linearLayout, getString(R.string.item_moved_trash), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }


                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mrecyclerView);
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
        for (NotesModel notesModel: notesModelList)
        {
            if(notesModel.getReminderDate()!=null)
            {
            if (notesModel.getReminderDate().equals(currentDate) && !(notesModel.isArchived()) && !(notesModel.isDeleted())){
                reminderNoteList.add(notesModel);
            }
        }}
        allNotes.clear();
        allNotes.addAll(reminderNoteList);
        reminder_adapter= new RecyclerAdapter(todoMainActivity,reminderNoteList);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeview) {
            toggle(item);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    void toggle(MenuItem item) {
        if (!isView) {
            mrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_straggered);
            isView = true;
        } else {
            mrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            isView = false;
        }

    }
}

