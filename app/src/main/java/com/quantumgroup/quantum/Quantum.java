package com.quantumgroup.quantum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.pushbots.push.Pushbots;

import java.util.ArrayList;

public class Quantum extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayoutAndroid;
    CoordinatorLayout rootLayoutAndroid;
    GridView gridView;
    Context context;
    ArrayList arrayList;

    public static String[] gridViewStrings = {
            "Lunch",
            "Other"

    };
    public static int[] gridViewImages = {
            R.drawable.meals,
            R.mipmap.ic_launcher
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantum);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages));
        initInstances();

        try {
            SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0);
            String fullname = settings.getString("fullname", "");
            Pushbots.sharedInstance().tag("Sage Petroleum");
            Pushbots.sharedInstance().setAlias(fullname); // assign name of user to pushbots
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    private void initInstances() {
        rootLayoutAndroid = (CoordinatorLayout) findViewById(R.id.android_coordinator_layout);
        collapsingToolbarLayoutAndroid = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_android_layout);
        collapsingToolbarLayoutAndroid.setTitle("Quantum App");
    }
    public void itemClicked(int position) {
       switch (position){
           case 0:
               Intent intent=new Intent(this, LunchHome.class);
               startActivity(intent);


       }
    }

}
