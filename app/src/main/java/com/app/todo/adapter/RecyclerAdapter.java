package com.app.todo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.activity.TodoMainActivity;
import com.app.todo.todoMain.ui.fragment.NoteseditFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Context context;
    List<NotesModel> model;
    private int lastPosition = -1;
    Integer[] colorList = {R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5,R.color.color6
            ,R.color.color7,R.color.color8,R.color.color9,R.color.color10,R.color.color11,R.color.color12,R.color.color13
            ,R.color.color14,R.color.color15,R.color.color16,R.color.color17} ;

    public RecyclerAdapter(Context context, List<NotesModel> model)  {
      //  model=new ArrayList<>();
        this.model = model;
        this.context = context;
    }

    public RecyclerAdapter(Context baseContext, TodoMainActivity todoMainActivity) {

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

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final CharSequence[] options = {"Share note","Cancel"};
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
                    builder.show();

                    return true;
                }
            });
           // cardView.setOnLongClickListener(new MyclickListener());

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.myCardView:
                    NoteseditFragment fragment = new NoteseditFragment();
                    ((AppCompatActivity)context).setTitle("Notes Update");
                    Bundle args = new Bundle();
                    NotesModel note=model.get(getAdapterPosition());
                    args.putString("title", note.getTitle());
                    args.putString("content", note.getContent());
                    args.putString("date", note.getDate());
                    args.putString("time", note.getTime());
                    args.putInt("id", note.getId());
                    args.putString("reminder",note.getReminderDate());
                    fragment.setArguments(args);
                    ((AppCompatActivity)context).getFragmentManager().beginTransaction().replace(R.id.frameLayout_container, fragment).addToBackStack(null).commit();

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
   /* private final class MyclickListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View view) {
            // create it from the object's tag
            ClipData.Item item = new ClipData.Item((CharSequence)view.getTag());

            String[] mimeTypes = {
                    ClipDescription.MIMETYPE_TEXT_PLAIN
            };
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag( data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );


            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }
    class MyDragListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            return false;
        }
    }
*/
}
