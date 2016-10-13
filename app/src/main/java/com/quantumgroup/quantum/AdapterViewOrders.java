package com.quantumgroup.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mr. Vanson on 8/24/2016.
 */
public class AdapterViewOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private byte[] imageAsBytes;
    private String ID;
    List<DataLunch> data = Collections.emptyList();
    DataLunch current;
    int currentPos = 0;
    int sel = 0;
    private int selectedItem = -1;




    // create constructor to innitilize context and data sent from MainActivity
    public AdapterViewOrders(Context context, List<DataLunch> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_order_layout, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        final DataLunch current = data.get(position);
        myHolder.textLunchName.setText(current.lunchName);
        myHolder.date.setText(current.lunchDate);
        SharedPreferences settings = context.getSharedPreferences(Feedback.PREFS_NAME, 0);
        String hasReview = settings.getString("order_id", "");//Get "hasReview" value. If the value doesn't exist yet false is returned
        //checkes if image isset for server data
        if (current.lunchImage.equals("false")){
            myHolder.ivImage.setImageResource(R.drawable.ic_local_dining_black_48dp);
        }
        else {
            byte[] imageAsBytes = Base64.decode(current.lunchImage.getBytes(), Base64.DEFAULT);
            myHolder.ivImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
            );
        }

        if (current.lunchCom) {
            if (current.isReviewed) {
                myHolder.com.setImageResource(R.drawable.com_true);
            } else {
                myHolder.com.setImageResource(R.drawable.com);
            }


            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), Feedback.class);
                    intent.putExtra("id", current.lunchId);
                    intent.putExtra("order_id", current.lunchOrderId);
                    intent.putExtra("food_name", String.valueOf(myHolder.textLunchName.getText()));
                    intent.putExtra("has_reviwed",current.isReviewed);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) myHolder.ivImage.getDrawable();
                    Bitmap foodBitmap = bitmapDrawable.getBitmap();
                    intent.putExtra("food_image", foodBitmap);
                    intent.putExtra("date", String.valueOf(myHolder.date.getText().toString()));
                    context.startActivity(intent);
                }
            });

        }


    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder  {

        TextView textLunchName;
        ImageView ivImage;

        ImageView com;
        TextView date;
        CardView card;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();//used to check item selection

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textLunchName = (TextView) itemView.findViewById(R.id.name);
           date =(TextView)itemView.findViewById(R.id.date);
            com =(ImageView)itemView.findViewById(R.id.imageView2);
            context = itemView.getContext();
            ivImage = (ImageView) itemView.findViewById(R.id.imageView);//
            card = (CardView) itemView.findViewById(R.id.cardView);


        }





    }


}