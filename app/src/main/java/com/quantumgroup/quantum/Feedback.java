package com.quantumgroup.quantum;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class Feedback extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
   String user_comment;
   String comment_food;
    String quality_choice;
    String food_date;
    String [] quality = new String[]{"Excellent", "Good", "Poor"};
    String id;
    String order_id;
    EditText comment;
    String selected;
    String access_token;
    //ArrayAdapter<String> qualityAdapter;
    ArrayAdapter<CharSequence> qualityAdapter;
    Spinner qualitySpinner;
    SharedPreferences signin;
    Button clear;
    String ip_addrs;
    Boolean hasReviewed;

    public static final String PREFS_NAME = "Feedback";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // qualityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,quality);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Review");
        ip_addrs = getString(R.string.ip_address);
        comment=(EditText)findViewById(R.id.comment);

        TextView name= (TextView)findViewById(R.id.food);


        comment.setVisibility(View.INVISIBLE);
        final String food_name = getIntent().getExtras().getString("food_name");
        final String date = getIntent().getExtras().getString("date");
        name.setText(food_name);
        final String food_id = getIntent().getExtras().getString("id");
        order_id = getIntent().getExtras().getString("order_id");
        hasReviewed = getIntent().getExtras().getBoolean("has_reviwed");

        Log.i("ID=>",food_id);

        ImageView img =(ImageView)findViewById(R.id.img) ;
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("food_image");
        img.setImageBitmap(bitmap);

        qualityAdapter = ArrayAdapter.createFromResource(this,R.array.quality_array, android.R.layout.simple_spinner_item);
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qualitySpinner = (Spinner) findViewById(R.id.option);
        qualitySpinner.setAdapter(qualityAdapter);

//        qualitySpinner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                comment.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });

        qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((qualitySpinner.getItemAtPosition(position).equals("Select Quality"))){
                    selected = "";
                }else
                selected =qualitySpinner.getSelectedItem().toString();
                comment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button button =(Button)findViewById(R.id.submit);
       clear =(Button)findViewById(R.id.clear);
        clear.setVisibility(View.INVISIBLE);

