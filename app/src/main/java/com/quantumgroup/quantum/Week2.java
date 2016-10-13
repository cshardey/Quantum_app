package com.quantumgroup.quantum;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;
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
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Week2 extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private static final String TAG_WEEK = "weekinfo";
    private static final String TAG_DAY = "day";
    private String date1=""; // date for first tab activity
    private String date2="";
    private String date3="";
    private String date4="";
    private String date5="";
    String ip_addrs;

    String access_token;
    String responseJson;
    Integer status;




    public static final String PREFS_NAME = "Week2";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    TabLayout tabLayout;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
   ViewPager mViewPager;
 private  int week;
    private int active_week;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ip_addrs = getString(R.string.ip_address);
        try{
            week = getIntent().getExtras().getInt("week");
           // SharedPreferences active_week = getSharedPreferences(Week2.PREFS_NAME, 0); // 0 - for private mode
           // SharedPreferences.Editor editor = active_week.edit();
            //editor.putInt("active-week",week);

            active_week = week + 1;


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        new AsyncFetch().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            try {
                senddatatoserver();
               Intent intent=new Intent(this,Week2.class);
                intent.putExtra("week",week);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_week2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private JSONArray jsonArray;

        public SectionsPagerAdapter(FragmentManager fm, JSONArray jsonArray) {
            super(fm);
            this.jsonArray = jsonArray;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Monday tab1 = new Monday();
                    return tab1;

                case 1:
                    Tuesday tab2 = new Tuesday();
                    return tab2;
                case 2:
                    Wednesday tab3 = new Wednesday();
                    return tab3;
                case 3:
                    Thursday tab4 = new Thursday();
                    return tab4;
                case 4:
                    Friday tab5 = new Friday();
                    return tab5;


                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return jsonArray.length();
        }

        @Override
        public CharSequence getPageTitle(int position) {


            try {
                return jsonArray.getJSONObject(position).getString("day");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        final String TAG = "AsyncTaskParseJson.java";
        ProgressDialog pdLoading = new ProgressDialog(Week2.this);

        @Override
        protected String doInBackground(String... strings) {
            try {


                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                //url = new URL("http://192.168.43.18/quantum/lunchweek.json");
                // I will need to make soem change here with the URL
                //url = new URL("http://192.168.43.68:8069/weeks/" + active_week + "/days?for=order_meal");
                url = new URL(ip_addrs+"/weeks/" + active_week + "/days?for=order_meal");

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

                } else {

                    return ("unsuccessful");
                }
            } catch (ConnectException e) {
                System.out.println("More than "  + " elapsed.");

                return ("timeout");
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
            //this method will be running on UI thread
            if(result.equalsIgnoreCase("timeout")){
                AlertDialog alertDialog = new AlertDialog.Builder(Week2.this).create();
                alertDialog.setTitle("Connection Timeout");
                alertDialog.setMessage("Took to long establishing connection to server");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncFetch().execute();
                            }
                        });
                alertDialog.show();


            }

            try {
                JSONObject payload_data = new JSONObject(result);
                responseJson = payload_data.getString("result");
                JSONObject result_payload = new JSONObject(responseJson);
                if (result_payload.has("status")){
                    status = result_payload.getInt("status");
                    if (status == 200){
                        String days_in_week = result_payload.getString("days_in_week");
                        JSONArray jsonArray = new JSONArray(days_in_week);
                        ArrayList<String> date = new ArrayList<String>();
                        // Extract data from json and store into ArrayList as class objects



                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);

                            tabLayout.addTab(tabLayout.newTab().setText(json.getString("day")));
                            date.add(json.getString("date"));


                        }

                        SharedPreferences settings = getSharedPreferences(Week2.PREFS_NAME, 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();

                        try {
                            date1 = date.get(0);
                            date2 = date.get(1);
                            date3 = date.get(2);
                            date4 = date.get(3);
                            date5 = date.get(4);

                        }catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                        editor.putString("date1", date1); //commit date to editor
                        editor.putString("date2", date2);
                        editor.putString("date3", date3);
                        editor.putString("date4", date4);
                        editor.putString("date5", date5);

                        editor.commit();


                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), jsonArray);

                        mViewPager.setAdapter(mSectionsPagerAdapter);

                        tabLayout.setupWithViewPager(mViewPager);


                    }else{

                    }
                }




            } catch (JSONException e) {
                //Toast.makeText(Week2.this, e.toString(), Toast.LENGTH_LONG).show();
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        }

    }

    public void senddatatoserver() throws JSONException {


        SharedPreferences day1 = getSharedPreferences(Monday.PREFS_NAME, 0);//retrieve values from first day
        String food_id1 = day1.getString("id", "");
        String food_date1 = day1.getString("date", "");
        String custom_meal1 = day1.getString("custom_meal","");

        SharedPreferences day2 = getSharedPreferences(Tuesday.TUESDAY_MEAL, 0);//retrieve values from second day
        String food_id2 = day2.getString("id", "");
        String food_date2 = day2.getString("date", "");
        String custom_meal2 = day2.getString("custom_meal","");

        SharedPreferences day3 = getSharedPreferences(Wednesday.PREFS_NAME, 0);//retrieve values from third day
        String food_id3 = day3.getString("id", "");
        String food_date3 = day3.getString("date", "");
        String custom_meal3 = day3.getString("custom_meal","");

        SharedPreferences day4 = getSharedPreferences(Thursday.PREFS_NAME, 0);//retrieve values from third day
        String food_id4 = day4.getString("id", "");
        String food_date4 = day4.getString("date", "");
        String custom_meal4 = day4.getString("custom_meal","");

        SharedPreferences day5 = getSharedPreferences(Friday.PREFS_NAME, 0);//retrieve values from third day
        String food_id5 = day5.getString("id", "");
        String food_date5 = day5.getString("date", "");
        String custom_meal5 = day5.getString("custom_meal","");

        JSONArray elements = new JSONArray();
        JSONObject aux;
        //CHECKS IF USER SELECTED ALL MEALS ELSE GIVE IT A NULL

         if (food_id1.equals("")&& custom_meal1.equals("")){}
         else{
             if (!(food_id1.equals(""))){
             aux = new JSONObject().put("id", food_id1);
             aux.put("date", food_date1);
             elements.put(aux);
            }
             else{
                 aux = new JSONObject().put("is_custom", true);
                 aux.put("custom_meal",custom_meal1);
                 aux.put("date", food_date1);
                 elements.put(aux);
             }

         }

        if (food_id2.equals("")&& custom_meal2.equals("")){}
        else {
            if (!(food_id2.equals(""))){
            aux = new JSONObject().put("id", food_id2);
            aux.put("date", food_date2);
            elements.put(aux);}
            else {
                aux = new JSONObject().put("is_custom", true);
                aux.put("custom_meal",custom_meal2);
                aux.put("date", food_date2);
                elements.put(aux);}
            }


        if (food_id3.equals("")&& custom_meal3.equals("")){}
        else {
            if (!(food_id3.equals(""))){
            aux = new JSONObject().put("id", food_id3);
            aux.put("date", food_date3);
            elements.put(aux);}
            else{
                aux = new JSONObject().put("is_custom", true);
                aux.put("custom_meal",custom_meal3);
                aux.put("date", food_date3);
                elements.put(aux);
            }
        }
        if (food_id4.equals("") && custom_meal4.equals("")){}
        else {
            if (!(food_id4.equals(""))) {
                aux = new JSONObject().put("id", food_id4);
                aux.put("date", food_date4);
                elements.put(aux);
            } else {
                aux = new JSONObject().put("is_custom",true);
                aux.put("custom_meal",custom_meal4);
                aux.put("date", food_date4);
                elements.put(aux);
            }
        }

        if (food_id5.equals("")&& custom_meal5.equals("")){}
        else {
            if(!(food_id5.equals(""))) {
                aux = new JSONObject().put("id", food_id5);
                aux.put("date", food_date5);
                elements.put(aux);
            }else {
                aux = new JSONObject().put("is_custom", true);
                aux.put("custom_meal",custom_meal5);
                aux.put("date", food_date5);
                elements.put(aux);
            }
        }

        if (elements.length()==0){
            Toast.makeText(Week2.this, "Please select your meal", Toast.LENGTH_LONG).show();

        }
        else {
            //JSONObject food_list =new JSONObject().put("userlist",elements.toString());
            SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0);
            String user_id = settings.getString("user_id", "");


            JSONObject finalObject = new JSONObject();

            finalObject.put("food_list", elements);

            JSONObject send_payload = new JSONObject();
            send_payload. put("params",finalObject);



           // finalObject.put("user_id", user_id);


            Log.e("mainToPost", "mainToPost" + send_payload.toString());


            new AsyncSubmitFetch().execute(String.valueOf(send_payload));
        }
        //call to async class
    }

    private class AsyncSubmitFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Week2.this);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... strings) {

            String food_list = strings[0];


            try {
                SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0);

                access_token = settings.getString("access_token","");
                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
              // url = new URL("http://192.168.43.18/quantum/req.php");
               // url = new URL("http://192.168.43.68:8069/employee/foods");
                url = new URL(ip_addrs+"/employee/foods");

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
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.connect();


                // String postKey = "food_list=" + JsonDATA; add key tp JSON post


                Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(food_list.toString());
                writer.flush();
                Log.i("JSONDATA", food_list);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == 400) {
                    String err = "400 error";
                    Log.i("400 eror", err);
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        return result.toString();
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
                    return (result.toString());
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
            pdLoading.setMessage("\tSaving...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                pdLoading.dismiss();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
        }
            //clears submited food id from shared pref of user selected meals
            SharedPreferences day1 = getSharedPreferences(Monday.PREFS_NAME, 0);
            day1.edit().remove("id").apply();
            day1.edit().remove("custom_meal").apply();

            SharedPreferences day2 = getSharedPreferences(Tuesday.TUESDAY_MEAL, 0);
            day2.edit().remove("id").apply();
            day2.edit().remove("custom_meal").apply();

            SharedPreferences day3 = getSharedPreferences(Wednesday.PREFS_NAME, 0);
            day3.edit().remove("id").apply();
            day3.edit().remove("custom_meal").apply();

            SharedPreferences day4 = getSharedPreferences(Thursday.PREFS_NAME, 0);
            day4.edit().remove("id").apply();
            day4.edit().remove("id").apply();

            SharedPreferences day5 = getSharedPreferences(Friday.PREFS_NAME, 0);
            day5.edit().remove("id").apply();
            day5.edit().remove("custom_meal").apply();



            if (result != "400") {
                try {

                    ArrayList<String> cr_id = new ArrayList<>();  // holds id of meals returnd from server
                    ArrayList<String> cr_date = new ArrayList<>(); // holds date of meals returned from server
                    JSONObject result_payload = new JSONObject(result);
                    String responsePayload = result_payload.getString("result");
                    JSONObject results_paylaod = new JSONObject(responsePayload);
                    String created_food = results_paylaod.getString("created_food");
                    JSONArray jsonArray = new JSONArray(created_food);

                    Log.i("created_food",jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);

                        cr_id.add(json.getString("id"));
                        cr_date.add(json.getString("date"));



                    }
                    SharedPreferences createdId = getSharedPreferences(Week2.PREFS_NAME, 0); // 0 - for private mode
                    SharedPreferences.Editor editor = createdId.edit();
                    Gson gson = new Gson();
                try {

                    createdId.getString("created_id","");
                    ArrayList<String> emptyList = new ArrayList<>();
                    String update_id;
                    String update_date;
                    if (createdId.contains("created_id")) {
                        update_id = createdId.getString("created_id", "");
                    } else {
                        update_id = gson.toJson(emptyList);
                    }

                    if (createdId.contains("created_date")){
                        update_date = createdId.getString("created_date", "");
                    } else {
                        update_date = gson.toJson(emptyList);
                    }

                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    ArrayList<String> existing_id = gson.fromJson(update_id, type);
                    ArrayList<String> existing_date = gson.fromJson(update_date, type);

                    for (int i=0; i < cr_id.size();i++){
                        Boolean found = false;
                        Log.i("Looping",cr_date.get(i).toString());
                            for (int j=0; j < existing_id.size();j++){
                                if (cr_date.get(i).toString().equals(existing_date.get(j).toString())){
                                    found =  true;
                                    if (cr_id.get(i).toString().equals(existing_id.get(j).toString())){
                                        continue;
                                    }else{
                                        existing_id.remove(j);
                                        existing_date.remove(j);
//                                        existing_id.set(j, cr_id.get(i).toString());
                                        existing_id.add(cr_id.get(i).toString());
                                        existing_date.add(cr_date.get(i).toString());
                                        Log.i("Adding to shared pref",cr_date.get(i).toString());

                                    }
                                }
                            }

                        if (!found) {
                            // Order date does not already exist in shared pref
                            existing_id.add(cr_id.get(i).toString());
                            existing_date.add(cr_date.get(i).toString());
                            Log.i("Adding  Shared NotFound",cr_date.get(i).toString());

                        }

                    }


                    String created_id = gson.toJson(existing_id);
                    String created_date = gson.toJson(existing_date);
                    editor.putString("created_id", created_id);
                    editor.putString("created_date",created_date);
                    editor.commit();


//                        id1 = cr_id.get(0); cr_date1=cr_date.get(0);
//                        id2 =cr_id.get(1); cr_date2=cr_date.get(1);
//                        id3 =cr_id.get(2);cr_date3=cr_date.get(2);
//                        id4 =cr_id.get(3);cr_date4=cr_date.get(3);
//                        id5 =cr_id.get(4);cr_date5=cr_date.get(4);
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }

//                    editor.putString("id1",id1);  editor.putString("cr_date1",cr_date1);
//
//                    editor.putString("id2",id2);  editor.putString("cr_date2",cr_date2);
//                    editor.putString("id3",id3);  editor.putString("cr_date3",cr_date3);
//                    editor.putString("id4",id4);  editor.putString("cr_date4",cr_date4);
//                    editor.putString("id5",id5);  editor.putString("cr_date5",cr_date5);
//
//                    editor.commit();



                     Log.i("Dates",cr_id.toString());

                    //SharedPreferences createdId = getSharedPreferences(Week2.PREFS_NAME, 0);//retrieve values from first day
                   // SharedPreferences.Editor editor = createdId.edit();
                  //  editor.putString("createdId", jsonArray.toString());//add returned created meal to shared peref
                    //Log.i("Shared Array",jsonArray.toString());
                   // editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
            }

        }
    }
}