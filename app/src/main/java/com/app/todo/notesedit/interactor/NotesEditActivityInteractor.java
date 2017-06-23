package com.app.todo.todoMain.interactor;

import android.content.Context;

import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.NotesEditActivityPresenterInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesEditActivityInteractor implements NotesEditActivityInteractorInterface{
    Context context;
    NotesEditActivityPresenterInterface presenter;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseReference;
    public NotesEditActivityInteractor(Context context, NotesEditActivityPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public void editNotes(NotesModel model) {

    }
}
