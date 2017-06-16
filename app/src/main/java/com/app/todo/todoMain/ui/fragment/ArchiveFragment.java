package com.app.todo.todoMain.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.app.todo.localdatabase.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.ArchiveFragmentPresenter;
import com.app.todo.todoMain.presenter.ArchiveFragmentPresenterInterface;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class ArchiveFragment extends Fragment implements ArchiveFragmentInterface,
        SearchView.OnQueryTextListener {
    public static final String TAG = "ArchiveFragment";
    ArchiveFragmentPresenterInterface presenter;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    RecyclerView archive_recyclerView;
    RecyclerAdapter archive_adapter;
    List<NotesModel> archiveItem;
    TodoMainActivity todoMainActivity;
    AppCompatTextView archive_textView;
    AppCompatImageView archive_imageView;
    NotesModel notesModel = new NotesModel();
    LinearLayout linearLayout;
    DatabaseReference databaseReference;
    String uId;
    List<NotesModel> filteredNotes = new ArrayList<>();
    List<NotesModel> allNotes = new ArrayList<>();
    boolean isView = false;
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    CardView cardView;
    public ArchiveFragment(TodoMainActivity todoMainActivity) {
        this.todoMainActivity = todoMainActivity;
        presenter = new ArchiveFragmentPresenter(todoMainActivity, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.fragment_archive, container, false);
        initView(view);
        setHasOptionsMenu(true);
        getActivity().setTitle("Archive");
        presenter.getArchiveNote(uId);
        return view;

    }

    private void initView(View view) {
        dataBaseUtility = new DataBaseUtility(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        archive_recyclerView = (RecyclerView) view.findViewById(R.id.archiveItem_recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        archive_textView = (AppCompatTextView) view.findViewById(R.id.archive_textView);
        archive_imageView = (AppCompatImageView) view.findViewById(R.id.archive_icon);
        archive_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        linearLayout = (LinearLayout) view.findViewById(R.id.root_archive_recycler);
        initSwipeView();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }

    void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

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
                if (direction == ItemTouchHelper.RIGHT) {
                    try {
                        notesModel = allNotes.get(position);
                        databaseReference.child(uId).child(notesModel.getDate())
                                .child(String.valueOf(notesModel.getId())).child("archived")
                                .setValue(false);
                        Snackbar snackbar = Snackbar
                                .make(linearLayout, getString(R.string.item_moved), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(archive_recyclerView);
    }

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());

        if (!getActivity().isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (!getActivity().isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void archiveSuccess(List<NotesModel> modelList) {

        ArrayList<NotesModel> archiveNoteList = new ArrayList<>();
        for (NotesModel notesModel : modelList) {
            if (notesModel.isArchived() && !notesModel.isDeleted()) {
                archiveNoteList.add(notesModel);
            }
        }
        allNotes.clear();
        allNotes.addAll(archiveNoteList);
        archive_adapter = new RecyclerAdapter(getActivity(), archiveNoteList);
        archive_recyclerView.setAdapter(archive_adapter);

        if (archiveNoteList.size() != 0) {
            archive_textView.setVisibility(View.INVISIBLE);
            archive_imageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.START);
        } else {
            archive_textView.setVisibility(View.VISIBLE);
            archive_imageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void archiveFailure(String message) {
        //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

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
            archive_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_straggered);
            isView = true;
        } else {
            archive_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            isView = false;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        if (!TextUtils.isEmpty(newText)) {
            ArrayList<NotesModel> newList = new ArrayList<>();
            for (NotesModel model : allNotes) {
                String name = model.getTitle().toLowerCase();
                if (name.contains(newText))
                    newList.add(model);
            }
            filteredNotes.clear();
            filteredNotes.addAll(newList);
        } else {
            filteredNotes.clear();
            filteredNotes.addAll(allNotes);
        }
        archive_adapter.notifyDataSetChanged();
        return true;
    }
}
