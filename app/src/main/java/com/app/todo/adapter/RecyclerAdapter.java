package com.app.todo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.app.todo.R;
import com.app.todo.fragment.NoteseditFragment;
import com.app.todo.model.NotesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Context context;
    List<NotesModel> model;
    private int lastPosition = -1;

    public RecyclerAdapter(Context context, List<NotesModel> model)  {
      //  model=new ArrayList<>();
        this.model = model;
        this.context = context;

    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType)   {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_todonotes, parent, false);
        TaskViewHolder myViewHolder = new TaskViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.TaskViewHolder holder, final int position) {
        holder.titleTextView.setText(model.get(position).getTitle());
        holder.dateTextView.setText(model.get(position).getDate());
        holder.timeTextView.setText(model.get(position).getTime());
        holder.contentTextView.setText(model.get(position).getContent());
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
       /* notifyItemRemoved(position);
        notifyItemRangeChanged(position, model.size());*/
    }
    public void archiveItem(int position){

        notifyDataSetChanged();
        notifyItemRangeChanged(position,model.size());
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatTextView titleTextView, dateTextView, contentTextView,timeTextView;
        CardView cardView;
        public TaskViewHolder(final View itemView)
        {
            super(itemView);
            titleTextView = (AppCompatTextView) itemView.findViewById(R.id.title_TextView);
            dateTextView = (AppCompatTextView) itemView.findViewById(R.id.date_TextView);
            timeTextView = (AppCompatTextView) itemView.findViewById(R.id.time_TextView);
            contentTextView = (AppCompatTextView) itemView.findViewById(R.id.content_TextView);
            cardView = (CardView) itemView.findViewById(R.id.myCardView);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.myCardView:

                    NoteseditFragment fragment = new NoteseditFragment();
                    Bundle args = new Bundle();
                    NotesModel note=model.get(getAdapterPosition());
                    args.putString("title", note.getTitle());
                    args.putString("content", note.getContent());
                    args.putString("date", note.getDate());
                    args.putString("time", note.getTime());
                    args.putInt("id", note.getId());
                    fragment.setArguments(args);
                    /*ActivityOptionsCompat option = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(ListActivity.this, cardView, "card");*/
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

}
