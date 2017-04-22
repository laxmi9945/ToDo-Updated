package com.app.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.baseclass.BaseActivity;



public class NotesItemDetailsActivity extends BaseActivity implements View.OnClickListener {
    AppCompatEditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoitemsdetails);

        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("name");
        initView();
        if(bundle!=null)
        {
            Log.i("test", "onCreate: ");
            String data =(String) bundle.get("keys");
            editText.setText(data);
        }

    }

    @Override
    public void initView() {
        editText =(AppCompatEditText)findViewById(R.id.edit_title);
        setClicklistener();
    }

    @Override
    public void setClicklistener() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_item_details_action,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pushpin:

                Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            case R.id.action_save:

                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();

                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
