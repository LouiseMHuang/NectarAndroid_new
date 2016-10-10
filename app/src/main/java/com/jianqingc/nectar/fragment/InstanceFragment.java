package com.jianqingc.nectar.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.jianqingc.nectar.R;
import com.jianqingc.nectar.controller.HttpRequestController;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InstanceFragment extends Fragment {

    View myView;
    JSONArray instanceFragmentResultArray;

    public InstanceFragment() {
        // Required empty public constructor
    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_instance, container, false);
        // Inflate the layout for this fragment
        HttpRequestController.getInstance(getContext()).listInstance( new HttpRequestController.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    instanceFragmentResultArray = new JSONArray(result);
                    ArrayList<String[]> instanceListArray = new ArrayList<String[]>();
                    //do something with jsonArray
                    for (int i = 0; i < instanceFragmentResultArray.length(); i++) {
                        String[] instanceList = {
                                instanceFragmentResultArray.getJSONObject(i).getString("instanceId"),
                                instanceFragmentResultArray.getJSONObject(i).getString("instanceName"),
                                instanceFragmentResultArray.getJSONObject(i).getString("instanceStatus"),
                                instanceFragmentResultArray.getJSONObject(i).getString("instanceUpdatedTime"),
                                //instanceFragmentResultArray.getJSONObject(i).getString("IPv4Address"),
                                //instanceFragmentResultArray.getJSONObject(i).getString("zone")
                        };
                        instanceListArray.add(instanceList);
                    }
                    BindDictionary<String[]> dictionary = new BindDictionary<String[]>();
                    dictionary.addStringField(R.id.instanceIdTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] instanceList, int position) {
                            return ("ID: " + instanceList[0]);
                        }
                    });
                    dictionary.addStringField(R.id.instanceNameTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] instanceList, int position) {
                            return ("Name: " + instanceList[1]);
                        }
                    });
                    dictionary.addStringField(R.id.instanceStatusTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] instanceList, int position) {
                            return instanceList[2];
                        }
                    });
                    dictionary.addStringField(R.id.instanceUpdatedTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] instanceList, int position) {
                            return ("Updated at: " + instanceList[3]);
                        }
                    });


                    FunDapter adapter = new FunDapter(InstanceFragment.this.getActivity(), instanceListArray, R.layout.instance_list_pattern, dictionary);
                    ListView instanceLV = (ListView) myView.findViewById(R.id.listViewInstance);
                    instanceLV.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                    AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle bundle = new Bundle();
                            try {
                                String instanceId = instanceFragmentResultArray.getJSONObject(position).getString("instanceId");
                                String instanceName = instanceFragmentResultArray.getJSONObject(position).getString("instanceName");
                                String zone = instanceFragmentResultArray.getJSONObject(position).getString("zone");
                                String address = instanceFragmentResultArray.getJSONObject(position).getString("IPv4Address");
                                String instanceStatus = instanceFragmentResultArray.getJSONObject(position).getString("instanceStatus");
                                ArrayList<String> bundleArrayList = new ArrayList<String>();
                                bundleArrayList.add(instanceId);
                                bundleArrayList.add(instanceName);
                                bundleArrayList.add(zone);
                                bundleArrayList.add(address);
                                bundleArrayList.add(instanceStatus);
                                bundle.putStringArrayList("bundleParam", bundleArrayList);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                                        .beginTransaction();
                                InstanceDetailFragment instanceDetailFragment = new InstanceDetailFragment();
                                instanceDetailFragment.setArguments(bundle);
                                ft.replace(R.id.relativelayout_for_fragment, instanceDetailFragment, instanceDetailFragment.getTag());
                                ft.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    instanceLV.setOnItemClickListener(onListClick);



                    //refresh
                    //refresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, getActivity());

        return myView;
    }

}
