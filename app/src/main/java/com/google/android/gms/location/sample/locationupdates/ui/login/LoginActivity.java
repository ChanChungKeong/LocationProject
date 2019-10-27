package com.google.android.gms.location.sample.locationupdates.ui.login;

import android.accounts.Account;
import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.sample.locationupdates.ProfileActivity;
import com.google.android.gms.location.sample.locationupdates.R;
import com.google.android.gms.location.sample.locationupdates.RegisterActivity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final TextView rowTextView = findViewById(R.id.LinkRegister);


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());




                postData(usernameEditText.getText().toString(),passwordEditText.getText().toString());
                //postData("sexygirl","password");



            }
        });
        rowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister(v);


            }
        });
    }

    public void goToRegister(View view) {
        Log.i(TAG, "goToRegister: ");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }
    public void postData( String username, String password) {
        Log.i(TAG, "postData: entered");
        //String urlAdress ="http://10.0.2.2:3000/account/login";
        String urlAdress ="http://192.168.2.125:3000/account/login";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlAdress);
                    Log.i("URL", url.toString());
                    Log.i("username and password ", username+" "+password);
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
                    item.put("username", username);
                    item.put("password", password);

                    array.put(item);

                    json.put("account", array);

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
                    String accountUID = jsonOutput.getString("accountUID");
                    String profileUID = jsonOutput.getString("profileUID");
                    String token = jsonOutput.getString("token");

                    Log.i("responseFields" , accountUID +" "+profileUID +" "+token);



                    conn.disconnect();
                    goToProfile(accountUID,profileUID,token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }




    private void goToProfile( String accountUID,String profileUID, String token) {
        Log.i(TAG, "goToProfile: ");
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("accountUID",accountUID);
        intent.putExtra("profileUID",profileUID);
        intent.putExtra("token",token);
        startActivity(intent);

    }





    private void updateUiWithUser(LoggedInUserView model) {
        //String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience

    }

    private void showLoginFailed(@StringRes Integer errorString) {

    }
}
