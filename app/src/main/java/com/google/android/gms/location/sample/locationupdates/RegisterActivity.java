package com.google.android.gms.location.sample.locationupdates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.sample.locationupdates.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    String gender=""; String preference="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEditText = findViewById(R.id.Rusername);
        final EditText passwordEditText = findViewById(R.id.Rpassword);
        final EditText emailEditText = findViewById(R.id.Remail);
        final EditText displaynameEditText = findViewById(R.id.Rdisplayname);
        //final RadioButton genderEditText = findViewById(R.id.RgenderM);
        final Button registerButton = findViewById(R.id.button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postData(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        displaynameEditText.getText().toString()
                );
                //postData("sexygirl","password");



            }
        });

    }
    public void postData( String usernameEditText, String passwordEditText,
                          String emailEditText,String displaynameEditText
                           ) {
        Log.i(TAG, "postData: entered");

        final RadioGroup genderEditText = (RadioGroup) findViewById(R.id.radioGroup);
        if (genderEditText.getCheckedRadioButtonId() == -1)
        {
            // no radio buttons are checked
            Toast.makeText(getApplicationContext(), "Please select Gender", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // get selected radio button from radioGroup
            switch (genderEditText.getCheckedRadioButtonId()){
                case R.id.RgenderM:
                    gender = "M";
                    break;
                case R.id.RgenderF:
                    gender = "F";
                    break;

            }

            }
        final CheckBox preferenceEditText = findViewById(R.id.RpreferenceM);
        final CheckBox preferenceEditText1 = findViewById(R.id.RpreferenceF);
        if(preferenceEditText.isChecked()){
            preference+="Male";
        }
        if(preferenceEditText1.isChecked()){
            preference+="Female";
        }
        if(preferenceEditText.isChecked()&&preferenceEditText1.isChecked()){
            preference="Both";
        }
        else{
            Toast.makeText(getApplicationContext(), "Please select preference", Toast.LENGTH_SHORT).show();
        }
        //String urlAdress ="http://10.0.2.2:3000/account/login";
        String urlAdress ="http://192.168.2.125:3000/account/register";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlAdress);
                    Log.i("URL", url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    String message;
                    JSONObject json = new JSONObject();

                    JSONArray array = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("username", usernameEditText);
                    item.put("password", passwordEditText);
                    item.put("email", emailEditText);
                    JSONArray array1 = new JSONArray();
                    JSONObject item1 = new JSONObject();
                    item1.put("displayName", displaynameEditText);
                    item1.put("gender", gender);
                    item1.put("genderTarget", preference);
                    item1.put("biography", "Add your biography");//default empty

                    array.put(item);
                    array1.put(item1);

                    json.put("account", array);
                    json.put("profile", array1);

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
/*
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    Log.i("response" , line);
                    JSONObject jsonOutput = new JSONObject(line);
                    String accountUID = jsonOutput.getString("accountUID");
                    String profileUID = jsonOutput.getString("profileUID");
                    String token = jsonOutput.getString("token");

                    Log.i("responseINTOJSON" , accountUID +" "+profileUID +" "+token);

*/

                    conn.disconnect();
                    goToLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.RgenderM:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.RgenderF:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.RpreferenceM:
                if (checked){}
                // Put some meat on the sandwich
            else
                // Remove the meat
                break;
            case R.id.RpreferenceF:
                if (checked){}
                // Cheese me
            else
                // I'm lactose intolerant
                break;
            // TODO: Veggie sandwich
        }
    }

    private void goToLogin() {
        Log.i(TAG, "goTologin: ");
        //
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}
