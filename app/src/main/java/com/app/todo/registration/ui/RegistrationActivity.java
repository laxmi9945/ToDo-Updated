package com.app.todo.registration.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.registration.presenter.RegistrationPresenter;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityInterface{
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
    //a Uri object to store file path
    private Uri filePath;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void addProfilePic() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == PICK_IMAGE_CAMERA)
                onCaptureImageResult(data);
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profile_imageView.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profile_imageView.setImageBitmap(bitmap);
    }
}



