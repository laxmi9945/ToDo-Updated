package com.app.todo.todoMain.ui.fragment;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import static com.facebook.FacebookSdk.getApplicationContext;


public class NotesFragment extends Fragment implements NotesFragmentInterface, OnSearchTextChange, View.OnClickListener {

    public static final String TAG = "NotesFragment";
    private static final int REQUEST_CODE = 2;
    @BindView(R.id.recyclerViewNotes)
    RecyclerView recyclerViewNotes;
    @BindView(R.id.fabAddNotes)
    FloatingActionButton fabAddNotes;
    @BindView(R.id. list_empty_imageView)
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
    DataBaseUtility dataBaseUtility;
    Snackbar snackbar;
    View view;
    SharedPreferences sharedPreferences;
    boolean isGrid = false;



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

        view.findViewById(R.id.coordinatorRootNotesFragment).setOnDragListener(new MyDragListener());

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isList", false)) {
            isGrid = false;

            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = true;
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
            if (!note.isArchieved() && !note.isDeleted()) {
                allNotes.add(note);
            }
        }
        filteredNotes.addAll(allNotes);
        Log.i(TAG, "getNotesListSuccess: " + allNotes.size());
        recyclerAdapter.notifyDataSetChanged();
        if (filteredNotes.size() != 0) {
            empty_list_textView.setVisibility(View.INVISIBLE);
            empty_list_imageView.setVisibility(View.INVISIBLE);
            //coordinatorRootNotesFragment.setGravity(Gravity.START);
        } else {
            empty_list_textView.setVisibility(View.VISIBLE);
            empty_list_imageView.setVisibility(View.VISIBLE);
            //coordinatorRootNotesFragment.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void getNotesListFailure(String message) {

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSearchTagChange(String searchTag) {
        Log.i(TAG, "onSearchTagChange: " + searchTag);
        searchTag = searchTag.toLowerCase();

        if (!TextUtils.isEmpty(searchTag)) {
            ;

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
                    notesModel.setDeleted(true);
                    databaseReference.child(Constants.userdata).child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).setValue(notesModel);
                    recyclerAdapter.deleteItem(position);
                    dataBaseUtility.delete(notesModel);
                    recyclerViewNotes.setAdapter(recyclerAdapter);
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

    void toggle(MenuItem item) {

        if (!isGrid) {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_straggered);
            isGrid = true;
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isList", true);
            edit.commit();

        } else {
            recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.drawable.ic_action_list);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isList", false);
            edit.commit();
            isGrid = false;
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

    private final class MyClickListener implements View.OnLongClickListener {

        // called when the item is long-clicked
        @Override
        public boolean onLongClick(View view) {
            // TODO Auto-generated method stub

            // create it from the object's tag
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            String[] mimeTypes = {
                    ClipDescription.MIMETYPE_TEXT_PLAIN
            };
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );


            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable normalShape = getResources().getDrawable(R.drawable.normal_shape);
        //Drawable targetShape = getResources().getDrawable(R.drawable.target_shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {

            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(normalShape);    //change the shape of the view
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);    //change the shape of the view back to normal
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    // if the view is the bottomlinear, we accept the drag item
                    if (v == view.findViewById(R.id.coordinatorRootNotesFragment)) {
                        View view = (View) event.getLocalState();
                        ViewGroup viewgroup = (ViewGroup) view.getParent();
                        viewgroup.removeView(view);

                        //change the text
                        TextView text = (TextView) v.findViewById(R.id.text);
                        text.setText("The item is dropped");

                        LinearLayout containView = (LinearLayout) v;
                        containView.addView(view);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        Context context = getApplicationContext();
                        Toast.makeText(context, "You can't drop the image here",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);    //go back to normal shape

                default:
                    break;
            }
            return true;
        }
    }
}
