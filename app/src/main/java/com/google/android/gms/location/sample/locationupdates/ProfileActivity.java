package com.google.android.gms.location.sample.locationupdates;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the transferred data from source activity.
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        String profileUID = intent.getStringExtra("profileUID");
        String accountUID = intent.getStringExtra("accountUID");
        final Button update = findViewById(R.id.button);

        Log.i(TAG, "onCreate: data from intent"+profileUID+" "+accountUID+" "+token);
        postData(  accountUID,  token, profileUID);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 updateprofile(accountUID,  token, profileUID);



                    }
                });







    }
    public void updateprofile( String accountUID, String token, String profileUID) {
        EditText displaynameEditText = findViewById(R.id.Rdisplayname);
        EditText biographyEditText = findViewById(R.id.biography);
        String genderTarget=""; String gender="";
        RadioButton genderEditTextM = findViewById(R.id.RgenderM);
        RadioButton genderEditTextF = findViewById(R.id.RgenderF);
        if(genderEditTextM.isChecked()){
            gender = "M";
        }
        else if(genderEditTextF.isChecked()){
            gender = "F";
        }


        CheckBox preferenceEditText = findViewById(R.id.RpreferenceM);
        Log.i(TAG, "gendercheck: "+preferenceEditText.isChecked());
        CheckBox preferenceEditText1 = findViewById(R.id.RpreferenceF);
        Log.i(TAG, "gendercheck: "+preferenceEditText1.isChecked());

        if(preferenceEditText.isChecked()){
            preferenceEditText.setChecked(true);
            genderTarget ="Male";
        }
        if(preferenceEditText1.isChecked()){
            preferenceEditText1.setChecked(true);
            genderTarget ="Female";
        }
        if(preferenceEditText.isChecked()&& preferenceEditText1.isChecked()){
            preferenceEditText.setChecked(true);
            preferenceEditText1.setChecked(true);
            genderTarget ="Both";
        }
        Log.i(TAG, "postData: entered");
        String urlAdress ="http://192.168.2.125:3000/profile/update-profile";
        String finalGenderTarget = genderTarget;
        String finalGender = gender;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlAdress);
                    Log.i("URL", url.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Authorization", "Basic " + token);
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    String message;
                    JSONObject json = new JSONObject();

                    JSONArray array = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("accountUID", accountUID);
                    item.put("displayName", displaynameEditText.getText());
                    item.put("biography", biographyEditText.getText());
                    item.put("genderTarget", finalGenderTarget);
                    item.put("gender",finalGender );


                    array.put(item);

                    json.put("profile", array);

                    message = json.toString();
                    message = message.replaceAll("\\[", "").replaceAll("\\]","");
                    Log.i("message ", message);


                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    //Log.i("URL+Req", url+"?username="+username+"password="+password);
                    os.writeBytes(message);


                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }

    public void postData( String accountUID, String token, String profileUID) {
        Log.i(TAG, "postData: entered");
        String urlAdress ="http://192.168.2.125:3000/profile/self-profile";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlAdress);
                    Log.i("URL", url.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Authorization", "Basic " + token);
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    String message;
                    JSONObject json = new JSONObject();

                    JSONArray array = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("accountUID", accountUID);


                    array.put(item);

                    json.put("profile", array);

                    message = json.toString();
                    message = message.replaceAll("\\[", "").replaceAll("\\]","");
                    Log.i("message ", message);


                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    //Log.i("URL+Req", url+"?username="+username+"password="+password);
                    os.writeBytes(message);


                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    Log.i("response" , line);
                    JSONObject jsonOutput = new JSONObject(line);


                    String displayName = jsonOutput.getString("displayName");
                    String gender = jsonOutput.getString("gender");
                    String genderTarget = jsonOutput.getString("genderTarget");
                    //Arr profileImages = jsonOutput.getString("token");
                    String biography = jsonOutput.getString("biography");

                    Log.i("responseFields" , accountUID +" "+profileUID
                            +" "+token+" "+biography+ gender + genderTarget);




                    final EditText displaynameEditText = findViewById(R.id.Rdisplayname);
                    final  EditText biographyEditText = findViewById(R.id.biography);

                    final RadioGroup genderEditText = (RadioGroup) findViewById(R.id.radioGroup);
                    final RadioButton genderEditTextM = findViewById(R.id.RgenderM);
                    final RadioButton genderEditTextF = findViewById(R.id.RgenderF);


                    displaynameEditText.setText(displayName);
                    biographyEditText.setText(biography);

                    if (gender.equals("F"))
                    {
                        // no radio buttons are checked
                        genderEditTextF.setChecked(true);

                    }
                    else if (gender.equals("M"))
                    {
                        // no radio buttons are checked
                        genderEditTextM.setChecked(true);
                    }
                    final CheckBox preferenceEditText = findViewById(R.id.RpreferenceM);
                    final CheckBox preferenceEditText1 = findViewById(R.id.RpreferenceF);

                    if(genderTarget.equals("Male")){
                        preferenceEditText.setChecked(true);
                    }
                    if(genderTarget.equals("Female")){
                        preferenceEditText1.setChecked(true);
                    }
                    if(genderTarget.equals("Both")){
                        preferenceEditText.setChecked(true);
                        preferenceEditText1.setChecked(true);
                    }



                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }}


