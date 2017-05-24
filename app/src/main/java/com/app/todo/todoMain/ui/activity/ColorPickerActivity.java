package com.app.todo.todoMain.ui.activity;

import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import com.app.todo.R;
import com.jrummyapps.android.colorpicker.ColorPanelView;
import com.jrummyapps.android.colorpicker.ColorPickerView;

public class ColorPickerActivity extends AppCompatActivity implements ColorPickerView.OnColorChangedListener, View.OnClickListener {
    private ColorPickerView colorPickerView;
    private ColorPanelView newColorPanelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.color_picker_layout);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("color_3", 0xFF000000);

        colorPickerView = (ColorPickerView) findViewById(R.id.cpv_color_picker_view);
        ColorPanelView colorPanelView = (ColorPanelView) findViewById(R.id.cpv_color_panel_old);
        newColorPanelView = (ColorPanelView) findViewById(R.id.cpv_color_panel_new);

        AppCompatButton btnOK = (AppCompatButton) findViewById(R.id.okButton);
        AppCompatButton btnCancel = (AppCompatButton) findViewById(R.id.cancelButton);

        ((LinearLayout) colorPanelView.getParent())
                .setPadding(colorPickerView.getPaddingLeft(), 0, colorPickerView.getPaddingRight(), 0);

        colorPickerView.setOnColorChangedListener(this);
        colorPickerView.setColor(initialColor, true);
        colorPanelView.setColor(initialColor);

        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }



    @Override
    public void onColorChanged(int newColor) {
        newColorPanelView.setColor(colorPickerView.getColor());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                edit.putInt("color_3", colorPickerView.getColor());
                edit.apply();
                finish();
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }
}
