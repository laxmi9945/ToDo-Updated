package com.app.todo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    SharedPreferences.Editor editor = null;
    AppCompatEditText editTextEmail, editTextPassword;
    AppCompatButton login_Button,googleButton;
    AppCompatTextView createAccountTextview, forgotTextview;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    LoginButton loginButton;
    SharedPreferences sharedPreferences;
    SignInButton signInButton;
    GoogleSignInOptions googleSignInOptions;
    GoogleApiClient googleApiClient;
    int RC_SIGN_IN = 100; //to check the activity result
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("test", "onCreate: test");

        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager=CallbackManager.Factory.create();

        facebookSDKInitialize();
        initView();

        //initializing google signin options
        googleSignInOptions=new GoogleSignInOptions
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
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (isNetworkConnected()) {

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_internet))
                    .setMessage(getString(R.string.network_error_msg))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

        loginButton.setReadPermissions("public_profile email");

    }
    private void facebookSDKInitialize() {

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
        loginButton= (LoginButton) findViewById(R.id.fb_login_button);
        //signInButton= (SignInButton) findViewById(R.id.google_signin_button);
        //fbButton = (AppCompatButton) findViewById(R.id.fb_button);
        googleButton = (AppCompatButton) findViewById(R.id.google_button);
        facebookLogin();
        setClicklistener();
    }

    @Override
    public void setClicklistener() {
        forgotTextview.setOnClickListener(this);
        createAccountTextview.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        login_Button.setOnClickListener(this);
        //signInButton.setOnClickListener(this);
        //fbButton.setOnClickListener(this);
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

                loginUser();


                break;
            case R.id.fb_login_button:

                break;

           /* case R.id.google_signin_button:

                break;*/
            case R.id.google_button:
                signIn();
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private Bundle getFacebookData(JSONObject object) {

        try {

            Bundle bundle = new Bundle();

            String id = object.getString("id");

            try {

                //URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                URL profile_pic = new URL("http://graph.facebook.com/" + id + "/picture?type=square");

                Log.i("profile_pic", profile_pic + "");

                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {

                e.printStackTrace();

                return null;

            }

            bundle.putString("idFacebook", id);

            if (object.has("first_name"))

                bundle.putString("first_name", object.getString("first_name"));

            if (object.has("last_name"))

                bundle.putString("last_name", object.getString("last_name"));

            if (object.has("email"))

                bundle.putString("email", object.getString("email"));

            if (object.has("gender"))

                bundle.putString("gender", object.getString("gender"));

            if (object.has("birthday"))

                bundle.putString("birthday", object.getString("birthday"));

            if (object.has("location"))

                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;

        }

        catch(JSONException e) {

           // Log.d(TAG,"Error parsing JSON");

            return null;

        }

    }

    //Facebook Social Login

    public  void facebookLogin(){

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override

            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

                System.out.println("onSuccess");

                String accessToken = loginResult.getAccessToken().getToken();

                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override

                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("LoginActivity", response.toString());

                        // Get facebook data from login

                        Bundle bFacebookData = getFacebookData(object);

                        String emailid=bFacebookData.getString("email");
                        sharedPreferences=getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
                        editor=sharedPreferences.edit();
                        editor.putString("email",emailid);
                        editor.putString("profile", bFacebookData.getString("profile_pic"));
                        editor.putString("firstname", bFacebookData.getString("first_name"));
                        editor.putString("lastname", bFacebookData.getString("last_name"));
                        editor.commit();
                       // Log.i(TAG, "onCompleted: "+emai);
                        Toast.makeText(LoginActivity.this, "id :"+bFacebookData.getString("first_name"), Toast.LENGTH_SHORT).show();

                    }

                });

                Bundle parameters = new Bundle();

                parameters.putString("fields", "id, first_name, last_name, email"); // Parámetros que pedimos a facebook

                request.setParameters(parameters);

                request.executeAsync();

            }

            @Override

            public void onCancel() {

                Toast.makeText(LoginActivity.this, "Login attempt canceled", Toast.LENGTH_SHORT).show();

            }

            @Override

            public void onError(FacebookException e) {

                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

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
                            startActivity(new Intent(getApplicationContext(),TodoNotesActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }

    public void loginUser() {
        String Email = editTextEmail.getText().toString();
        String Password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(Email)) {
            editTextEmail.setError(getString(R.string.field_msg));
            return;
        } else if (TextUtils.isEmpty(Password)) {
            editTextPassword.setError(getString(R.string.field_msg));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), TodoNotesActivity.class));
                    /*Intent intent = new Intent(getApplicationContext(), TodoNotesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);*/
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        progressDialog.setMessage(getString(R.string.login_msg));
        progressDialog.show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(getApplicationContext(),TodoNotesActivity.class));
                            finish();
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });

    }



}

