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
 * Created by Jianqing Chen on 2016/10/2.
 */
public class HttpRequestController {
    private Context mApplicationContext;
    private static HttpRequestController mInstance;
    private SharedPreferences sharedPreferences;

    public interface VolleyCallback {
        void onSuccess(String result);

    }

    public static HttpRequestController getInstance(Context context) {
        if (mInstance == null)
            mInstance = new HttpRequestController(context);
        return mInstance;

    }

    public HttpRequestController(Context context) {
        this.mApplicationContext = context.getApplicationContext();
    }

    /**
     * Login Http Request sent to Keystone.
     * @param tenantName
     * @param username
     * @param password
     * @param context
     */

    public void loginHttp(String tenantName, String username, String password, final Context context) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String loginUri = "https://keystone.rc.nectar.org.au:5000/v2.0/tokens";
        /**
         * Assemble Json Object According to NeCTAR API documentation
         */
        JSONObject json0 = new JSONObject();
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        try {
            json2.put("username", username);
            json2.put("password", password);
            json1.put("tenantName", tenantName);
            json1.put("passwordCredentials", json2);
            json0.put("auth", json1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUri, json0, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseParser.getInstance(mApplicationContext).loginParser(response);
                Intent i = new Intent(mApplicationContext, MainActivity.class);
                SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                /**
                 * Enable auto-login function
                 */
                editor.putBoolean("isSignedOut", false);
                editor.apply();
                context.startActivity(i);
                Toast.makeText(mApplicationContext, "Login Succeed", Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Log.i("error", "onErrorResponse: ");
                Toast.makeText(mApplicationContext, "              Login Failed\nPlease check the required fields", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * List Overview Http Request
     * Pass the String response to Overview Fragment. Overview Fragment can then draw graphs based on the response.
     * @param callback
     * @param context
     */
    public void listOverview(final VolleyCallback callback,final Context context) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/limits";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(mApplicationContext, "Listing limits Succeed", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Toast.makeText(mApplicationContext, "Listing limits Failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            /**
             * Set Token inside  the Http Request Header
             * @return
             * @throws AuthFailureError
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                return headers;
            }
        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(stringRequest);
    }

    /**
     * List Instance Http Request showing the servers detail
     * @param callback
     * @param context
     */
    public void listInstance(final VolleyCallback callback, final Context context) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/detail";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray resultArray;
                        resultArray = ResponseParser.getInstance(mApplicationContext).listInstance(response);
                        String result = resultArray.toString();
                        callback.onSuccess(result);
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                } else {
                    Toast.makeText(mApplicationContext, "Listing Instances Failed", Toast.LENGTH_SHORT).show();
                }
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

    /**
     * List Instance Detail for the specific instance the user clicks in the InstanceFragment listview
     * @param callback
     * @param context
     * @param instanceId
     */
    public void listSingleInstance(final VolleyCallback callback, final Context context, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId;
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /**
                         *  Pass the response of HTTP request to the Fragment
                         *  Server ID, AZ,IP address, Name, and Status
                         */
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONObject result = new JSONObject();
                            String id = resp.getJSONObject("server").getString("id");
                            String zone = resp.getJSONObject("server").getString("OS-EXT-AZ:availability_zone");
                            String address = resp.getJSONObject("server").getString("accessIPv4");
                            String name = resp.getJSONObject("server").getString("name");
                            String status = resp.getJSONObject("server").getString("status");
                            result.put("id", id);
                            result.put("zone", zone);
                            result.put("address", address);
                            result.put("name", name);
                            result.put("status", status);
                            String stringResult = result.toString();
                            callback.onSuccess(stringResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Toast.makeText(mApplicationContext, "Listing Instances Failed", Toast.LENGTH_SHORT).show();
                callback.onSuccess("error");
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

    /**
     * List Volume Http Request
     * @param callback
     * @param context
     */
    public void listVolume(final VolleyCallback callback, final Context context) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String volumeV2ServiceURL = sharedPreferences.getString("volumeV2ServiceURL", "Error Getting volumeV2ServiceURL");
        String fullURL = volumeV2ServiceURL + "/volumes/detail";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray resultArray;
                        resultArray = ResponseParser.getInstance(mApplicationContext).listVolume(response);
                        String result = resultArray.toString();
                        callback.onSuccess(result);
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mApplicationContext, LoginActivity.class);
                    context.startActivity(i);
                }
                Toast.makeText(mApplicationContext, "Listing Volumes Failed", Toast.LENGTH_SHORT).show();

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

    /**
     * Server action: Pause Http Request
     * @param callback
     * @param instanceId
     */
    public void pause(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("pause", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /**
                 * Successful Response is null so we have to separate it from the real Errors
                 */
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                        }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                        }
                    }
                }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     *  Server action: Unpause Http Request
     * @param callback
     * @param instanceId
     */
    public void unpause(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("unpause", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }


    /**
     *  Server action: Stop Http Request
     * @param callback
     * @param instanceId
     */
    public void stop(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("os-stop", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }


    /**
     *  Server action: Pause Start Request
     * @param callback
     * @param instanceId
     */
    public void start(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("os-start", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     *  Server action: Suspend Http Request
     * @param callback
     * @param instanceId
     */
    public void suspend(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("suspend", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }


    /**
     *  Server action: Resume Http Request
     * @param callback
     * @param instanceId
     */
    public void resume(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("resume", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     *  Server action: Reboot Http Request
     * @param callback
     * @param instanceId
     */
    public void reboot(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        try {
            json2.put("type", "HARD");
            json1.put("reboot",json2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     *  Server action: Delete Http Request
     * @param callback
     * @param instanceId
     */
    public void delete(final VolleyCallback callback, String instanceId) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json = new JSONObject();
        try {
            json.put("forceDelete",JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }

    /**
     *  Server action: Snapshot Http Request
     * @param callback
     * @param instanceId
     * @param snapshotName
     */
    public void snapshot(final VolleyCallback callback, String instanceId, String snapshotName) {
        sharedPreferences = mApplicationContext.getSharedPreferences("nectar_android", 0);
        String computeServiceURL = sharedPreferences.getString("computeServiceURL", "Error Getting Compute URL");
        String fullURL = computeServiceURL + "/servers/" + instanceId + "/action";
        final String token = sharedPreferences.getString("tokenId", "Error Getting Token");
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONObject json3 = new JSONObject();
        try {
            json3.put("meta_var", "meta_val");
            json2.put("metadata",json3);
            json2.put("name",snapshotName);
            json1.put("createImage",json2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullURL, json1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    callback.onSuccess("success");
                } else{
                    if (error.networkResponse.statusCode == 401){
                        Toast.makeText(mApplicationContext, "Expired token. Please login again", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mApplicationContext, LoginActivity.class);
                        mApplicationContext.startActivity(i);
                    }else {
                        Toast.makeText(mApplicationContext,"Network Error", Toast.LENGTH_SHORT).show();
                        callback.onSuccess("error");
                    }
                }
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        NetworkController.getInstance(mApplicationContext).addToRequestQueue(jsonObjectRequest);
    }
}
