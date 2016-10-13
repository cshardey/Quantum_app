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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




import static com.quantumgroup.quantum.ObjectToFileUtil.writeObject;

public class Wk2  extends Fragment  {
    private  List<DataLunch> data=new ArrayList<>();

    View rootView;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView lunch;
    private AdapterViewOrders mAdapter;
    public static final String PREFS_NAME = "Wk2";
    String mydate;
    TextView error;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mondaywk2, container, false);
        SharedPreferences editor = getActivity().getSharedPreferences(ViewOrders.PREFS_NAME, 0); // 0 - for private mode
        mydate=editor.getString("date2",null);
        error = (TextView)rootView.findViewById(R.id.error);

        return rootView;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
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

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                //url = new URL("http://192.168.43.68:5000/manager/foods?date=2016-08-2");
                url = new URL("http://192.168.43.68:5000/employee/foods/"+mydate +"?user_id=" + user_id);

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
                conn.setRequestMethod("GET");


            } catch (IOException e1) {
                // TODO Auto-generated catch block
             Toast.makeText(getActivity(),"Connection timeout",Toast.LENGTH_LONG).show();
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

                else if(response_code == HttpURLConnection.HTTP_NO_CONTENT){
                    return ("nocontent");

                }
                else if(response_code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                    return ("timeout");
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


            pdLoading.dismiss();
            if(result.equalsIgnoreCase("nocontent")){
                error.setText("Oops.. Youhaven't made an order for this week");
            }

            if(result.equalsIgnoreCase("timeout")){
                error.setText("Oops.. Please check internet connection");
            }

            try {

                JSONArray jArray = new JSONArray(result);

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    DataLunch lunchData = new DataLunch();
                    lunchData.lunchImage= json_data.getString("image");
                    lunchData.lunchName= json_data.getString("name");
                    lunchData.lunchId = json_data.getString("id");
                    lunchData.lunchDate = json_data.getString("date");
                    lunchData.lunchCom = json_data.getBoolean("review_active");
                    lunchData.lunchOrderId = json_data.getString("order_id");
                    data.add(lunchData);


                }

                // Setup and Handover data to recyclerview
                lunch = (RecyclerView)rootView.findViewById(R.id.lunch_menu);
                mAdapter = new AdapterViewOrders(getActivity(), data);
                lunch.setAdapter(mAdapter);
                lunch.setLayoutManager(new LinearLayoutManager(getActivity()));

            } catch (JSONException e) {
               // error.setText("Oops.. Please check internet connection");
                Log.e("Error",e.toString());


            }

        }

    }
}
