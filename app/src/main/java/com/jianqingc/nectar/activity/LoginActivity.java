package com.jianqingc.nectar.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jianqingc.nectar.R;
import com.jianqingc.nectar.controller.HttpRequestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onClickSignInButton(View view) {
        String tenantName = ((EditText) findViewById(R.id.tenantNameEditText)).getText().toString();
        String username = ((EditText) findViewById(R.id.usernameEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        //Test
        tenantName = "android-nectar";
        username = "j.chen99@student.unimelb.edu.au";
        password = "ODU0YmJmZWFjOWU5NjNj";
        //Test
        Log.i("tenantName: ", tenantName);
        Log.i("username: ", username);
        Log.i("password: ", password);
        HttpRequestController.getInstance(this).loginHttp(tenantName,username,password,this);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.mainLayout).setSoundEffectsEnabled(false);
        findViewById(R.id.passwordEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences("nectar_android", 0);
        String tokenExpires = sharedPreferences.getString("expires","1970-01-01T00:00:00Z");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Log.i("Expires",tokenExpires);
        try {
            Date tokenExpireTime = sdf.parse(tokenExpires);
            Date currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
            //Log.i("isSignedOut ",Boolean.toString(!sharedPreferences.getBoolean("isSignedOut", true)));
            // Log.i("isSignedOut2 ",sharedPreferences.getString("isSignedOut2", "Error"));
            //Log.i("isTimeAfter ",Boolean.toString((tokenExpireTime.after(currentTime))));
            if((!sharedPreferences.getBoolean("isSignedOut", true)) & (tokenExpireTime.after(currentTime))){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}


