package com.app.todo.todoMain.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.app.todo.database.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.NotesFragmentPresenter;
import com.app.todo.todoMain.ui.activity.NotesAddActivity;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NotesFragment extends Fragment implements NotesFragmentInterface, OnSearchTextChange {

    public static final String TAG = "NotesFragment";
    TodoMainActivity todoMainActivity;

    @BindView(R.id.recyclerViewNotes)
    RecyclerView recyclerViewNotes;

    @BindView(R.id.fabAddNotes)
    FloatingActionButton fabAddNotes;

    @BindView(R.id.coordinatorRootNotesFragment)
    CoordinatorLayout coordinatorRootNotesFragment;


    RecyclerAdapter recyclerAdapter;
    List<NotesModel> allNotes = new ArrayList<>();
    List<NotesModel> filteredNotes = new ArrayList<>();
    NotesFragmentPresenter presenter;
    String uId;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    NotesModel notesModel = new NotesModel();
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBaseUtility = new DataBaseUtility(getActivity());
        presenter = new NotesFragmentPresenter(getContext(), this);
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getNoteList(uId);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        fabAnimate();
        initSwipeView();
        recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes);
        recyclerViewNotes.setAdapter(recyclerAdapter);

        ((TodoMainActivity) getActivity()).setSearchTagListener(this);
        fabAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotesAddActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        return view;
    }

    private void fabAnimate() {
        //Animating Fab button while Scrolling
        recyclerViewNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fabAddNotes.isShown()) {
                    fabAddNotes.hide();
                }
                //super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAddNotes.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }


        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((TodoMainActivity) getActivity()).setToolbarTitle("Notes");
        Log.i(TAG, "onResume:Notes  ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.coordinatorRootNotesFragment})

    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.fabAddNotes:
               /* Intent intent = new Intent(getActivity(), NotesAddActivity.class);
                startActivityForResult(intent, 2);*/
                break;
            case R.id.coordinatorRootNotesFragment:
                break;
        }
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
            if (!note.isArchieved()) {
                allNotes.add(note);
            }
        }
        filteredNotes.addAll(allNotes);
        Log.i(TAG, "getNotesListSuccess: " + allNotes.size());
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void getNotesListFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabAddNotes:
                Intent intent = new Intent(getActivity(), NotesAddActivity.class);
                startActivityForResult(intent, 2);
                break;
        }
    }

    public void onTextChange(String searchKey) {
        Log.i(TAG, "onTextChange: " + searchKey + filteredNotes.size());
        searchKey = searchKey.toLowerCase();
        ArrayList<NotesModel> newList = new ArrayList<>();
        for (NotesModel model : filteredNotes) {

            String name = model.getTitle().toLowerCase();

            Log.i(TAG, "onTextChange: " + name);

            if (name.contains(searchKey))
                newList.add(model);
        }
        Log.i(TAG, "onTextChange: " + newList.size()
        );
    }

    @Override
    public void onSearchTagChange(String searchTag) {
        Log.i(TAG, "onSearchTagChange: " + searchTag);
        searchTag = searchTag.toLowerCase();

        if (!TextUtils.isEmpty(searchTag)) {

            ArrayList<NotesModel> newList = new ArrayList<>();
            for (NotesModel model : allNotes) {

                String name = model.getTitle().toLowerCase();

                Log.i(TAG, "onTextChange: " + name);

                if (name.contains(searchTag))
                    newList.add(model);
            }
            Log.i(TAG, "onTextChange: " + newList.size());

            filteredNotes.clear();
            filteredNotes.addAll(newList);
        } else {
            filteredNotes.clear();
            filteredNotes.addAll(allNotes);
        }
        recyclerAdapter.notifyDataSetChanged();

    }

    void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {

                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    notesModel = allNotes.get(position);
                    databaseReference.child(Constants.userdata).child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).removeValue();
                    dataBaseUtility.delete(notesModel);
                    /*recyclerAdapter.deleteItem(position);*/
                    /*recyclerViewNotes.setAdapter(recyclerAdapter);*/
                    snackbar = Snackbar
                            .make(coordinatorRootNotesFragment, getString(R.string.item_deleted), Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
                if (direction == ItemTouchHelper.RIGHT) {
                    allNotes = getWithoutArchiveItems();
                    notesModel = allNotes.get(position);
                    notesModel.setArchieved(true);
                    databaseReference.child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).setValue(notesModel);
                    recyclerAdapter.archiveItem(position);
                    recyclerViewNotes.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorRootNotesFragment, getString(R.string.item_archieved), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    notesModel.setArchieved(false);
                                    databaseReference.child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).setValue(notesModel);
                                    Snackbar snackbar1 = Snackbar.make(coordinatorRootNotesFragment, getString(R.string.item_restored), Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);

    }

    private List<NotesModel> getWithoutArchiveItems() {
        ArrayList<NotesModel> todoHomeDataModel = new ArrayList<>();
        for (NotesModel note : allNotes) {
            if (!note.isArchieved()) {
                todoHomeDataModel.add(note);
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
        int id = item.getItemId();
        if (id == R.id.changeview) {
            toggle(item);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isView = false;

    void toggle(MenuItem item) {

        if (!isView)
        {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_straggered);
            isView = true;
        } else {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            isView = false;
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
    }


}
