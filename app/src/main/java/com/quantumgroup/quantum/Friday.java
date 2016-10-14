package com.quantumgroup.quantum;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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



public class Friday  extends Fragment  {
    private  List<DataLunch> data=new ArrayList<>();

    View rootView;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView lunch;
    private AdapterFriday mAdapter;
    TextView error;
    public static final String PREFS_NAME = "Friday";
    String mydate;
    String access_token;
    String responseJson;
    Integer status;
    String ip_addrs;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mondaywk2, container, false);
        error = (TextView)rootView.findViewById(R.id.error);
        SharedPreferences settings = this.getActivity().getSharedPreferences(Week2.PREFS_NAME, 0);
        mydate = settings.getString("date5", null);
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

    }
    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                SharedPreferences settings = getActivity().getSharedPreferences(Signin.PREFS_NAME, 0);
                String user_id = settings.getString("user_id", "");
                access_token = settings.getString("access_token","");
                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                //url = new URL("http://192.168.43.68:8069/manager/foods?date="+mydate);
                url = new URL(ip_addrs+"/manager/foods?date="+mydate);
               // url = new URL("http://192.168.43.18/quantum/tuesday.json");;

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
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setRequestProperty("Content-Type", "application/json");
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


                } else {

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
                        mAdapter = new AdapterFriday(getActivity(), data);
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
