package com.quantumgroup.quantum;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mr. Vanson on 8/26/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private int lastPosition = -1;
    List<DataWeeks> data = Collections.emptyList();
    private Context context;
    DataWeeks current;
    CardView container;
    public RecyclerViewAdapter(Context context, List<DataWeeks> data) {

        this.data = data;
        this.context = context;
        this.container=container;
    }




    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_week, null);

        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        current = data.get(position);
        holder.weekNumber.setText("Week "+current.weekNumber);

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}

