package com.quantumgroup.quantum;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mr. Vanson on 8/24/2016.
 */
public class AdapterThursday extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> createdIds = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;
    private byte[] imageAsBytes;
    private String ID;

    CardView card;
    List<DataLunch> data;
    DataLunch current;
    int currentPos = 0;
    int sel;
    private int selectedItem = -1;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterThursday(Context context, List<DataLunch> data) {
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
        SharedPreferences date = context.getSharedPreferences(Week2.PREFS_NAME, 0);//retrieve date of the day from tabs
        String food_date = date.getString("date4", null);

        Gson gson = new Gson();
        String json = date.getString("created_id", null);
        String get_date = date.getString("created_date","");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> cr_id = gson.fromJson(json, type);
        ArrayList<String> cr_date = gson.fromJson(get_date, type);
        //String [] cr_date = {date.getString("cr_date1",""), date.getString("cr_date2",""),date.getString("cr_date3",""),date.getString("cr_date4",""),date.getString("cr_date5","")};
        // String [] cr_id ={date.getString("id1",""),date.getString("id2",""),date.getString("id3",""),date.getString("id4",""),date.getString("id5","")};





        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        DataLunch current = data.get(position);


        myHolder.textLunchName.setText(current.lunchName);
        ID = current.lunchId;
        myHolder.id.setText(current.lunchId);


        if (current.lunchImage.equals("false")){
            myHolder.ivImage.setImageResource(R.drawable.ic_local_dining_black_48dp);
        }
        else {
             try {
                 byte[] imageAsBytes = Base64.decode(current.lunchImage.getBytes(), Base64.DEFAULT);
                 myHolder.ivImage.setImageBitmap(
                         BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
                 );
             }catch (IllegalArgumentException e){
                 e.printStackTrace();
             }

        }
        myHolder.itemView.setSelected(selectedItem == position);

        // load image into imageview using glide
        //   Glide.with(context).load("http://192.168.1.7/test/images/" + current.lunchImage)
        // .placeholder(R.drawable.ic_turned_in_black_48dp)
        // .error(R.drawable.ic_turned_in_black_48dp
        //  )
        // .into(myHolder.ivImage);
        try {
            // SharedPreferences createdId = context.getSharedPreferences(Week2.PREFS_NAME, 0);//retrieve date of the day from tabs
            // String ids = createdId.getString("createdId", null);
            // JSONArray jsonArray2 = new JSONArray(ids);

            // JSONObject json = jsonArray2.getJSONObject(position);



            // String crid= json.getString("id");//gets iterated id
            // String crdate =json.getString("date"); //gets iterated date

            for (int i=0;i<cr_id.size();i++){
                if (ID.equals(cr_id.get(i).toString())&&food_date.equals(cr_date.get(i).toString())){
                    myHolder.confi.setImageResource(R.drawable.ic_check_circle_white_48dp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        ImageView confi;
        CardView card;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();//used to check item selection

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textLunchName = (TextView) itemView.findViewById(R.id.name);

            context = itemView.getContext();
            ivImage = (ImageView) itemView.findViewById(R.id.imageView);//
            card = (CardView) itemView.findViewById(R.id.cardView);
            id = (TextView) itemView.findViewById(R.id.food_id);
            confi = (ImageView) itemView.findViewById(R.id.checked);
            id.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(this);
            textLunchName.setOnClickListener(this);


        }

        public TextView getTitle() {
            return textLunchName;
        }

        @Override
        public void onClick(View view) {
            SharedPreferences date = context.getSharedPreferences(Week2.PREFS_NAME, 0);
            //retrieve date of the day from tabs
            final String food_date = date.getString("date4", null);

            SharedPreferences settings = context.getSharedPreferences(Thursday.PREFS_NAME, 0); // 0 - for private mode
            final SharedPreferences.Editor editor = settings.edit();

            if (getAdapterPosition() == data.size() - 1) {

                selectedItem = getLayoutPosition();
                notifyDataSetChanged();
                notifyItemChanged(selectedItem);

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.activity_feedback, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                        true);

                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);

                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                final EditText custom = (EditText) popupView.findViewById(R.id.custom);
                Button cancel = (Button) popupView.findViewById(R.id.cancel);



                cancel.setOnClickListener(new View.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
                    public void onClick(View arg0) {


                        popupWindow.dismiss();

                    }

                });
                Button save = (Button) popupView.findViewById(R.id.ohk);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String custom_meal = custom.getText().toString();
                        textLunchName.setText(custom_meal);
                        editor.putString("custom_meal",custom_meal);
                        editor.putString("date",food_date);
                        editor.commit();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAsDropDown(popupView, 20, 0);
            } else {


                String food_id = String.valueOf(id.getText());


                editor.putString("id", food_id);
                editor.putString("date", food_date);
                editor.commit();


                Snackbar.make(view, "Meal selected " + String.valueOf(textLunchName.getText()) + "\n Swipe to select food for next day.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                selectedItem = getLayoutPosition();


                notifyDataSetChanged();
                notifyItemChanged(selectedItem);

            }


        }
    }


}

