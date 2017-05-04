package com.app.todo.ui;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.fragment.AboutFragment;
import com.app.todo.fragment.ArchiveFragment;
import com.app.todo.fragment.NotesFragment;
import com.app.todo.fragment.TrashFragment;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.model.NotesModel;
import com.app.todo.utils.Constants;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;


public class TodoNotesActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    boolean isView = false;
    RecyclerAdapter recyclerAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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
    String firebase_name, firebase_email;
    private static final String TAG = "NetworkStateReceiver";
    String uId;
    private static int RESULT_LOAD_IMG = 1;
    boolean isFblogin = true, isGooglelogin = true, isFireBaselogin = true;
    GoogleSignInOptions  googleSignInOptions;
    GoogleApiClient googleApiClient;
    ArrayAdapter<String> myAdapter;
    ArrayList<NotesModel> notesModel=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawerlayout);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Getting reference to Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("userData");
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        initView();

        if (isFblogin) {

            sharedPreferences.getBoolean(Constants.key_fb_login, false);
            isFbLogin();

        } else if (isGooglelogin) {

            sharedPreferences.getBoolean(Constants.key_google_login, false);
            isGoogleLogin();

        } else if (isFireBaselogin) {

            sharedPreferences.getBoolean(Constants.key_firebase_login, false);
            isFirebaseLogin();
        }

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

        floatingActionButton.setVisibility(View.VISIBLE);
        notesModels = new ArrayList<NotesModel>();
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.show();

        //retriving data from firebase
        databaseReference.child(uId).addValueEventListener(new ValueEventListener() {
            //String uId = firebaseAuth.getCurrentUser().getUid();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<NotesModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<NotesModel>>() {
                };
                ArrayList<NotesModel> notesModel=new ArrayList<>();

                for (DataSnapshot post : dataSnapshot.getChildren()) {

                    ArrayList<NotesModel> notesModel_ArrayList=null;
                    notesModel_ArrayList = post.getValue(arrayListGenericTypeIndicator);
                    notesModel.addAll(notesModel_ArrayList);
                }
                notesModel.removeAll(Collections.singleton(null));
                setDatatoRecycler(notesModel);

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

    public boolean isFirebaseLogin() {
        firebase_name=sharedPreferences.getString(Constants.Name,"");
        firebase_email=sharedPreferences.getString(Constants.Email,"");
        nav_header_Name.setText(firebase_name);
        nav_header_Email.setText(firebase_email);
        return false;
    }

    public boolean isFbLogin() {

        //Getting data from SharedPreference
        fb_first_name = sharedPreferences.getString("firstname", "");
        fb_last_name = sharedPreferences.getString("lastname", "");
        fb_email = sharedPreferences.getString("email", "");
        imageUrl = sharedPreferences.getString("profile", "");
        nav_header_Name.setText(fb_first_name + " " + fb_last_name);
        nav_header_Email.setText(fb_email);
        Glide.with(getApplicationContext()).load(imageUrl).into(circleImageView);
        return false;

    }

    public boolean isGoogleLogin() {

        //Getting data from SharedPreference
        google_first_name = sharedPreferences.getString("uName", "");
        google_email = sharedPreferences.getString("uEmail", "");
        google_imageUrl = sharedPreferences.getString("uPic", "");
        nav_header_Name.setText(google_first_name);
        nav_header_Email.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleImageView);
        return false;
    }

    private void setDatatoRecycler(List<NotesModel> notesModel) {
        if (notesModel == null) {
            notesModels = new ArrayList<>();
        } else {
            notesModels = notesModel;
        }
        recyclerAdapter = new RecyclerAdapter(TodoNotesActivity.this, notesModels);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }


    private void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {

                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    NotesModel model = notesModels.get(position);
                    databaseReference.child("userData").child(uId).child(model.getDate()).child(String.valueOf(model.getId())).removeValue();
                    dataBaseUtility.delete(model);


                } else {

                    recyclerAdapter.archiveItem(position);
                    recyclerView.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(getCurrentFocus(), "Item is archived", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(getCurrentFocus(), "Item is restored!", Snackbar.LENGTH_SHORT);
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

      // myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesModel);

        //initializing google signin options
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        setClicklistener();

    }

    @Override
    public void setClicklistener() {
        floatingActionButton.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_notes_action, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener onQueryTextListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);
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
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.mipmap.straggered_view);
            isView = true;
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            item.setIcon(R.mipmap.list_view);
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
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    //imgView.setImageURI(selectedImageUri);
                    circleImageView.setImageURI(selectedImageUri);
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fabAddNotes:

                Intent intent = new Intent(this, NotesAddActivity.class);
                startActivityForResult(intent, 2);


                break;
            case R.id.profile_image:
                profilePictureSet();


                break;

        }
    }

    private void profilePictureSet() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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

                deleteAccessToken();
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
            case R.id.trash:

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new TrashFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle("Trash");
                Toast.makeText(this, "Trash", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();

                break;

            case R.id.archive:

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new ArchiveFragment())
                        .addToBackStack(null)
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

        LoginManager.getInstance().logOut();//fb logout
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                finish();
                Toast.makeText(TodoNotesActivity.this, "logout success", Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.key_fb_login, false);
        editor.putBoolean(Constants.key_google_login, false);
        editor.putBoolean(Constants.key_firebase_login,false);
        editor.apply();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

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
                Log.i("test ", "onBackPressed: " + getSupportFragmentManager().getBackStackEntryCount());
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

   /* private String getCalendarUriBase(Activity act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }
*/
}
