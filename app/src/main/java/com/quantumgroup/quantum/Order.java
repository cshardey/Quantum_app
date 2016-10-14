package com.quantumgroup.quantum;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Order extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private GridLayoutManager lLayout;
    private String current_date;
    TextView order;
    TextView vendorName;
    TextView vendorAddress;
    String vendor_name;
    String vendor_address;
    String responseJson;
    Integer status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        vendorAddress = (TextView)findViewById(R.id.vendor_address);
        vendorName =(TextView)findViewById(R.id.vendor_name);
        order =(TextView)findViewById(R.id.error);
        new AsyncFetch().execute();
    }
    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Order.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
               // url = new URL("http://192.168.43.68:8069/weeks?for=order_meal");
                url = new URL("http://192.168.43.34:8069/weeks?for=order_meal");
               // url = new URL ("http://192.168.43.18/quantum/weeks.json");
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

                // setDoOutput to true as we recieve data from json file


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
                    return (result.toString());

                }
                else if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED){
                    return ("noaccess");

                } else {

                    return ("unsuccessful");
                }
            } catch (java.net.ConnectException e) {
                System.out.println("More than "  + " elapsed.");

                return ("timeout");
            }  catch (java.net.SocketTimeoutException e){
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
            List<DataWeeks> data=new ArrayList<>();
            lLayout = new GridLayoutManager(Order.this, 2);
            pdLoading.dismiss();

            if(result.equalsIgnoreCase("timeout")){
                AlertDialog alertDialog = new AlertDialog.Builder(Order.this).create();
                alertDialog.setTitle("Connection Timeout");
                alertDialog.setMessage("Took to long establishing connection to server");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncFetch().execute();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        });
                alertDialog.show();


            }
            if(result.equalsIgnoreCase("noaccess")){
                AlertDialog alertDialog = new AlertDialog.Builder(Order.this).create();
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

                JSONObject payload = new JSONObject(result);
                responseJson = payload.getString("result");
                JSONObject result_payload = new JSONObject(responseJson);
                if (result_payload.has("status")){
                    status = result_payload.getInt("status");
                    if (status == 200){

                        JSONObject weeks_payload = result_payload.getJSONObject("weeks");

                        String  vendorPayload = weeks_payload.getString("vendor");
                        JSONObject vendor_obj = new JSONObject(vendorPayload);
                        vendor_name = vendor_obj.getString("name");
                        vendor_address = vendor_obj.getString("contact_address");


                        int weeksCount = weeks_payload.getInt("weeks_count");

                        for (int week=1;week<=weeksCount;week++){

                            DataWeeks weeksData = new DataWeeks();
                            weeksData.weekNumber=week;
                            data.add(weeksData);
                        }
                        vendorName.setText(vendor_name);
                        vendorAddress.setText(vendor_address);

                        RecyclerView rView = (RecyclerView)findViewById(R.id.weeks);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);

                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(Order.this, data);
                        rView.setAdapter(rcAdapter);

                    }else{

                    }
                }



//                JSONArray jArray = new JSONArray(result);
//
//                // Extract data from json and store into ArrayList as class objects
//                for(int i=0;i<jArray.length();i++){
//                    JSONObject json_data = jArray.getJSONObject(i);
//                    DataWeeks weeksData = new DataWeeks();
//                    weeksData.weekNumber= json_data.getString("week");
//
//
//                    data.add(weeksData);
//                }

                // Setup and Handover data to recyclerview




            } catch (JSONException e) {
               // Toast.makeText(Order.this, e.toString(), Toast.LENGTH_LONG).show();
                 order.setText("Error establishing connecting..");


            }

        }

    }

}
