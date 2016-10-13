package com.quantumgroup.quantum;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Submit extends AppCompatActivity {
    String responseServer;
    TextView success;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        success =(TextView)findViewById(R.id.success);
        setSupportActionBar(toolbar);
        try {
            senddatatoserver();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void senddatatoserver() throws JSONException {
        JSONObject post_dict = new JSONObject();

        SharedPreferences day1 = getSharedPreferences(Mondaywk2.PREFS_NAME, 0);//retrieve values from first day
        String food_id1 = day1.getString("id", null);
        String food_date1 = day1.getString("date", null);

        SharedPreferences day2 = getSharedPreferences(Tuesdaywk2.TUESDAY_MEAL, 0);//retrieve values from second day
        String food_id2 = day2.getString("idT", null);
        String food_date2 = day2.getString("dateT", null);

        JSONArray elements=new JSONArray();
        JSONObject aux;
        if (food_id1==null){

        }else{
            aux=new JSONObject().put("id", food_id1);
            aux.put("date", food_date1);
            elements.put(aux);
        }




        aux=new JSONObject().put("id", food_id2);
        aux.put("date", food_date2);
        elements.put(aux);



        //JSONObject food_list =new JSONObject().put("userlist",elements.toString());

        JSONObject finalObject = new JSONObject();
        finalObject.put("food_list", elements);

        Log.e("mainToPost", "mainToPost" + finalObject.toString());

            new AsyncFetch().execute(String.valueOf(finalObject));
            //call to async class
        }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Submit.this);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... strings) {

            String food_list = strings[0];
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = new URL("http://192.168.43.68:5000/employee/foods");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();

               // conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.connect();

               // String postKey = "food_list=" + JsonDATA; add key tp JSON post


                Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(food_list);
                writer.flush();
                Log.i("JSONDATA",food_list);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == 400) {
                    String err="400 error";
                    Log.i("400 eror", err);
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    Log.e("WhyNot", result.toString());
                    return (result.toString());


                } else {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.e("WhyNot", result.toString());
                    return ("unsucceful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            final String TAG = "Date worked";

            pdLoading.dismiss();
            success.setText("Order Received");




        }

    }

}
