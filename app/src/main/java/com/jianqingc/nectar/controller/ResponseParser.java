package com.jianqingc.nectar.controller;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by scjc on 2016/10/1.
 * The ResponseParser is designed to parse the response of Login HTTP request with keyboard input tenant name, username and password
 */
public class ResponseParser {
    private static ResponseParser mInstance;
    private Context mApplicationContext;


    public static ResponseParser getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ResponseParser(context);
        return mInstance;
    }
    public ResponseParser(Context context){
        this.mApplicationContext = context.getApplicationContext();
    }

    public void loginParser(JSONObject loginResponse){
        String expires = null;
        String tokenId = null;
        String tenantName = null;
        String tenantId = null;
        String tenantDescription = null;
        String username = null;
        String dnsServiceURL = null;
        String computeServiceURL = null;
        String networkServiceURL = null;
        String volumeV2ServiceURL = null;
        String s3ServiceURL = null;
        String alarmingServiceURL = null;
        String imageServiceURL = null;
        String meteringServiceURL = null;
        String cloudformationServiceURL = null;
        String applicationCatalogServiceURL = null;
        String volumeV1ServiceURL = null;
        String ec2ServiceURL = null;
        String orchestrationServiceURL = null;
        try {
            JSONObject accessInfo = loginResponse.getJSONObject("access");
            JSONArray serviceCatalog = accessInfo.getJSONArray("serviceCatalog");
            JSONObject token = accessInfo.getJSONObject("token");
            JSONObject user = accessInfo.getJSONObject("user");
            JSONObject tenant = token.getJSONObject("tenant");
            expires = token.getString("expires");
            tokenId = token.getString("id");
            tenantName= tenant.getString("name");
            tenantId = tenant.getString("id");
            tenantDescription= tenant.getString("description");
            username = user.getString("username");
            dnsServiceURL = serviceCatalog.getJSONObject(0).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            computeServiceURL = serviceCatalog.getJSONObject(1).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            networkServiceURL = serviceCatalog.getJSONObject(2).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            volumeV2ServiceURL = serviceCatalog.getJSONObject(3).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            s3ServiceURL = serviceCatalog.getJSONObject(4).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            alarmingServiceURL = serviceCatalog.getJSONObject(5).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            imageServiceURL = serviceCatalog.getJSONObject(6).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            meteringServiceURL = serviceCatalog.getJSONObject(7).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            cloudformationServiceURL = serviceCatalog.getJSONObject(8).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            applicationCatalogServiceURL = serviceCatalog.getJSONObject(9).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            volumeV1ServiceURL = serviceCatalog.getJSONObject(10).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            ec2ServiceURL = serviceCatalog.getJSONObject(11).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
            orchestrationServiceURL = serviceCatalog.getJSONObject(12).getJSONArray("endpoints").getJSONObject(0).getString("publicURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences =  mApplicationContext.getSharedPreferences("nectar_android", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("expires",expires);
        editor.putString("tokenId", tokenId);
        editor.putString("tenantName", tenantName);
        editor.putString("tenantId",tenantId);
        editor.putString("tenantDescription", tenantDescription);
        editor.putString("username", username);
        editor.putString("dnsServiceURL", dnsServiceURL);
        editor.putString("computeServiceURL", computeServiceURL);
        editor.putString("networkServiceURL", networkServiceURL);
        editor.putString("volumeV2ServiceURL", volumeV2ServiceURL);
        editor.putString("s3ServiceURL", s3ServiceURL);
        editor.putString("alarmingServiceURL", alarmingServiceURL);
        editor.putString("imageServiceURL", imageServiceURL);
        editor.putString("meteringServiceURL", meteringServiceURL);
        editor.putString("cloudformationServiceURL", cloudformationServiceURL);
        editor.putString("applicationCatalogServiceURL", applicationCatalogServiceURL);
        editor.putString("volumeV1ServiceURL", volumeV1ServiceURL);
        editor.putString("ec2ServiceURL", ec2ServiceURL);
        editor.putString("orchestrationServiceURL", orchestrationServiceURL);
        editor.apply();
        }


    public JSONArray listInstance(String response){
        JSONArray resultArray= new JSONArray();
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONArray serverArray= responseJSON.getJSONArray("servers");
            for (int i=0;i<serverArray.length();i++){
                JSONObject instanceObject = (JSONObject) serverArray.get(i);
                JSONObject resultObject = new JSONObject();
                resultObject.put("instanceId", instanceObject.getString("id"));
                resultObject.put("instanceStatus", instanceObject.getString("status"));
                resultObject.put("instanceName", instanceObject.getString("name"));
                resultObject.put("keyName", instanceObject.getString("key_name"));
                resultObject.put("instanceCreatedTime", instanceObject.getString("created"));
                resultObject.put("instanceUpdatedTime", instanceObject.getString("updated"));
                resultObject.put("tenantID", instanceObject.getString("tenant_id"));
                resultObject.put("IPv4Address", instanceObject.getString("accessIPv4"));
                resultObject.put("zone", instanceObject.getString("OS-EXT-AZ:availability_zone"));
                resultArray.put(i,resultObject);
            }
            return resultArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }
    //listVolume
    public JSONArray listVolume(String response){
        JSONArray resultArray= new JSONArray();
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONArray volumesArray= responseJSON.getJSONArray("volumes");
            for (int i=0;i<volumesArray.length();i++){
                JSONObject instanceObject = (JSONObject) volumesArray.get(i);
                JSONObject resultObject = new JSONObject();
                resultObject.put("volumeId", instanceObject.getString("id"));
                resultObject.put("volumeSize", instanceObject.getString("size"));
                resultObject.put("volumeStatus", instanceObject.getString("status"));
                resultArray.put(i,resultObject);
            }
            return resultArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }
}
