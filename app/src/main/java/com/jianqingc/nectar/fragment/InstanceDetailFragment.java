package com.jianqingc.nectar.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jianqingc.nectar.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstanceDetailFragment extends Fragment {
    View myView;
    public InstanceDetailFragment() {
        // Required empty public constructor
    }
    public void enable(Button btn){
        btn.setEnabled(true);
        btn.setVisibility(View.VISIBLE);
        btn.setBackgroundColor(Color.parseColor("#31319b"));
        if (btn.getText().equals("TERMINATE")){
            btn.setBackgroundColor(Color.parseColor("#990000"));
        }
        btn.setTextColor(Color.parseColor("#ffffff"));
    }
    public void disable(Button btn){
        btn.setEnabled(false);
        btn.setVisibility(View.VISIBLE);
        btn.setBackgroundColor(Color.parseColor("#e6e6e6"));
        btn.setTextColor(Color.parseColor("#8c8c8c"));

    }
    public void hide(Button btn){
        btn.setEnabled(false);
        btn.setVisibility(View.GONE);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_instance_detail, container, false);
        Bundle bundle = getArguments();
        ArrayList<String> bundleParam = bundle.getStringArrayList("bundleParam");
        String instanceId = bundleParam.get(0);
        String instanceName = bundleParam.get(1);
        String zone = bundleParam.get(2);
        String address =bundleParam.get(3);
        String instanceStatus =bundleParam.get(4);
        ((TextView)myView.findViewById(R.id.instanceIdTV)).setText(instanceId);
        ((TextView)myView.findViewById(R.id.instanceNameTV)).setText(instanceName);
        ((TextView)myView.findViewById(R.id.instanceZoneTV)).setText(zone);
        ((TextView)myView.findViewById(R.id.instanceIPAddressTV)).setText(address);
        TextView instanceStatusTV = (TextView)myView.findViewById(R.id.instanceStatusTV);
        instanceStatusTV.setText(instanceStatus);
        //Set Status Color
        if (instanceStatus.equals("ACTIVE")){
            instanceStatusTV.setTextColor(Color.parseColor("#2eb82e"));
        } else if((instanceStatus.equals("DELETED"))||(instanceStatus.equals("ERROR"))|| (instanceStatus.equals("PAUSED"))|| (instanceStatus.equals("SHUTOFF"))|| (instanceStatus.equals("SUSPENDED"))){
            instanceStatusTV.setTextColor(Color.parseColor("#ff0000"));
        } else{
            instanceStatusTV.setTextColor(Color.parseColor("#e9a92a"));
        }

        Button startBtn = (Button)myView.findViewById(R.id.startBtn);
        Button stopBtn = (Button)myView.findViewById(R.id.stopBtn);
        Button pauseBtn = (Button)myView.findViewById(R.id.pauseBtn);
        Button unpauseBtn = (Button)myView.findViewById(R.id.unpauseBtn);
        Button suspendBtn = (Button)myView.findViewById(R.id.suspendBtn);
        Button resumeBtn = (Button)myView.findViewById(R.id.resumeBtn);
        Button deleteBtn = (Button)myView.findViewById(R.id.deleteBtn);
        Button rebootBtn = (Button)myView.findViewById(R.id.rebootBtn);
        Button snapshotBtn = (Button)myView.findViewById(R.id.snapshotBtn);
        //set button visibility

        if(instanceStatus.equals("ACTIVE")){
            enable(pauseBtn);
            enable(suspendBtn);
            enable(stopBtn);
            enable(rebootBtn);
            enable(snapshotBtn);
            enable(deleteBtn);
            hide(unpauseBtn);
            hide(resumeBtn);
            hide(startBtn);
        }
        else if(instanceStatus.equals("SUSPENDED")){
            disable(pauseBtn);
            hide(unpauseBtn);
            enable(resumeBtn);
            hide(suspendBtn);
            disable(stopBtn);
            hide(startBtn);
            disable(rebootBtn);
            enable(snapshotBtn);
            enable(deleteBtn);
        }else if(instanceStatus.equals("PAUSED")){
            enable(unpauseBtn);
            hide(pauseBtn);
            disable(suspendBtn);
            hide(resumeBtn);
            disable(stopBtn);
            hide(startBtn);
            disable(rebootBtn);
            enable(snapshotBtn);
            enable(deleteBtn);
        }else if(instanceStatus.equals("SHUTOFF")){
            disable(pauseBtn);
            hide(unpauseBtn);
            disable(suspendBtn);
            hide(resumeBtn);
            hide(stopBtn);
            enable(startBtn);
            enable(rebootBtn);
            enable(snapshotBtn);
            enable(deleteBtn);
        } else{
            disable(pauseBtn);
            hide(unpauseBtn);
            disable(suspendBtn);
            hide(resumeBtn);
            disable(stopBtn);
            hide(startBtn);
            disable(rebootBtn);
            disable(snapshotBtn);
            disable(deleteBtn);
        }




        // Inflate the layout for this fragment

        return myView;
    }

}
