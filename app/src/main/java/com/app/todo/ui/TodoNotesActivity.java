package com.app.todo.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.fragment.AboutFragment;
import com.app.todo.fragment.NotesFragment;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.model.NotesModel;
import com.app.todo.utils.Constants;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;


public class TodoNotesActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    boolean isView = false;
    RecyclerAdapter recyclerAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor = null;
    CardView cardView;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    private Menu menu;
    DataBaseUtility dataBaseUtility;
    AppCompatTextView titleTextView, dateTextview, contentTextview, nav_header_Name, nav_header_Email;
    CircleImageView circleImageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<NotesModel> notesModels;
    public FloatingActionButton floatingActionButton;
    ProgressDialog progressDialog;
    String fb_first_name, fb_last_name, fb_email, imageUrl;
    String google_first_name, google_email, google_imageUrl;
    private static final String TAG = "NetworkStateReceiver";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawerlayout);



        //Getting reference to Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("userData");
        firebaseAuth = FirebaseAuth.getInstance();
        initView();
        initSwipeView();
        setSupportActionBar(toolbar);
        dataBaseUtility = new DataBaseUtility(this);
        //notesModels = dataBaseUtility.getDatafromDB();
        toolbar.setVisibility(View.VISIBLE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        //sharedPreferences = getApplicationContext().getSharedPreferences("monk", Context.MODE_PRIVATE);

        floatingActionButton.setVisibility(View.VISIBLE);
        notesModels = new ArrayList<NotesModel>();
        progressDialog = new ProgressDialog(this);

        //Getting data from SharedPreference
        google_first_name = sharedPreferences.getString("uName", "value");
        google_email = sharedPreferences.getString("uEmail", "value");
        google_imageUrl = sharedPreferences.getString("uPic", "value");
        //Toast.makeText(this, "ggl"+google_first_name+google_email+google_imageUrl, Toast.LENGTH_SHORT).show();
        Log.i("test", "onCreate: " + google_first_name + google_email + google_imageUrl);
        nav_header_Name.setText(google_first_name);
        nav_header_Email.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleImageView);

        //Getting data from SharedPreference
        fb_first_name = sharedPreferences.getString("firstname", "value");
        fb_last_name = sharedPreferences.getString("lastname", "value");
        fb_email = sharedPreferences.getString("email", "value");
        imageUrl = sharedPreferences.getString("profile", "value");
        nav_header_Name.setText(fb_first_name + " " + fb_last_name);
        nav_header_Email.setText(fb_email);
        Glide.with(getApplicationContext()).load(imageUrl).into(circleImageView);

        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.show();
        //retriving data from firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            String uId = firebaseAuth.getCurrentUser().getUid();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<NotesModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<NotesModel>>() {
                };
                ArrayList<NotesModel> notesModelArrayList;
                for (DataSnapshot post : dataSnapshot.child(uId).getChildren()) {
                    notesModelArrayList = post.getValue(arrayListGenericTypeIndicator);
                    notesModels.addAll(notesModelArrayList);
                }

                recyclerAdapter = new RecyclerAdapter(TodoNotesActivity.this, notesModels);

                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(context, getString(R.string.fetching_error) , Toast.LENGTH_SHORT).show();
            }
        });

        //Animating Fab button while Scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }
                //super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }


        });

    }

    private void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    //final String uId = firebaseAuth.getCurrentUser().getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    Query query = databaseReference.orderByChild("userdata").equalTo("userData");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            /*for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                            }*/
                            dataSnapshot.child("").getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(TodoNotesActivity.this, "Cancelled..", Toast.LENGTH_SHORT).show();
                            //Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                    dataBaseUtility.delete(notesModels.get(position));
                    recyclerAdapter.deleteItem(position);


                } else {

                    recyclerAdapter.archiveItem(position);
                    recyclerView.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(getCurrentFocus(), "Message is deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(getCurrentFocus(), "Message is restored!", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });

                    snackbar.show();
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    @Override
    public void initView() {

        View view = getLayoutInflater().inflate(R.layout.activity_todonotes, null, false);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabAddNotes);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        titleTextView = (AppCompatTextView) view.findViewById(R.id.title_TextView);
        dateTextview = (AppCompatTextView) view.findViewById(R.id.date_TextView);
        contentTextview = (AppCompatTextView) view.findViewById(R.id.content_TextView);
        cardView = (CardView) view.findViewById(R.id.myCardView);
        nav_header_Name = (AppCompatTextView) header.findViewById(R.id.nav_header_appName);
        nav_header_Email = (AppCompatTextView) header.findViewById(R.id.nav_header_emailId);
        circleImageView = (CircleImageView) header.findViewById(R.id.profile_image);
        /*nav_header_Name2 = (AppCompatTextView) header.findViewById(R.id.nav_header_appName);
        nav_header_Email2 = (AppCompatTextView) header.findViewById(R.id.nav_header_emailId);
        circleImageView2 = (CircleImageView) header.findViewById(R.id.profile_image);*/
        setClicklistener();
    }

    @Override
    public void setClicklistener() {
        floatingActionButton.setOnClickListener(this);
        //circleImageView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_notes_action, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeview) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.changeview);
        if (!isView) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            item.setIcon(R.drawable.gridbutton);
            isView = true;
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            item.setIcon(R.drawable.listbutton);
            isView = false;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String titleData = "";
        String contentData = "";
        String recentTimeData = "";

        if (resultCode == RESULT_OK) {

            Bundle bundle = data.getBundleExtra("bundle");
            if (bundle != null) {
                Log.i("g", "onActivityResult: ");
                titleData = bundle.getString(Constants.title_data);
                contentData = bundle.getString(Constants.content_data);
                recentTimeData = bundle.getString(Constants.date_data);
            }
            NotesModel note = new NotesModel();
            note.setTitle(titleData);
            note.setContent(contentData);
            note.setDate(recentTimeData);
            Log.i("abc", "onActivityResult: ");
            recyclerAdapter.addNotes(note);
            recyclerAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(recyclerAdapter);

        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fabAddNotes:

                Intent intent = new Intent(this, NotesAddActivity.class);
                startActivityForResult(intent, 2);


                break;
            case R.id.profile_image:
                //profilePictureSet();


                break;

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notes:

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new NotesFragment(), NotesFragment.TAG).
                        addToBackStack(null)
                        .commit();
                setTitle("Notes");
                Toast.makeText(this, "Notes", LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.logout:
                //LoginManager.getInstance().logOut();//fb logout
                /*firebaseAuth.signOut();
                FirebaseAuth.getInstance().signOut();*///Gmail signout
                deleteAccessToken();
                /*finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));*/

                /*SharedPreferences.Editor shEditor = sharedPreferences.edit();
                if (sharedPreferences.contains("login")) {

                    Log.i("check", "onOptionsItemSelected: ");
                    shEditor.putString("login", "false");
                    shEditor.commit();
                    Intent intent = new Intent(TodoNotesActivity.this, LoginActivity.class);
                    Toast.makeText(TodoNotesActivity.this, getString(R.string.logout), LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();

                }*/
                //Toast.makeText(TodoNotesActivity.this, getString(R.string.logout_success), LENGTH_SHORT).show();
                break;
            case R.id.reminder:

                //Toast.makeText(this, getString(R.string.logic), LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new ReminderFragment()).
                        addToBackStack(null)
                        .commit();
                setTitle("Reminder");

                Toast.makeText(this, "Reminder", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();

                break;

            case R.id.archive:
                //Toast.makeText(this, getString(R.string.logic), LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new ArchiveFragment()).
                        addToBackStack(null)
                        .commit();
                setTitle("Archive");

                Toast.makeText(this, "Archive", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();

                break;
            case R.id.about:

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new AboutFragment()).
                        addToBackStack(null)
                        .commit();
                setTitle("About");

                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

        }

        return true;
    }

    private void deleteAccessToken() {
        //if(firebaseAuth.getCurrentUser()!=null) {
        LoginManager.getInstance().logOut();//fb logout
        firebaseAuth.signOut();
        FirebaseAuth.getInstance().signOut();//google signout
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        //}
    }

    public void updateNote(NotesModel notesModel) {
        recyclerAdapter.addNotes(notesModel);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void setData(NotesModel notesModel) {
        recyclerAdapter.addNotes(notesModel);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                Log.i("asdad ", "onBackPressed: " + getSupportFragmentManager().getBackStackEntryCount());
                if (getSupportFragmentManager().findFragmentByTag(NotesFragment.TAG) instanceof NotesFragment) {
                    finish();
                } else {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                            .replace(R.id.frameLayout_container, new NotesFragment(), NotesFragment.TAG).
                            addToBackStack(null)
                            .commit();
                    setTitle("Notes");
                }
            }
        }
    }

    public void showOrHideFab(boolean show) {
        if (show) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
        setTitle(title);
    }

}
