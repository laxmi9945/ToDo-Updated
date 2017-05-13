package com.app.todo.todoMain.ui.activity;


import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.app.todo.model.NotesModel;

import java.util.List;

public interface TodoMainActivityInterface extends TextToSpeech.OnInitListener,SearchView.OnQueryTextListener, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    void showDialog(String message);
    void hideDialog();
    void getNotesListFailure(String message);
    void getNotesListSuccess(List<NotesModel> modelList);
}
