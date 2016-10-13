    package com.quantumgroup.quantum;

    import android.annotation.SuppressLint;
    import android.app.ProgressDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.Configuration;
    import android.graphics.BitmapFactory;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.annotation.Nullable;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.NavigationView;
    import android.support.design.widget.Snackbar;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.GridLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
    import android.util.Base64;
    import android.util.Log;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.ImageView;
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
    import java.net.SocketTimeoutException;
    import java.net.URL;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class LunchHome extends AppCompatActivity {
        private ActionBarDrawerToggle actionBarDrawerToggle;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        private DrawerLayout drawerLayout;
        TextView food;
        ImageView image;
        Toolbar toolbar;
        String username;
        String profile_img;
        String user_id;
        private static final String TAG_FOOD = "name";
        private static final String TAG_IMAGE = "total";
        private String food_rec;
        private String food_img;
        String acces_token;
        String responseJson;
        Integer status;
        SharedPreferences settings;
        String  ip_addrs;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lunch_home);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            food = (TextView)findViewById(R.id.textView);
            image = (ImageView)findViewById(R.id.meal_today) ;

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            ip_addrs = getString(R.string.ip_address);


            initNavigationDrawer();
            new AsyncFetch().execute();

        }
        private void initNavigationDrawer() {
            NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    int id = item.getItemId();
                    switch (id){
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
            actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
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
            ProgressDialog pdLoading = new ProgressDialog(LunchHome.this);
            HttpURLConnection conn;
            URL url = null;

            @Override
            protected String doInBackground(String... strings) {
                try {
                 settings = getSharedPreferences(Signin.PREFS_NAME, 0);
                    //String user_id = settings.getString("user_id", "");
                     acces_token = settings.getString("access_token","");
                    // Enter URL address where your json file resides
                    // Even you can make call to php file which returns json data
                    //url = new URL("http://192.168.43.68:5000/employee/foods/today?user_id=" + user_id);
                   // url = new URL("http://192.168.43.68:8069/employee/foods/today");
                    url = new URL(ip_addrs+"/employee/foods/today");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return e.toString();
                }
                try {

                    // Setup HttpURLConnection class to send and receive data from php and mysql
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + acces_token);
                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setConnectTimeout(CONNECTION_TIMEOUT);
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

                } catch (java.net.SocketTimeoutException e) {


                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return e1.toString();
                }
                try {

                    int response_code = conn.getResponseCode();

                    // Check if successful connection made
                    if (response_code == 200) {

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




                    }else {

                        return ("unsuccessful");
                    }
                } catch (ConnectException e) {
                    System.out.println("More than "  + " elapsed.");

                    return ("timeout");
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.toString();
                }
                finally {
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
                    food.setText("Food not available for this day");
                }
    //            if(result.equalsIgnoreCase("noaccess")){
    //                SharedPreferences.Editor editor = settings.edit();
    //                editor.clear();
    //
    //                AlertDialog alertDialog = new AlertDialog.Builder(LunchHome.this).create();
    //                    alertDialog.setTitle("Unauthorized Access");
    //                    alertDialog.setMessage("You don't have permission to use Quantum app\n Contact Administrator" );
    //                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT",
    //                            new DialogInterface.OnClickListener() {
    //                                public void onClick(DialogInterface dialog, int which) {
    //                                    System.exit(0);
    //                                }
    //                            });
    //                    Window window = alertDialog.getWindow();
    //                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
    //                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    //                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    //                    alertDialog.show();
    //                }
    //
    //
    //            if(result.equalsIgnoreCase("timeout")){
    //                AlertDialog alertDialog = new AlertDialog.Builder(LunchHome.this).create();
    //                alertDialog.setTitle("Connection Timeout");
    //                alertDialog.setMessage("Took to long establishing connection to server");
    //                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
    //                        new DialogInterface.OnClickListener() {
    //                            public void onClick(DialogInterface dialog, int which) {
    //                                new AsyncFetch().execute();
    //                            }
    //                        });
    //                Window window = alertDialog.getWindow();
    //                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
    //                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    //                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    //                alertDialog.show();
    //
    //
    //            }
    //


                try {

                    JSONObject payload_data = new JSONObject(result);
                        responseJson = payload_data.getString("result");
                        JSONObject json_data = new JSONObject(responseJson);

                        if (json_data.has("status")){
                            status = json_data.getInt("status");
                            if(status == 200){
                                String food_json;
                                food_json = json_data.getString("food");

                                JSONObject food_data = new JSONObject(food_json);
                                food_rec= food_data.getString("name");
                                food_img = food_data.getString("image");
                                food.setText(food_rec);
                                try {
                                    byte[] imageAsBytes = Base64.decode(food_img, Base64.DEFAULT);
                                    image.setImageBitmap(
                                            BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                                }catch (IllegalArgumentException im){
                                    im.printStackTrace();
                                }
                            }else if(status == 204){
                                food.setText("Food not available for this day");
                            }else{
                                Log.e("Error",result.toString());
                            }
                        }


                  //  }



                } catch (JSONException e) {
                    Log.e("AsyncTaskError =>", e.toString());
    //                AlertDialog alertDialog = new AlertDialog.Builder(LunchHome.this).create();
    //                alertDialog.setTitle("Connection Timeout");
    //                alertDialog.setMessage("Took to long establishing connection");
    //                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "RETRY",
    //                        new DialogInterface.OnClickListener() {
    //                            public void onClick(DialogInterface dialog, int which) {
    //                                new AsyncFetch().execute();
    //                            }
    //                        });
    //                alertDialog.show();
    //
    //            }
                }

            }

        }
    }
