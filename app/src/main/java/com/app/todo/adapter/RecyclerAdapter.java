package com.app.todo.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.fragment.MyFragment;
import com.app.todo.model.NotesModel;

import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Context context;
    List<NotesModel> model;
    public RecyclerAdapter(Context context, List<NotesModel> model)  {
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
        holder.dateTextview.setText(model.get(position).getDate());
        holder.contentTextview.setText(model.get(position).getContent());

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

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, model.size());
    }
    public void archiveItem(int position){

        notifyDataSetChanged();
        notifyItemRangeChanged(position,model.size());
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatTextView titleTextView, dateTextview, contentTextview;
        CardView cardView;
        public TaskViewHolder(final View itemView)
        {
            super(itemView);
            titleTextView = (AppCompatTextView) itemView.findViewById(R.id.title_TextView);
            dateTextview = (AppCompatTextView) itemView.findViewById(R.id.date_TextView);
            contentTextview = (AppCompatTextView) itemView.findViewById(R.id.content_TextView);
            cardView = (CardView) itemView.findViewById(R.id.myCardView);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.myCardView:

                    MyFragment fragment = new MyFragment();
                    Bundle args = new Bundle();
                    NotesModel note=model.get(getAdapterPosition());
                    args.putString("title", note.getTitle());
                    args.putString("content", note.getContent());
                    fragment.setArguments(args);
                    ((Activity)context).getFragmentManager().beginTransaction().add(R.id.myframe_layout, fragment).commit();
                    break;
            }
        }
    }

}
