package com.app.todo.fragment;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.app.todo.R;

import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePicker datePicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reminderfragment, container, false);

        datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        return view;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        updateDate(year, month, dayOfMonth);
    }
    public void updateDate(int year, int month, int day) {
        Calendar calendar= Calendar.getInstance();

        Toast.makeText(getApplicationContext(), "reminder set", Toast.LENGTH_SHORT).show();
    }

}
