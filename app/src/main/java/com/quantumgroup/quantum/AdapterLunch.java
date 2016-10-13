package com.quantumgroup.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
public class AdapterLunch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String PREFS_NAME = "MyPrefsFile";
    private Context context;
    private LayoutInflater inflater;
    private byte[] imageAsBytes;
    private String ID;
    List<DataLunch> data = Collections.emptyList();
    DataLunch current;
    int currentPos = 0;
    int sel = 0;




    // create constructor to innitilize context and data sent from MainActivity
    public AdapterLunch(Context context, List<DataLunch> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_layout, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        DataLunch current = data.get(position);
               myHolder.textLunchName.setText(current.lunchName);
       myHolder.id.setText(current.lunchId);
        //checkes if image isset for server data
        if (current.lunchImage==null){
            myHolder.ivImage.setImageResource(R.drawable.ic_local_dining_black_48dp);
        }
        else {
            byte[] imageAsBytes = Base64.decode(current.lunchImage.getBytes(), Base64.DEFAULT);
            myHolder.ivImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
            );
        }

        // load image into imageview using glide
        //   Glide.with(context).load("http://192.168.1.7/test/images/" + current.lunchImage)
        // .placeholder(R.drawable.ic_turned_in_black_48dp)
        // .error(R.drawable.ic_turned_in_black_48dp
        //  )
        // .into(myHolder.ivImage);

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textLunchName;
        ImageView ivImage;
        TextView id;
        CardView card;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();//used to check item selection

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textLunchName = (TextView) itemView.findViewById(R.id.name);

            context = itemView.getContext();
            ivImage = (ImageView) itemView.findViewById(R.id.imageView);//
            card = (CardView) itemView.findViewById(R.id.cardView);
            id =(TextView)itemView.findViewById(R.id.food_id);
            id.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(this);
            textLunchName.setOnClickListener(this);
        }

        public TextView getTitle() {
            return textLunchName;
        }

        @Override
        public void onClick(View view) {
            SharedPreferences settings = context.getSharedPreferences(Mondaywk2.PREFS_NAME, 0); // 0 - for private mode
            SharedPreferences.Editor editor = settings.edit();
            String food_id= String.valueOf(id.getText());
            String food_date =String.valueOf(textLunchName.getText());

            editor.putString("id", food_id);
            editor.putString("date",food_date);
            editor.commit();

            Snackbar.make(view, "Meal selected " + String.valueOf(textLunchName.getText())+"\n Swipe to select food for next day.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();


        }
    }


}