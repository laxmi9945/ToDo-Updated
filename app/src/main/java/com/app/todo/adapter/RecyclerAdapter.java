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

import com.app.todo.R;
import com.app.todo.fragment.NoteseditFragment;
import com.app.todo.model.NotesModel;

import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Context context;
    List<NotesModel> model;

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
                    args.putString("id", String.valueOf(note.getId()));
                    fragment.setArguments(args);
                    ((AppCompatActivity)context).getFragmentManager().beginTransaction().replace(R.id.frameLayout_container, fragment).addToBackStack(null).commit();
                    break;
            }
        }
    }

}
