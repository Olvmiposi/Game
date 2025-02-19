package com.example.game.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.R;

import java.util.List;

public class YearRecyclerViewAdapter extends RecyclerView.Adapter<YearRecyclerViewAdapter.ViewHolder> {

    private List<Integer> mYears;
    private LayoutInflater mInflater;
    private int selected_position;
    private static int selectedItem = -1;

    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public YearRecyclerViewAdapter(Context context, List<Integer> years) {
        this.mInflater = LayoutInflater.from(context);
        this.mYears = years;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int year = mYears.get(position);
        holder.myTextView.setText(String.valueOf(year));
        holder.setIsRecyclable(true);
        if(selectedItem == position)
            holder.itemView.setSelected(true);
        // Here I am just highlighting the background
        holder.itemView.setBackgroundColor(selected_position == position ? Color.parseColor("#34833C") : Color.TRANSPARENT);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mYears.size();
    }
    // convenience method for getting data at click position
    public int getItem(int id) {
        return mYears.get(id);
    }
    public int getSelectedItem() {
        return selectedItem;
    }
    public void setSelectedItem(int position)
    {
        selectedItem = position;
    }


    public int getSelected_position() {
        return selected_position;
    }

    public void setSelected_position(int selected_position) {
        this.selected_position = selected_position;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvYear);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);
        }
    }
}