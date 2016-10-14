package com.quantumgroup.quantum;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class Monday  extends Fragment  {

    PopupWindow popupWindow;
    private  List<DataLunch> data=new ArrayList<>();

    View rootView;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView lunch;
    private AdapterMonday mAdapter;
    public static final String PREFS_NAME = "Monday";
    private EditText custom;
    String mydate;
    TextView error;
    String access_token;
    String responseJson;
    Integer status;
    String ip_addrs;

    SharedPreferences settings;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mondaywk2, container, false);
        error = (TextView)rootView.findViewById(R.id.error);
//        mydate=getArguments().getString("week","");
        SharedPreferences settings = this.getActivity().getSharedPreferences(Week2.PREFS_NAME, 0);
        mydate = settings.getString("date1", null);
        ip_addrs = getString(R.string.ip_address);
        TextView show_date = (TextView)rootView.findViewById(R.id.date_show);
        show_date.setText(mydate);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Make call to AsyncTask
        new AsyncFetch().execute();


        SharedPreferences settings = getContext().getSharedPreferences(Monday.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();


        editor.commit();
    }
    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                settings = getActivity().getSharedPreferences(Signin.PREFS_NAME, 0);
                //String user_id = settings.getString("user_id", "");
                access_token = settings.getString("access_token","");

                // Enter URL address where your json file resides


                  url = new URL(ip_addrs+"/manager/foods?date="+mydate);


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setDoOutput(true);

                JSONObject empty_obj = new JSONObject();
                JSONObject requestPayload = new JSONObject();
                try {

                    requestPayload.put("params", empty_obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(requestPayload.toString());
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                Log.i("JSONDATA", requestPayload.toString());

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    Log.e("DatafromServer",result.toString());
                    return (result.toString());


                }
                else if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED){
                    return ("noaccess");

                }
                else if(response_code == HttpURLConnection.HTTP_NO_CONTENT){
                    return ("nocontent");

                }

                else {

                    return ("unsuccessful");
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

            if((result.equalsIgnoreCase("nocontent"))){
                error.setText("Menu not available for this day");
            }
            if((result.equalsIgnoreCase("unsuccesful")))
            {
                error.setText("Error in connection");
            }
            if(result.equalsIgnoreCase("noaccess")){

                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Unauthorized Access");
                alertDialog.setMessage("You don't have permission to use Quantum app\n Contact Administrator" );
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        });
                Window window = alertDialog.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

            }
            try {
                JSONObject payload_data = new JSONObject(result);
                responseJson = payload_data.getString("result");
                JSONObject result_payload = new JSONObject(responseJson);

                if (result_payload.has("status")){
                    status = result_payload.getInt("status");
                    if (status == 200){
                        String payload_foodList= result_payload.getString("food_list");
                        JSONArray jArray = new JSONArray(payload_foodList);
                        for(int i=0;i<jArray.length();i++){
                            JSONObject json_data = jArray.getJSONObject(i);
                            DataLunch lunchData = new DataLunch();
                            lunchData.lunchImage= json_data.getString("image");
                            lunchData.lunchName= json_data.getString("name");
                            lunchData.lunchId = json_data.getString("id");


                            data.add(lunchData);


                        }

                        // Setup and Handover data to recyclerview
                        lunch = (RecyclerView)rootView.findViewById(R.id.lunch_menu);
                        mAdapter = new AdapterMonday(getActivity(), data);
                        lunch.setAdapter(mAdapter);
                        lunch.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }else if (status == 204){
                        error.setText("Menu not available for this day");
                    }
                }


                // Extract data from json and store into ArrayList as class objects




            } catch (JSONException e) {
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                Log.e("Error",e.toString());


            }


        }

    }





}
