package com.app.todo.todoMain.ui.activity;


import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.fragment.AboutFragment;
import com.app.todo.todoMain.ui.fragment.ArchiveFragment;
import com.app.todo.todoMain.ui.fragment.NotesFragment;
import com.app.todo.todoMain.ui.fragment.OnSearchTextChange;
import com.app.todo.todoMain.ui.fragment.ReminderFragment;
import com.app.todo.todoMain.ui.fragment.TrashFragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class TodoMainActivity extends BaseActivity implements TodoMainActivityInterface, TextToSpeech.OnInitListener, SearchView.OnQueryTextListener, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    static final String TAG = "NetworkStateReceiver";
    private final int PICK_IMAGE_CAMERA = 100, PICK_IMAGE_GALLERY = 200, CROP_IMAGE = 1;
    RecyclerView recyclerView;
    TextToSpeech textToSpeech;
    boolean isList = false;
    RecyclerAdapter recyclerAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CardView cardView;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    Menu menu;
    DataBaseUtility dataBaseUtility;
    AppCompatTextView titleTextView;
    AppCompatTextView dateTextview;
    AppCompatTextView contentTextview;
    AppCompatTextView nav_header_Name;
    AppCompatTextView nav_header_Email;
    CircleImageView circleImageView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<NotesModel> allNotes;
    FloatingActionButton floatingActionButton;
    ProgressDialog progressDialog;
    String fb_first_name;
    String fb_last_name;
    String fb_email;
    String imageUrl;
    String google_first_name;
    String google_email;
    String google_imageUrl;
    String uId;
    GoogleSignInOptions googleSignInOptions;
    GoogleApiClient googleApiClient;
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent;
    OnSearchTextChange searchTagListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawerlayout);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                .replace(R.id.frameLayout_container, new NotesFragment(), NotesFragment.TAG)
                .commit();

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("isList", false)) {
            isList = false;
        } else {
            isList = true;
        }

        //Getting reference to Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.userData));
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();

        initView();

        if (sharedPreferences.getBoolean(Constants.key_fb_login, false)) {
            isFbLogin();
        } else if (sharedPreferences.getBoolean(Constants.key_google_login, false)) {
            isGoogleLogin();

        } else {
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            String previouslyEncodedImage = shre.getString("image_data", "");

            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                circleImageView.setImageBitmap(bitmap);
            }
            String uName = firebaseAuth.getCurrentUser().getDisplayName();
            String uEmail = firebaseAuth.getCurrentUser().getEmail();
            nav_header_Name.setText(uName);
            nav_header_Email.setText(uEmail);
            circleImageView.setOnClickListener(this);
        }
        setTitle(getString(R.string.notes));
        setSupportActionBar(toolbar);
        dataBaseUtility = new DataBaseUtility(this);
        toolbar.setVisibility(View.VISIBLE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        floatingActionButton.setVisibility(View.VISIBLE);

    }

    boolean isFbLogin() {

        //Getting data from SharedPreference
        fb_first_name = sharedPreferences.getString(Constants.fb_name_key, "");
        fb_last_name = sharedPreferences.getString(Constants.fb_lastname_key, "");
        fb_email = sharedPreferences.getString(Constants.fb_email_key, "");
        imageUrl = sharedPreferences.getString(Constants.fb_profile_key, "");
        nav_header_Name.setText(fb_first_name + " " + fb_last_name);
        nav_header_Email.setText(fb_email);
        Glide.with(getApplicationContext()).load(imageUrl).into(circleImageView);
        return false;

    }

    boolean isGoogleLogin() {

        //Getting data from SharedPreference
        google_first_name = sharedPreferences.getString(Constants.Name, "");
        google_email = sharedPreferences.getString(Constants.Email, "");
        google_imageUrl = sharedPreferences.getString(Constants.profile_pic, "");
        nav_header_Name.setText(google_first_name);
        nav_header_Email.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleImageView);
        return false;
    }

    @Override
    public void initView() {

        View view = getLayoutInflater().inflate(R.layout.activity_todonotes_cards, null, false);
        textToSpeech = new TextToSpeech(this, this);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_notes_action, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();


        View searchBar = searchView.findViewById(R.id.search_bar);
        if (searchBar != null && searchBar instanceof LinearLayout) {
            ((LinearLayout) searchBar).setLayoutTransition(new LayoutTransition());
        }

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeview) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.profile_image:

                profilePictureSet();

                break;

        }
    }

    void profilePictureSet() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.choose_option));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getString(R.string.take_photo))) {
                            dialog.dismiss();
                            ClickImageFromCamera();
                        } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                            dialog.dismiss();
                            GetImageFromGallery();
                        } else if (options[item].equals(getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void ClickImageFromCamera() {

        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);

        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        CamIntent.putExtra("return-data", true);

        startActivityForResult(CamIntent, PICK_IMAGE_CAMERA);

    }

    public void GetImageFromGallery() {

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, getString(R.string.select_img_from_gallery)), PICK_IMAGE_GALLERY);

    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String titleData = "";
        String contentData = "";
        String recentTimeData = "";

        Log.i(TAG, "onActivityResult: " + requestCode + "  " + resultCode);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE_GALLERY) {
                uri = data.getData();
                ImageCropFunction();
            } else if (requestCode == PICK_IMAGE_CAMERA) {
                uri = data.getData();
                ImageCropFunction();
            } else if (requestCode == CROP_IMAGE) {
                if (data != null) {

                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    //  textEncode.setText(encodedImage);

                    SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = shre.edit();
                    edit.putString("image_data", encodedImage);
                    edit.commit();
                    Log.i(TAG, "pic: " + bitmap);

                    circleImageView.setImageBitmap(bitmap);
                } else {
                    Bundle bundle = data.getBundleExtra("bundle");
                    if (bundle != null) {
                        titleData = bundle.getString(Constants.title_data);
                        contentData = bundle.getString(Constants.content_data);
                        recentTimeData = bundle.getString(Constants.date_data);
                    }
                    NotesModel note = new NotesModel();
                    note.setTitle(titleData);
                    note.setContent(contentData);
                    note.setDate(recentTimeData);
                    recyclerAdapter.addNotes(note);
                    recyclerAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerAdapter);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notes:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new NotesFragment(), NotesFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                recyclerView.setAdapter(recyclerAdapter);
                setTitle(getString(R.string.notes));
                Toast.makeText(this, getString(R.string.notes), LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.logout:

                deleteAccessToken();
                break;

            case R.id.reminder:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new ReminderFragment(this), ReminderFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                setTitle(getString(R.string.reminder));
                Toast.makeText(this, getString(R.string.reminder), Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.trash:

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new TrashFragment(this), TrashFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                setTitle(getString(R.string.trash));
                Toast.makeText(this, getString(R.string.trash), Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();

                break;

            case R.id.archive:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new ArchiveFragment(this), ArchiveFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                setTitle(getString(R.string.archive));
                Toast.makeText(this, getString(R.string.archive), Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();

                break;
            case R.id.about:

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_left)
                        .replace(R.id.frameLayout_container, new AboutFragment(), AboutFragment.TAG)
                        .addToBackStack(null)
                        .commit();
                setTitle(getString(R.string.about));

                Toast.makeText(this, getString(R.string.about), Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.nav_share:
                Toast.makeText(this, getString(R.string.logic), Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    void deleteAccessToken() {

        LoginManager.getInstance().logOut();//fb logout

        firebaseAuth.signOut();//Firebase logout

        //google logout
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(TodoMainActivity.this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.key_fb_login, false);
        editor.putBoolean(Constants.key_google_login, false);
        editor.putBoolean(Constants.key_firebase_login, false);
        editor.apply();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    public void setData(NotesModel notesModel) {
        recyclerAdapter.addNotes(notesModel);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            getFragmentManager().popBackStack();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchTagListener.onSearchTagChange(newText.trim());

        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);


            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "This Language is not supported");
            } else {

                //speakOut();
            }

        } else {
            Log.e("TextToSpeech", "Initilization Failed!");
        }
    }

    private void speakOut() {

        String userName = "Welcome back " + fb_first_name;
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
        textToSpeech.speak(userName, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onDestroy() {

        //Stopping textToSpeech
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(this);
        if (!isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void getNotesListFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getNotesListSuccess(List<NotesModel> modelList) {
        allNotes = modelList;
        ArrayList<NotesModel> todoHomeDataModel = new ArrayList<>();
        for (NotesModel note : allNotes) {
            if (!note.isArchieved()) {
                todoHomeDataModel.add(note);
            }
        }
        recyclerAdapter = new RecyclerAdapter(TodoMainActivity.this, todoHomeDataModel, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void setSearchTagListener(OnSearchTextChange searchTagListener) {
        this.searchTagListener = searchTagListener;
    }
}