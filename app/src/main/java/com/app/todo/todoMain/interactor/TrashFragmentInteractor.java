package com.app.todo.todoMain.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.presenter.TrashFragmentPresenter;
import com.app.todo.utils.CommonChecker;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrashFragmentInteractor implements TrashFragmentInteractorInterface {
    Context context;
    TrashFragmentPresenter presenter;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public TrashFragmentInteractor(Context context, TrashFragmentPresenter presenter) {
        this.context=context;
        this.presenter=presenter;
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference(Constants.userdata);
    }

    @Override
    public void getDeleteNote(String uId) {
        presenter.showDialog(context.getString(R.string.getting_deleted_notes));

        if (CommonChecker.isNetworkConnected(context)){
            
            databaseReference.child(uId).addValueEventListener(new ValueEventListener() {
                //String uId = firebaseAuth.getCurrentUser().getUid();=
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<NotesModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<NotesModel>>() {
                    };
                    ArrayList<NotesModel> notesModel = new ArrayList<>();

                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        ArrayList<NotesModel> notesModel_ArrayList;
                        notesModel_ArrayList = post.getValue(arrayListGenericTypeIndicator);
                        notesModel.addAll(notesModel_ArrayList);

                    }
                    presenter.noteDeleteSuccess(notesModel);
                    presenter.hideDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    //Toast.makeText(context, getString(R.string.fetching_error) , Toast.LENGTH_SHORT).show();
                    presenter.noteDeleteFailure(context.getString(R.string.delete_failure));
                    presenter.hideDialog();

                }
            });

        }else {
            presenter.noteDeleteFailure(context.getString(R.string.fail));
            presenter.hideDialog();
        }


    }
        
    }
