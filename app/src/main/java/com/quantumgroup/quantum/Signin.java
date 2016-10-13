    package com.quantumgroup.quantum;

    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.annotation.Nullable;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.Toast;


    import com.pushbots.push.Pushbots;

    import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
    import org.jose4j.jwe.JsonWebEncryption;
    import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
    import org.jose4j.jwk.JsonWebKey;
    import org.jose4j.lang.JoseException;
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

    /**
     * Created by Mr. Vanson on 8/5/2016.
     */
    public class Signin  extends AppCompatActivity {
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        private EditText phone_editText;
        private EditText pin_editText;
        String phone;
        Integer status;

        String pin;
        String username;
        String profile_img;
        boolean img;
        String user_id;
        String acces_token;
        String responsePayload;
        String decrypt_accessToken;
        String fullname;
        String ip_address;

        //Give your SharedPreferences file a name and save it to a static variable
        public static final String PREFS_NAME = "SignIn";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            Pushbots.sharedInstance().init(this);
            this.phone_editText=(EditText)findViewById(R.id.phone);
            this.pin_editText = (EditText)findViewById(R.id.pin_code);
            // Get Reference to variables

             ip_address = getString(R.string.ip_address);

            SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0);
    //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

            if(hasLoggedIn){
               Intent intent = new Intent();
                intent.setClass(Signin.this, Quantum.class);
                startActivity(intent);
                Signin.this.finish();
            }

        }

        // Triggers when LOGIN Button clicked
        public void checkLogin(View arg0) {

            // Get text from email and passord field

            phone = phone_editText.getText().toString();
           pin = pin_editText.getText().toString();
            if (pin.equals("")||phone.equals("")){
                Toast.makeText(Signin.this,"Please all fields are required",Toast.LENGTH_LONG).show();
            }else {
                // Initialize  AsyncLogin() class with email and password
                new AsyncLogin().execute(phone, pin);
            }
        }

        public class AsyncLogin extends AsyncTask<String, String, String> {
            ProgressDialog pdLoading = new ProgressDialog(Signin.this);
            HttpURLConnection conn;
            URL url = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //this method will be running on UI thread
                pdLoading.setMessage("\tSigning in...");
                pdLoading.setCancelable(false);
                pdLoading.show();


            }

            @Override
            protected String doInBackground(String... params) {
                phone = params[0];
                pin = params[1];
                try {

                    // Enter URL address where your php file resides
                   // url = new URL("http://192.168.43.68:8069/authenticate");
                    url = new URL(ip_address+"/authenticate");

                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "exception";
                }
                try {
                    // Setup HttpURLConnection class to send and receive data from php and mysql
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setReadTimeout(READ_TIMEOUT);

                    conn.setConnectTimeout(CONNECTION_TIMEOUT);
                    conn.setRequestMethod("POST");


                    // setDoInput and setDoOutput method depict handling of both send and receive

                    conn.setDoOutput(true);
                    JSONObject user_info = new JSONObject();
                    JSONObject requestPayload = new JSONObject();
                    try {
                        user_info.put("phone_number",params[0]);
                        user_info.put("pin_code",params[1]);
                        requestPayload.put("params", user_info);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Append parameters to URL
                 //   Uri.Builder builder = new Uri.Builder()
                   //         .appendQueryParameter("cmpy", params[0])
                     //       .appendQueryParameter("staffId", params[1]);
                    //String query = builder.build().getEncodedQuery();

                    // Open connection for sending data
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

                    }else{

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

                if(!(result.equalsIgnoreCase("false")))
                {
                   try{

                    JSONObject json =new JSONObject(result);

                       responsePayload = json.getString("result");

                       JSONObject responseJson = new JSONObject(responsePayload);

                       if (responseJson.has("status")){
                           status = responseJson.getInt("status");

                           if (status == 200){
                               acces_token = responseJson.getString("access_token");
                               Log.i("Access token",acces_token.toString());
                               String jwkJson = "{\"kty\":\"oct\",\"k\":\"rHAQnzDkxNLEGFrVn8NNd2Jq9xfyjcrSmcxm9G6bo00\"}";
                               try {
                                   JsonWebKey jwk = JsonWebKey.Factory.newJwk(jwkJson);

                                   JsonWebEncryption receiverJwe = new JsonWebEncryption();
                                   receiverJwe.setCompactSerialization(acces_token);
                                   receiverJwe.setKey(jwk.getKey());
                                   decrypt_accessToken = receiverJwe.getPlaintextString();
                                   Log.e("Decrypted =>", decrypt_accessToken);

                               }
                               catch (JoseException e) {
                                   e.printStackTrace();
                               }
                               catch (NullPointerException e) {
                                   e.printStackTrace();
                               }

                               try {
                                   JSONObject json_data = new JSONObject(decrypt_accessToken);
                                   username = json_data.getString("username");
                                   fullname = json_data.getString("full_name");
                                   img = json_data.getBoolean("has_image");
                                   profile_img = json_data.getString("image");
                                   user_id = json_data.getString("id");
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                               catch (NullPointerException e) {
                                   e.printStackTrace();
                               }
                    /* Here launching another activity when login successful. If you persist login state
                    use sharedPreferences of Android. and logout button to clear sharedPreferences.
                     */
                               //User has successfully logged in, save this information
                               // We need an Editor object to make preference changes.


                               SharedPreferences settings = getSharedPreferences(Signin.PREFS_NAME, 0); // 0 - for private mode
                               SharedPreferences.Editor editor = settings.edit();

                               //Set "hasLoggedIn" to true
                               editor.putString("access_token",acces_token);
                               editor.putBoolean("hasLoggedIn", true);
                               editor.putString("username",username );
                               editor.putString("fullname",fullname);
                               editor.putBoolean("has_image",img);
                               editor.putString("user_id",user_id);
                               editor.putString("profile_img",profile_img);
                               //editor.putString("password",staffId);
                               // Commit the edits!
                               editor.commit();



                              Intent i = new Intent(getApplicationContext(),Quantum.class);
                              startActivity(i);



                           }else {
                               Toast.makeText(Signin.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();

                           }

                       }else{
                           Log.e("Warning","Bad Response Payload.. Status neede none given");
                           Toast.makeText(Signin.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                       }








                   } catch (JSONException e) {
                       e.printStackTrace();
                   }





                }else if (result.equalsIgnoreCase("false")){

                    // If username and password does not match display a error message


                } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("false")) {

                    Toast.makeText(Signin.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

                }
            }

        }


    }
