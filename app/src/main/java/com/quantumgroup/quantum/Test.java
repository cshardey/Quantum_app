package com.quantumgroup.quantum;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Test extends AppCompatActivity {
    private static String url = "the url";

    // JSON Node names
    private static final String TAG_WEEKINFO = "weekinfo";
    private static final String TAG_ID = "id";
    private static final String TAG_DAY = "day";
    private static final String TAG_DATE = "date";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // Calling async task to get json

    }


}

