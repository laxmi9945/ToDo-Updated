package com.app.todo.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.login.presenter.LoginPresenter;
import com.app.todo.model.UserInfoModel;
import com.app.todo.ui.ResetPasswordActivity;
import com.app.todo.ui.TodoNotesActivity;
import com.app.todo.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity implements LoginActivityInterface {

    SharedPreferences.Editor editor = null;
    AppCompatEditText editTextEmail, editTextPassword;
    AppCompatButton login_Button, googleButton;
    AppCompatTextView createAccountTextview, forgotTextview;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    LoginButton loginButton;
    SharedPreferences sharedPreferences;
    GoogleSignInOptions googleSignInOptions;
    GoogleApiClient googleApiClient;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int RC_SIGN_IN = 100; //to check the activity result
    LoginPresenter loginPresenter;
    Snackbar snackbar;
    private LinearLayout linearLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        initView();
        checkNetwork();
        getOldUserData();
        loginButton.setReadPermissions("public_profile email");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    private void getOldUserData() {
        sharedPreferences = getSharedPreferences(Constants.keys, 0);
        editTextEmail.setText(sharedPreferences.getString("UserEmail", "").toString());
        editTextPassword.setText(sharedPreferences.getString("Password", "").toString());
    }

    private void checkNetwork() {

        if (isNetworkConnected()) {

        } else {
             snackbar = Snackbar
                    .make(linearLayout,getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            snackbar.dismiss();

                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            AppCompatTextView textView = (AppCompatTextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initView() {

        editTextEmail = (AppCompatEditText) findViewById(R.id.email_Edittext);
        editTextPassword = (AppCompatEditText) findViewById(R.id.password_Edittext);
        createAccountTextview = (AppCompatTextView) findViewById(R.id.createAccount_Textview);
        forgotTextview = (AppCompatTextView) findViewById(R.id.forgot_textview);
        login_Button = (AppCompatButton) findViewById(R.id.login_button);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        googleButton = (AppCompatButton) findViewById(R.id.google_button);
        linearLayout= (LinearLayout) findViewById(R.id.Linear_rootLayout);

        //initializing google signin options
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //initializing google api client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setClicklistener();
        loginPresenter=new LoginPresenter(this,this);

    }

    @Override
    public void setClicklistener() {

        forgotTextview.setOnClickListener(this);
        createAccountTextview.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        login_Button.setOnClickListener(this);
        googleButton.setOnClickListener(this);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    }

    // [END on_start_check_user]
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAccount_Textview:

                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;

            case R.id.forgot_textview:

                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
                break;

            case R.id.login_button:

                //loginUser();
                loginPresenter.loginResponse(editTextEmail.getText().toString(),editTextPassword.getText().toString());

                break;

            case R.id.fb_login_button:
                facebookLogin();
                break;

            case R.id.google_button:
                signIn();
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //Facebook Social Login

    private void facebookLogin() {

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override

            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
                //String accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override

                    public void onCompleted(JSONObject object, GraphResponse response) {

                        // Get facebook data from login

                        Bundle bFacebookData = getFacebookData(object);

                        String emailid = bFacebookData.getString(Constants.fb_email);
                         editor.putString("email", emailid);
                        editor.putString("profile", bFacebookData.getString(Constants.fb_profile_pic));
                        editor.putString("firstname", bFacebookData.getString(Constants.fb_first_name));
                        editor.putString("lastname", bFacebookData.getString(Constants.fb_last_name));
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Welcome :" + bFacebookData.getString(Constants.fb_first_name), Toast.LENGTH_SHORT).show();

                    }

                });

                Bundle parameters = new Bundle();

                parameters.putString("fields", "id, first_name, last_name, email");

                request.setParameters(parameters);

                request.executeAsync();

            }

            @Override

            public void onCancel() {

                Toast.makeText(LoginActivity.this,getString(R.string.login_attempt_warn), Toast.LENGTH_SHORT).show();

            }

            @Override

            public void onError(FacebookException e) {

                Toast.makeText(LoginActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), TodoNotesActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }
    private Bundle getFacebookData(JSONObject object) {

        try {

            Bundle bundle = new Bundle();

            String id = object.getString("id");

            try {

                URL profile_pic = new URL("http://graph.facebook.com/" + id + "/picture?type=square");

                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {

                e.printStackTrace();

                return null;

            }

            bundle.putString("idFacebook", id);

            if (object.has(Constants.fb_first_name))

                bundle.putString(Constants.fb_first_name, object.getString(Constants.fb_first_name));

            if (object.has(Constants.fb_last_name))

                bundle.putString(Constants.fb_last_name, object.getString(Constants.fb_last_name));

            if (object.has(Constants.fb_email))

                bundle.putString(Constants.fb_email, object.getString(Constants.fb_email));

            return bundle;

        } catch (JSONException e) {

            return null;

        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            String person_name=account.getDisplayName();
            String person_email=account.getEmail();
            Uri profile_pic=account.getPhotoUrl();
            String person_id=account.getId();
            sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("uName",person_name);
            editor.putString("uEmail",person_email);
            editor.putString("uId",person_id);
            editor.putString("uPic",profile_pic.toString());
            editor.apply();
            //Toast.makeText(this, "info"+person_name+person_email+person_id+profile_pic, Toast.LENGTH_SHORT).show();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                startActivity(new Intent(getApplicationContext(), TodoNotesActivity.class));
                                finish();
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }


                        }
                    });
            //firebaseAuthWithGoogle(account);
        } else {
            // Google Sign In failed, update UI appropriately

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {


    }


    @Override
    public void loginSuccess(UserInfoModel userInfoModel, String uid) {
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String login_email=editTextEmail.getText().toString();
        String login_password=editTextPassword.getText().toString();
        editor.putString(Constants.Email,login_email);
        editor.putString(Constants.Password,login_password);
        editor.putString("uid",uid);
        editor.commit();
        startActivity(new Intent(getApplicationContext(), TodoNotesActivity.class));
        finish();
    }

    @Override
    public void loginFailure(String message) {

    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(int errorType) {
        switch (errorType){
            case Constants.ErrorType.ERROR_EMPTY_EMAIL:
                editTextEmail.setError(getString(R.string.email_field_condition));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_EMAIL:
                editTextEmail.setError(getString(R.string.invalid_email));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_EMPTY_PASSWORD:
                editTextPassword.setError(getString(R.string.password_field_condition));
                editTextPassword.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_PASSWORD:
                editTextPassword.setError(getString(R.string.invalid_password));
                editTextPassword.requestFocus();
                break;
            case Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION:
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void isFbLogin(){
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

    }
    public void isGoogleLogin(){
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

}

