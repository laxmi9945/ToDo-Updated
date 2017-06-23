package com.app.todo.todoMain.presenter;

import android.os.Bundle;



public interface NotesAddPresenterInterface {

    void addNoteToFirebase(Bundle bundle);

    void showDialog(String message);
    void hideDialog();

    void noteAddSuccess(String message);
    void noteAddFailure(String message);

}