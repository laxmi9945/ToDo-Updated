package com.app.todo.registration.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.registration.presenter.RegistrationPresenter;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;


public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityInterface{
    static final String TAG = "checker";
    private AppCompatEditText edittextName;
    private AppCompatEditText edittextemail;
    private AppCompatEditText edittextpswrd;
    private AppCompatEditText edittextmobNo;
    private AppCompatButton buttonSave;
    private AppCompatTextView textView;
    CircleImageView profile_imageView;
    private Pattern pattern;
    private Pattern pattern2;
    private Matcher matcher;
    private Matcher matcher2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private UserInfoModel userInfoModel;
    private RegistrationPresenter registrationPresenter;
    private String Name;
    private String Email;
    private String Password;
    private String MobileNo;
    Intent CamIntent, GalIntent, CropIntent;
    private final int PICK_IMAGE_CAMERA = 100, PICK_IMAGE_GALLERY = 200, CROP_IMAGE = 1;
    Uri uri;
    Uri downloadUrl2;
    private StorageReference storageReference;
    File file;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        registrationPresenter=new RegistrationPresenter(this,this);
        progressDialog = new ProgressDialog(this);
        initView();
    }

    private void initView() {
        profile_imageView= (CircleImageView) findViewById(R.id.user_pic);
        buttonSave = (AppCompatButton) findViewById(R.id.save_button);
        edittextName = (AppCompatEditText) findViewById(R.id.name_edittext);
        edittextemail = (AppCompatEditText) findViewById(R.id.email_Edittext);
        edittextpswrd = (AppCompatEditText) findViewById(R.id.password_edittext);
        edittextmobNo = (AppCompatEditText) findViewById(R.id.mobilenumber_edittext);
        textView = (AppCompatTextView) findViewById(R.id.allreadyacc_textview);
        storageReference = FirebaseStorage.getInstance().getReference().child("images");
        setClicklistener();

    }

    private void setClicklistener() {
        buttonSave.setOnClickListener(this);
        textView.setOnClickListener(this);
        profile_imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:

                if(registerUser()) {
                    userInfoModel = new UserInfoModel();
                    userInfoModel.setName(Name);
                    userInfoModel.setEmail(Email);
                    userInfoModel.setPassword(Password);
                    userInfoModel.setMobile(MobileNo);
                    userInfoModel.setProfile_pic(String.valueOf(profile_imageView));
                    registrationPresenter.registrationResponse(userInfoModel);
                }

                break;

            case R.id.allreadyacc_textview:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.user_pic:
                addProfilePic();
                break;
        }
    }

    /*private void addProfilePic() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new
                        android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/
    void addProfilePic() {
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
    private boolean registerUser() {
        boolean checkName = false, checkMail = false, checkPassword = false, checkMobNo = false;
        pattern = Pattern.compile(Constants.Password_Pattern);
        matcher = pattern.matcher(edittextpswrd.getText().toString());
        pattern2 = Pattern.compile(Constants.Mobile_Pattern);
        matcher2 = pattern.matcher(edittextmobNo.getText().toString());
        Name = edittextName.getText().toString();
        Email = edittextemail.getText().toString();
        Password = edittextpswrd.getText().toString();
        //Log.i("", "Save: "+Password);
        MobileNo = edittextmobNo.getText().toString();


        if (Name.isEmpty()) {
            edittextName.setError(getString(R.string.first_name_field));
            edittextpswrd.requestFocus();

        } else {
            checkName = true;
        }

        if (Email.isEmpty()) {
            edittextemail.setError(getString(R.string.email_field_condition));
            edittextpswrd.requestFocus();

        } else if (!isValidEmail(Email)) {
            edittextemail.setError(getString(R.string.invalid_email));
            edittextpswrd.requestFocus();

        } else {
            checkMail = true;
        }

        if (Password.isEmpty()) {
            edittextpswrd.setError(getString(R.string.password_field_condition));
            edittextpswrd.requestFocus();

        } else if (matcher.matches()) {
            checkPassword = true;
        } else {
            edittextpswrd.setError(getString(R.string.password_hint));
            edittextpswrd.requestFocus();

        }


        if (MobileNo.isEmpty()) {
            edittextmobNo.setError(getString(R.string.enter_mobile));
            edittextpswrd.requestFocus();

        } else {
            checkMobNo = true;
        }
        return checkName && checkMail && checkMobNo && checkPassword;
    }

    private static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void registrationSuccess(UserInfoModel userInfoModel, String uid) {

        Intent intent = new Intent(RegistrationActivity.this, TodoMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void registrationFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(String message) {
        if(!isFinishing()&& progressDialog!=null){
            progressDialog.setMessage(message);
           // progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
            if (!isFinishing()){
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
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

        startActivityForResult(Intent.createChooser(GalIntent,
                getString(R.string.select_img_from_gallery)),
                PICK_IMAGE_GALLERY);

    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {

            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 200);
            CropIntent.putExtra("outputY", 200);
            CropIntent.putExtra("aspectX", 4);
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
        Log.i(TAG, "onActivityResult: " + requestCode + "  " + resultCode);

        if (resultCode == RESULT_OK ) {

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
                    edit.apply();
                    Log.i(TAG, "pic: " + bitmap);
                    uploadImage();
                    profile_imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void uploadImage() {
        if (uri != null) {
            StorageReference riversRef = storageReference.child("images/profile.jpg");
            riversRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.i(TAG, "onSuccess: "+downloadUrl);

                            /*Glide.with(TodoMainActivity.this)
                                    .load(downloadUrl)
//                                    .placeholder(R.drawable.user)
                                    .into(circleImageView);*/


                            //progressBar.setVisibility(View.VISIBLE);

                           /* Glide.with(TodoMainActivity.this)
                                    .load(String.valueOf(downloadUrl))
                                    .asBitmap()
                                   *//* .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })*//*
                                     .centerCrop()
                                     .into(circleImageView)
                            ;*/


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });

        } else {
            //TODo display error Toast
        }
    }
}



