package com.quantumgroup.quantum;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mr. Vanson on 8/26/2016.
 */
public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    String c;
    public TextView weekNumber;

    public CardView container;
    public static final String gh = "Ghana";
    private final Context context;



    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        weekNumber = (TextView)itemView.findViewById(R.id.week);
        container=(CardView)itemView.findViewById(R.id.card_view);

        context = itemView.getContext();
    }

    @Override
    public void onClick(View v) {






                Intent intent =  new Intent(context, Week2.class);
                intent.putExtra("week",getAdapterPosition());
                v.getContext().startActivity(intent);



        }

    }



