package com.jianqingc.nectar.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jianqingc.nectar.activity.LoginActivity;
import com.jianqingc.nectar.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by scjc on 2016/10/2.
 */
public class HttpRequestController {
    private Context mApplicationContext;
    private static HttpRequestController mInstance;
    private SharedPreferences sharedPreferences;

    public interface VolleyCallback{
        void onSuccess(String result);
    }
    public static HttpRequestController getInstance(Context context){
        if (mInstance == null)
            mInstance = new HttpRequestController(context);
        return mInstance;

    }
    public HttpRequestController(Context context){
        this.mApplicationContext = context.getApplicationContext();
    }
    // loginHttp for Login activity

    public void loginHttp(String tenantName, String username, String password, final Context context ){
        sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
        String loginUri = "https://keystone.rc.nectar.org.au:5000/v2.0/tokens";
        JSONObject json0 = new JSONObject();
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        try {
            json2.put("username",username);
            json2.put("password",password);
            json1.put("tenantName",tenantName);
            json1.put("passwordCredentials",json2);
            json0.put("auth",json1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.POST, loginUri, json0, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getJSONObject("access").getJSONObject("token").getString("id");
                    //Log.i("token:", token);
                    ResponseParser.getInstance(mApplicationContext).loginParser(response);
                    String token2 = sharedPreferences.getString("tokenId","error");
                    //Log.i("token2:", token2);
                    Intent i = new Intent(mApplicationContext, MainActivity.class);
                    SharedPreferences sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isSignedOut",false);
                    //editor.putString("isSignedOut2","False");
                    editor.apply();
                    context.startActivity(i);
                    Toast.makeText(mApplicationContext, "Login Succeed", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401){
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Log.i("error", "onErrorResponse: ");
                Toast.makeText(mApplicationContext, "              Login Failed\nPlease check the required fields", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }
    //overview
    public void  listOverview(final Context context){
        sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL","Error Getting Compute URL");
        String fullURL = computeServiceURL + "/limits";
        final String token = sharedPreferences.getString("tokenId","Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respondJSON = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Display the first 500 characters of the response string.
                        Toast.makeText(mApplicationContext, "Listing limits Succeed", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401){
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Toast.makeText(mApplicationContext, "Listing limits Failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                return headers;
            }
        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(stringRequest);
    }
    //list instance
    public void  listInstance(final VolleyCallback callback, final Context context){
        sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL","Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/detail";
        final String token = sharedPreferences.getString("tokenId","Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray resultArray;
                        resultArray = ResponseParser.getInstance(mApplicationContext).listInstance(response);
                        String result =  resultArray.toString() ;
                        callback.onSuccess(result);
                        // Display the first 500 characters of the response string.
                        Toast.makeText(mApplicationContext, "Listing Instances Succeed", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401){
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Toast.makeText(mApplicationContext, "Listing Instances Failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                return headers;
            }
        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(stringRequest);
    }

//list volume
public void  listVolume(final VolleyCallback callback, final Context context){
    sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
    String volumeV2ServiceURL = sharedPreferences.getString("volumeV2ServiceURL","Error Getting volumeV2ServiceURL");
    String tenantId = sharedPreferences.getString("tenantId","Error Getting tenantId");
    String fullURL = volumeV2ServiceURL + "/volumes/detail";
    final String token = sharedPreferences.getString("tokenId","Error Getting Token");
    StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONArray resultArray;
                    resultArray = ResponseParser.getInstance(mApplicationContext).listVolume(response);
                    String result =  resultArray.toString() ;
                    callback.onSuccess(result);
                    // Display the first 500 characters of the response string.
                    Toast.makeText(mApplicationContext, "Listing Volume Succeed", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 401){
                Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mApplicationContext, LoginActivity.class);
                context.startActivity(i);
            }
            Toast.makeText(mApplicationContext, "Listing Instances Failed", Toast.LENGTH_SHORT).show();

        }
    }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("X-Auth-Token", token);
            return headers;
        }
    };
    NetworkController.getInstance(mApplicationContext).addToRequestQueue(stringRequest);
}
}
