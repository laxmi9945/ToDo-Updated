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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.TrashFragmentPresenter;
import com.app.todo.todoMain.presenter.TrashFragmentPresenterInterface;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class TrashFragment extends Fragment implements TrashFragmentInterface,View.OnLongClickListener {
    public static final String TAG = "NotesFragment";
    ProgressDialog progressDialog;
    TrashFragmentPresenterInterface presenter;
    RecyclerView trash_recyclerView;
    RecyclerAdapter trash_recyclerAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String uId;
    AppCompatTextView trash_textView;
    List<NotesModel> allNotes = new ArrayList<>();
    List<NotesModel> filteredNotes = new ArrayList<>();
    ArrayList<NotesModel> deleteNoteList = new ArrayList<>();
    AppCompatImageView trash_imageView;
    TodoMainActivity todoMainActivity;
    TrashFragmentPresenter trashFragmentPresenter;
    NotesModel notesModel = new NotesModel();
    RecyclerAdapter recyclerAdapter;
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    LinearLayout linearLayout;
    public TrashFragment(TodoMainActivity todoMainActivity) {
        this.todoMainActivity = todoMainActivity;
        presenter = new TrashFragmentPresenter(todoMainActivity, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        setHasOptionsMenu(true);
        initView(view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.userdata);
        dataBaseUtility = new DataBaseUtility(getActivity());
        getActivity().setTitle("Trash");
        return view;

    }

    private void initView(View view) {
        linearLayout= (LinearLayout) view.findViewById(R.id.root_trash_recycler);
        //databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        trash_recyclerView = (RecyclerView) view.findViewById(R.id.deleteItem_recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        trashFragmentPresenter=new TrashFragmentPresenter(getContext(),this);
        trashFragmentPresenter.getDeleteNote(uId);
        trash_textView = (AppCompatTextView) view.findViewById(R.id.trashTextView);
        trash_imageView = (AppCompatImageView) view.findViewById(R.id.trash_icon);
        trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);

        initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.RIGHT) {
                    notesModel = deleteNoteList.get(position);
                    //notesModel.setDeleted(true);
                    databaseReference.child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).setValue(null);
                    recyclerAdapter.deleteItem(position);
                    dataBaseUtility.delete(notesModel);
                    trash_recyclerView.setAdapter(recyclerAdapter);
                    snackbar = Snackbar
                            .make(linearLayout, getString(R.string.item_deleted), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(trash_recyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TodoMainActivity) getActivity()).showOrHideFab(false);
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
    public void deleteSuccess(List<NotesModel> modelList) {

        for (NotesModel notesModel : modelList) {
            if (notesModel.isDeleted()) {
                deleteNoteList.add(notesModel);
            }
        }

        trash_recyclerAdapter = new RecyclerAdapter(todoMainActivity, deleteNoteList, this);
        trash_recyclerView.setAdapter(trash_recyclerAdapter);

        if (deleteNoteList.size() != 0) {
            trash_textView.setVisibility(View.INVISIBLE);
            trash_imageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.START);
        } else {
            trash_textView.setVisibility(View.VISIBLE);
            trash_imageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void deleteFailure(String message) {

    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(todoMainActivity, "clicked...", Toast.LENGTH_SHORT).show();
        return false;
    }
}
