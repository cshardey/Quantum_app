package com.quantumgroup.quantum;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewOrders extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    TabLayout tabLayout;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private String week1="";
    private String week2="";
    private String week3="";
    private String week4="";
    private String week5="";
    String username;
    String profile_img;
    String user_id;
    String responseJson;
    Integer status;
    String ip_addrs;





    public static final String PREFS_NAME = "ViewOrders";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ip_addrs = getString(R.string.ip_address);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        initNavigationDrawer();
        new AsyncFetch().execute();

    }

    private void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.meal_today:
                        Intent i=new Intent(getApplication(),LunchHome.class);
                        startActivity(i);
                        break;
                    case R.id.order:
                        Intent order=new Intent(getApplication(),Order.class);
                        startActivity(order);
                        break;
                    case R.id.view:
                        Intent Vorder=new Intent(getApplication(),ViewOrders.class);
                        startActivity(Vorder);
                        break;



                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        CircleImageView profile =(CircleImageView) header.findViewById(R.id.profile_img);
        SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0);
        profile_img = settings.getString("profile_img","");
        boolean has_image = settings.getBoolean("has_image", false);

        if (has_image){
            byte[] imageAsBytes = Base64.decode(profile_img, Base64.DEFAULT);
            profile.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        username =settings.getString("fullname","");
        TextView username_text = (TextView)header.findViewById(R.id.tv_email);
        username_text.setText(username);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lunch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            switch (position){
                case 0:
                    Wk1 tab1 = new Wk1();
                    Bundle wk1= new Bundle();
                    wk1.putString("week",week1);
                    tab1.setArguments(wk1);
                    return tab1;
                case 1:
                    Wk1 tab2 = new Wk1();
                    Bundle wk2 = new Bundle();
                    wk2.putString("week",week2);
                    tab2.setArguments(wk2);
                    return tab2;
                case 2:
                    Wk1 tab3 = new Wk1();
                    Bundle wk3= new Bundle();
                    wk3.putString("week",week3);
                    tab3.setArguments(wk3);
                    return tab3;
                case 3:
                    Wk1 tab4 = new Wk1();
                    Bundle wk4= new Bundle();
                    wk4.putString("week",week4);
                    tab4.setArguments(wk4);
                    return tab4;
                case 4:
                    Wk1 tab5 = new Wk1();
                    Bundle wk5= new Bundle();
                    wk5.putString("week",week5);
                    tab5.setArguments(wk5);
                    return tab5;


                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return jsonArray.length();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return "Week "+jsonArray.getJSONObject(position).getString("week");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lunch, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        final String TAG = "AsyncTaskParseJson.java";
        ProgressDialog pdLoading = new ProgressDialog(ViewOrders.this);

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                 //url = new URL("http://192.168.43.68:8069/weeks?for=view_order");
                 url = new URL(ip_addrs+"/weeks?for=view_order");
              // url = new URL("http://192.168.43.18/quantum/v_weeks.json");

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

                }
                else if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED){
                    return ("noaccess");

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
                AlertDialog alertDialog = new AlertDialog.Builder(ViewOrders.this).create();
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
                     finish();
                    }
                });
                Window window = alertDialog.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();
                alertDialog.show();


            }
            if(result.equalsIgnoreCase("noaccess")){
                AlertDialog alertDialog = new AlertDialog.Builder(ViewOrders.this).create();
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
                        String payload_weeks= result_payload.getString("weeks");


                        JSONArray jsonArray = new JSONArray(payload_weeks);

                        ArrayList<String> date = new ArrayList<String>();
                        // Extract data from json and store into ArrayList as class objects

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);

                            tabLayout.addTab(tabLayout.newTab().setText(json.getString("week")));
                            date.add(json.getString("date"));


                        }



                        try {
                            week1= date.get(0);
                            week2 = date.get(1);
                            week3 = date.get(2);
                            week4 = date.get(3);
                            week5 = date.get(4);


                        }catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                        SharedPreferences settings = getSharedPreferences(ViewOrders.PREFS_NAME, 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("date1", week1); //commit date of week to editor  to populate ViewOrders Tab
                        editor.putString("date2", week2);
                        editor.putString("date3", week3);
                        editor.putString("date4", week4);
                        editor.putString("date5", week5);

                        editor.commit();

                        Log.i("DATE1",week1);


                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), jsonArray);

                        mViewPager.setAdapter(mSectionsPagerAdapter);

                        tabLayout.setupWithViewPager(mViewPager);
                    }else{
                        Log.e("JSON Parser", "Error parsing data ");
                    }
                }



            } catch (JSONException e) {
                //Toast.makeText(ViewOrders.this, "An error occured restart App", Toast.LENGTH_LONG).show();
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        }

    }


}
