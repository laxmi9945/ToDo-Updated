package com.app.todo.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.todoMain.ui.fragment.ArchiveFragment;
import com.app.todo.todoMain.ui.fragment.NotesFragment;
import com.app.todo.todoMain.ui.fragment.NoteseditFragment;
import com.app.todo.todoMain.ui.fragment.ReminderFragment;
import com.app.todo.todoMain.ui.fragment.TrashFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Activity context;
    List<NotesModel> model;
    private int lastPosition = -1;
    ArchiveFragment archiveFragment;
    Activity todoMainActivity;
    ReminderFragment reminderFragment;
    NotesFragment notesFragment;
    TrashFragment trashFragment;
    Integer[] colorList = {R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5,R.color.color6
            ,R.color.color7,R.color.color8,R.color.color9,R.color.color10,R.color.color11,R.color.color12,R.color.color13
            ,R.color.color14,R.color.color15,R.color.color16,R.color.color17} ;

    public RecyclerAdapter(Activity todoMainActivity, List<NotesModel> model, ArchiveFragment archiveFragment)  {
      //  model=new ArrayList<>();
        this.model = model;
        this.context = todoMainActivity;
        this.archiveFragment=archiveFragment;
        this.todoMainActivity=todoMainActivity;
    }

    public RecyclerAdapter(Activity context, ArrayList<NotesModel> todoHomeDataModel, TodoMainActivity todoMainActivity) {
        this.context=context;
        this.model=todoHomeDataModel;
        this.todoMainActivity=todoMainActivity;
    }

    public RecyclerAdapter(Activity context, ArrayList<NotesModel> reminderNoteList, ReminderFragment reminderFragment) {
    this.model=reminderNoteList;
        this.context=context;
        this.reminderFragment=reminderFragment;
    }

    public RecyclerAdapter(Activity context, List<NotesModel> filteredNotes, NotesFragment notesFragment) {
        this.context=context;
        this.model=filteredNotes;
        this.notesFragment=notesFragment;
    }

    public RecyclerAdapter(Activity context, List<NotesModel> filteredNotes, TrashFragment trashFragment) {
        this.context=context;
        this.model=filteredNotes;
        this.trashFragment=trashFragment;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType)   {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_todonotes_cards, parent, false);
        TaskViewHolder myViewHolder = new TaskViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.TaskViewHolder holder, final int position) {

        holder.titleTextView.setText(model.get(position).getTitle());
        holder.dateTextView.setText(model.get(position).getDate());
        holder.timeTextView.setText(model.get(position).getTime());
        holder.contentTextView.setText(model.get(position).getContent());

        holder.linearLayoutNoteItem.setBackgroundColor(ContextCompat.getColor(context,colorList[position%17]));

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
    }


    @Override
    public int getItemCount() {

        return model.size();
    }


    public void addNotes(NotesModel note) {

        model.add(note);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        model.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, model.size());
    }
    public void archiveItem(int position){

        notifyDataSetChanged();
        notifyItemRangeChanged(position,model.size());
    }

    public void reminderItem(){

        notifyDataSetChanged();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatTextView titleTextView, dateTextView, contentTextView,timeTextView;
        CardView cardView;
        LinearLayout linearLayoutNoteItem;

        public TaskViewHolder(final View itemView)
        {
            super(itemView);
            titleTextView = (AppCompatTextView) itemView.findViewById(R.id.title_TextView);
            dateTextView = (AppCompatTextView) itemView.findViewById(R.id.date_TextView);
            timeTextView = (AppCompatTextView) itemView.findViewById(R.id.time_TextView);
            contentTextView = (AppCompatTextView) itemView.findViewById(R.id.content_TextView);
            cardView = (CardView) itemView.findViewById(R.id.myCardView);
            linearLayoutNoteItem = (LinearLayout) itemView.findViewById(R.id.linearLayoutNoteItem);
            cardView.setOnClickListener(this);
            if(archiveFragment!=null) {
                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                    /*final CharSequence[] options = {"Share note","Cancel"};
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals("Share note")) {
                                NotesModel note=model.get(getAdapterPosition());
                                //Toast.makeText(context, "Yes...", Toast.LENGTH_SHORT).show();
                                Intent shareIntent=new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                String title=note.getTitle();
                                String content=note.getContent();
                                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.getTitle());
                                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Title: "+title+ "\n" +"Content: "+content);
                                context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_using)));
                                dialog.dismiss();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();*/
                        final CharSequence[] options = {"move to Notes", "move to Trash", "Cancel"};
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals("move to Notes")) {
                                /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                NotesModel notesModel = new NotesModel();
                                databaseReference.child(Constants.userdata).child(uId).child(notesModel.getDate()).child(String.valueOf(notesModel.getId())).setValue(notesModel);
                                DataBaseUtility dataBaseUtility = new DataBaseUtility(context);
                                dataBaseUtility.delete(notesModel);*/
                                    Toast.makeText(context, "Moved to Notes", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else if (options[item].equals("move to Trash")) {
                                    Toast.makeText(context, "Moved to Trash", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                });
            }
           // cardView.setOnLongClickListener(new MyclickListener());

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.myCardView:
                    if(archiveFragment!=null && trashFragment!=null && reminderFragment!=null || notesFragment!=null ) {
                        NoteseditFragment fragment = new NoteseditFragment();
                        (context).setTitle("Notes Update");
                        Bundle args = new Bundle();
                        NotesModel note = model.get(getAdapterPosition());
                        args.putString("title", note.getTitle());
                        args.putString("content", note.getContent());
                        args.putString("date", note.getDate());
                        args.putString("time", note.getTime());
                        args.putInt("id", note.getId());
                        args.putString("reminder", note.getReminderDate());

                        fragment.setArguments(args);

                        ( context).getFragmentManager()
                                .beginTransaction().replace(R.id.frameLayout_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    /*Intent intent = new Intent(context, DummyActivity.class);

                        *//*Bundle args = new Bundle();
                        NotesModel note = model.get(getAdapterPosition());
                        args.putString("title", note.getTitle());
                        args.putString("content", note.getContent());
                        args.putString("date", note.getDate());
                        args.putString("time", note.getTime());
                        args.putInt("id", note.getId());
                        args.putString("reminder", note.getReminderDate());*//*
                        Bundle args = new Bundle();
                        NotesModel note = model.get(getAdapterPosition());
                        args.putString("title", note.getTitle());
                        args.putString("content", note.getContent());
                        args.putString("date", note.getDate());
                        args.putString("time", note.getTime());
                        args.putInt("id", note.getId());
                        args.putString("reminder", note.getReminderDate());
                        intent.putExtras(args);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,cardView, context.getString(R.string.custom_transition));
                        context.startActivity(intent, options.toBundle());*/
                    break;

            }
        }

    }

    public void setFilter(ArrayList<NotesModel> newList){

        model=new ArrayList<>();
        model.addAll(newList );
        notifyDataSetChanged();

    }

    private void setAnimation(View viewToAnimate, int position) {

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }
    public void setNoteList(ArrayList<NotesModel> notesmodel){
        this.model.clear();
        notifyDataSetChanged();
        this.model.addAll(notesmodel);
        notifyDataSetChanged();
    }

}
