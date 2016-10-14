package com.quantumgroup.quantum;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Wk1  extends Fragment  {
    private  List<DataLunch> data=new ArrayList<>();

    View rootView;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView lunch;
    private AdapterViewOrders mAdapter;
    public static final String PREFS_NAME = "Wk1";
    String mydate;
    TextView error;
    String access_token;
    SharedPreferences settings;
    String responseJson;
    Integer status;
    String ip_addrs;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mondaywk2, container, false);
       // SharedPreferences editor = getActivity().getSharedPreferences(ViewOrders.PREFS_NAME, 0); // get week date from tab

        mydate=getArguments().getString("week","");
        error = (TextView)rootView.findViewById(R.id.error);
        ip_addrs = getString(R.string.ip_address);
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
                settings = getActivity().getSharedPreferences(Signin.PREFS_NAME, 0);
                access_token = settings.getString("access_token", "");
                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                //  url = new URL("http://192.168.43.18/quantum/wednesday.json");
                //url = new URL("http://192.168.43.68:5000/employee/foods/"+mydate +"?user_id=" + user_id);
               // url = new URL("http://192.168.43.68:8069/employee/foods/" + mydate);
                url = new URL(ip_addrs+"/employee/foods/" + mydate);

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
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setRequestMethod("POST");
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
//                e1.printStackTrace();
//                return e1.toString();
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


                }else if (response_code == HttpURLConnection.HTTP_NO_CONTENT){

                    return ("nocontent");
                }

                else if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED){
                    return ("noaccess");

                }
                else {

                    return ("unsuccessful");
                }

            } catch (java.net.ConnectException e) {
                return ("timeout");
            } catch (java.net.SocketTimeoutException e){
                return ("timeout");
            }

            catch (IOException e) {
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

         if (result.equalsIgnoreCase("nocontent")){
         error.setText("Oops.. You haven't made an order for this week");

         }if(result.equalsIgnoreCase("timeout")){
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Connection Timeout");
                alertDialog.setMessage("Took to long establishing connection to server");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncFetch().execute();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        System.exit(0);
                    }
                });
                alertDialog.show();


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
                                System.exit(1);
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
                        String payload_food= result_payload.getString("foods");
                        JSONArray jArray = new JSONArray(payload_food);
                        for(int i=0;i<jArray.length();i++){
                            JSONObject json_data = jArray.getJSONObject(i);
                            DataLunch lunchData = new DataLunch();
                            lunchData.lunchImage= json_data.getString("image");
                            lunchData.lunchName= json_data.getString("name");
                            lunchData.lunchId = json_data.getString("id");
                            lunchData.lunchDate = json_data.getString("date");
                            lunchData.lunchCom = json_data.getBoolean("review_active");
                            lunchData.lunchOrderId = json_data.getString("order_id");
                            lunchData.isReviewed = json_data.getBoolean("is_reviewed");

                            data.add(lunchData);


                        }

                        // Setup and Handover data to recyclerview
                        lunch = (RecyclerView)rootView.findViewById(R.id.lunch_menu);
                        mAdapter = new AdapterViewOrders(getActivity(), data);
                        lunch.setAdapter(mAdapter);
                        lunch.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }else if(status == 204){
                        error.setText("Oops.. You haven't made an order for this week");

                    }else{
                        Log.i("Error","Didnt receive status from server");
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
