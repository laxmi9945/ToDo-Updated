package com.app.todo.todoMain.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
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
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NotesFragment extends Fragment implements NotesFragmentInterface, OnSearchTextChange,
        View.OnClickListener, viewFragment {

    public static final String TAG = "NotesFragment";
    private static final int REQUEST_CODE = 2;
    @BindView(R.id.recyclerViewNotes)
    RecyclerView recyclerViewNotes;
    @BindView(R.id.fabAddNotes)
    FloatingActionButton fabAddNotes;
    @BindView(R.id.list_empty_imageView)
    AppCompatImageView empty_list_imageView;
    @BindView(R.id.list_empty_textView)
    AppCompatTextView empty_list_textView;
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
    ArrayList<NotesModel> notesModel2 = new ArrayList<>();
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    View view;
    SharedPreferences sharedPreferences;
    boolean isGrid = false;
    // This is a handle so that we can call methods on our service

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        // Create a new service client and bind our activity to this service
        dataBaseUtility = new DataBaseUtility(getActivity());
        presenter = new NotesFragmentPresenter(getContext(), this);
        //cardView= (CardView) view.findViewById(R.id.myCardView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getNoteList(uId);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        fabAnimate();
        initSwipeView();
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys,
                Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isList", false)) {
            isGrid = false;

            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = true;
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
        }

        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);
        recyclerViewNotes.setAdapter(recyclerAdapter);

        ((TodoMainActivity) getActivity()).setSearchTagListener(this);
        fabAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotesAddActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivityForResult(intent, 2, bundle);
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
            if (!note.isArchived() && !note.isDeleted()) {
                allNotes.add(note);
            }
        }
        filteredNotes.addAll(allNotes);
        Log.i(TAG, "getNotesListSuccess: " + allNotes.size());
        recyclerAdapter.notifyDataSetChanged();
        if (filteredNotes.size() != 0) {
            empty_list_textView.setVisibility(View.INVISIBLE);
            empty_list_imageView.setVisibility(View.INVISIBLE);

        } else {
            empty_list_textView.setVisibility(View.VISIBLE);
            empty_list_imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getNotesListFailure(String message) {

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dataBaseUtility.getDatafromDB();
        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);
        recyclerViewNotes.setAdapter(recyclerAdapter);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "hii", Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "onSearchTagChange: " + newList);

            }
            filteredNotes.clear();
            filteredNotes.addAll(newList);

        } else {
            filteredNotes.clear();
            filteredNotes.addAll(allNotes);
        }
        recyclerAdapter.notifyDataSetChanged();

    }

    void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP
                | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                recyclerAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {

                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    notesModel = allNotes.get(position);
                    notesModel.setDeleted(true);
                    databaseReference.child(Constants.userdata).child(uId)
                            .child(notesModel.getDate()).child(String.valueOf(notesModel.getId()))
                            .setValue(notesModel);
                    recyclerAdapter.deleteItem(position);
                    dataBaseUtility.delete(notesModel);
                    recyclerViewNotes.setAdapter(recyclerAdapter);
                    snackbar = Snackbar
                            .make(coordinatorRootNotesFragment, getString(R.string.item_deleted),
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
                if (direction == ItemTouchHelper.RIGHT) {
                    allNotes = getWithoutArchiveItems();
                    notesModel = allNotes.get(position);
                    notesModel.setArchived(true);
                    databaseReference.child(uId).child(notesModel.getDate()).child(String
                            .valueOf(notesModel.getId())).setValue(notesModel);
                    recyclerAdapter.archiveItem(position);
                    recyclerViewNotes.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorRootNotesFragment, getString(R.string.item_archieved),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    notesModel.setArchived(false);
                                    databaseReference.child(uId).child(notesModel.getDate())
                                            .child(String.valueOf(notesModel.getId()))
                                            .setValue(notesModel);
                                    Snackbar snackbar1 = Snackbar.make(coordinatorRootNotesFragment,
                                            getString(R.string.item_restored), Snackbar.LENGTH_SHORT);
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
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isList", true);
            isGrid = true;
            edit.apply();

        } else {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(1,
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

        }
    }

    @Override
    public void implementFragment() {

        Toast.makeText(getActivity(), "clicked...", Toast.LENGTH_SHORT).show();

    }

}