//Get "hasReview" value. If the value doesn't exist yet false is returned


        if(hasReviewed){  //if hasreview then fetch data from server
            new AsyncFetch().execute(order_id);
            button.setText("UPDATE");
            qualitySpinner.setAdapter(qualityAdapter);
            clear =(Button)findViewById(R.id.clear);
            clear.setVisibility(View.VISIBLE);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncClear().execute(order_id);
                }
            });

        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String comment_=comment.getText().toString();

                if (selected.equals("")){
                    Toast.makeText(Feedback.this,"Please choose an option",Toast.LENGTH_LONG).show();
                }else {
                    new SubmitComment().execute(comment_, food_name, selected, date, food_id, order_id);
                }
            }
        });


    }

    public class SubmitComment extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Feedback.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tSending...");
            pdLoading.setCancelable(false);
            pdLoading.show();


        }

        @Override
        protected String doInBackground(String... params) {
            user_comment = params[0];
            comment_food = params[1];
            quality_choice =params[2];
            food_date= params[3];
            id = params[4];
            order_id =params[5];

            try {

                // Enter URL address where your php file resides
                url = new URL(ip_addrs+"/employee/orders/" + order_id+ "/review");
                //url = new URL("http://192.168.43.18/quantum/q.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                signin = getSharedPreferences(Signin.PREFS_NAME, 0);
                access_token = signin.getString("access_token","");
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject send_payload = new JSONObject();
                JSONObject user_comm = new JSONObject();
                try {
                    user_comm.put("comment",params[0]);
                    user_comm.put("quality",params[2].toLowerCase());
                    send_payload.put("params",user_comm);
               Log.i("POST =>" ,user_comm.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(send_payload.toString());
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                Log.i("JSONDATA", user_comm.toString());

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
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

                    Log.e("WhyNot", result.toString());

                    // Pass data to onPostExecute method
                    return(result.toString());

                }
                else if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED){
                    return ("noaccess");

                }

                else if(response_code == HttpURLConnection.HTTP_BAD_REQUEST){

                    return ("badrequest");

                }


                else{

                    return("false");

                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }


        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread

            pdLoading.dismiss();
            if((result.equalsIgnoreCase("badrequest"))){
                Toast.makeText(Feedback.this, "Error sending comment", Toast.LENGTH_LONG).show();
            }
            if(result.equalsIgnoreCase("noaccess")){
                SharedPreferences.Editor editor = signin.edit();
                editor.clear();

                AlertDialog alertDialog = new AlertDialog.Builder(Feedback.this).create();
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
            if(!(result.equalsIgnoreCase("false")))
            {
                Toast.makeText(Feedback.this, "Comment posted Thank you...", Toast.LENGTH_LONG).show();
                SharedPreferences settings = getSharedPreferences(Feedback.PREFS_NAME, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                //Set "hasReview" to true

                editor.putBoolean("is_reviewed",true);
                editor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Feedback.this, ViewOrders.class);
                        startActivity(intent);
                        Feedback.this.finish();
                    }
                }, 2000);
            }else  {

                // If username and password does not match display a error message
                Toast.makeText(Feedback.this, "Error sending comment", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Feedback.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            order_id =strings[0];
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
               // url = new URL("http://192.168.43.68:8069/employee/orders/" + order_id+ "/is_reviewed" );
                url = new URL(ip_addrs+"/employee/orders/" + order_id+ "/is_reviewed" );
                //url = new URL("http://192.168.43.18/quantum/qr.json");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                signin = getSharedPreferences(Signin.PREFS_NAME, 0);
                access_token = signin.getString("access_token","");
                // Setup HttpURLConnection class to send and receive data from odoo
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
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


            pdLoading.dismiss();


            if((result.equalsIgnoreCase("unsuccesful")))
            {
                Toast.makeText(Feedback.this, "Error fetching review..", Toast.LENGTH_LONG).show();
            }
            if((result.equalsIgnoreCase("nocontent")))
            {
                Toast.makeText(Feedback.this, "Error fetching review..", Toast.LENGTH_LONG).show();
            }
            if(result.equalsIgnoreCase("noaccess")){
                SharedPreferences.Editor editor = signin.edit();
                editor.clear();
                AlertDialog alertDialog = new AlertDialog.Builder(Feedback.this).create();
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
                   JSONObject response_data = new JSONObject(result);
                   String responseJson = response_data.getString("result");
                   JSONObject jsonResult= new JSONObject(responseJson);
                   JSONObject json_data = jsonResult.getJSONObject("review");
                    String r_quality = json_data.getString("quality");

                int a = 1;
                    for (int i=0;i< qualityAdapter.getCount();i++){
                        if (r_quality.trim().equals(qualitySpinner.getItemAtPosition(i).toString().toLowerCase())){
                            qualitySpinner.setSelection(i);
                            break;
                        }
                    }

                    String revew = json_data.getString("comment");
                comment.setVisibility(View.VISIBLE);
                    comment.setText(revew);


            } catch (JSONException e1) {
                e1.printStackTrace();
            }




        }

    }

    private class AsyncClear extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Feedback.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            order_id =strings[0];
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
               // url = new URL("http://192.168.43.68:5000/employee/orders/" + order_id+"/clear" );
                url = new URL(ip_addrs+"/employee/orders/" + order_id+"/clear" );
                //url = new URL("http://192.168.43.18/quantum/qr.json");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                signin = getSharedPreferences(Signin.PREFS_NAME, 0);
                access_token = signin.getString("access_token","");
                // Setup HttpURLConnection class to send and receive data from odoo
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setRequestMethod("POST");

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


            if((result.equalsIgnoreCase("unsuccesful")))
            {
                Toast.makeText(Feedback.this, "Error clearing  review..", Toast.LENGTH_LONG).show();
            }

            if(result.equalsIgnoreCase("noaccess")){
                SharedPreferences.Editor editor = signin.edit();
                editor.clear();
                AlertDialog alertDialog = new AlertDialog.Builder(Feedback.this).create();
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


            SharedPreferences sharedPreferences = getSharedPreferences(Feedback.PREFS_NAME, 0);
           sharedPreferences.edit().clear().apply();
            Toast.makeText(Feedback.this, "Comment cleared..", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Feedback.this, ViewOrders.class);
                    startActivity(intent);
                    Feedback.this.finish();
                }
            }, 2000);

        }

    }

}
