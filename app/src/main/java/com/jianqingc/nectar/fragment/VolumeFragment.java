package com.jianqingc.nectar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
public class VolumeFragment extends Fragment {

    View myView;
    JSONArray volumeFragmentResultArray;
    public VolumeFragment() {
        // Required empty public constructor
    }
    private void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_volume, container, false);
        // Inflate the layout for this fragment
        HttpRequestController.getInstance(getContext()).listVolume(new HttpRequestController.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    volumeFragmentResultArray = new JSONArray(result);
                    ArrayList<String[]> volumeListArray = new ArrayList<String[]>();
                    //do something with jsonArray
                    for (int i=0; i<volumeFragmentResultArray.length();i++){
                        String[] volumeList ={
                                volumeFragmentResultArray.getJSONObject(i).getString("volumeId"),
                                volumeFragmentResultArray.getJSONObject(i).getString("volumeSize"),
                                volumeFragmentResultArray.getJSONObject(i).getString("volumeStatus")
                        };
                        volumeListArray.add(volumeList);
                    }
                    BindDictionary<String[]> dictionary = new BindDictionary<String[]>();
                    dictionary.addStringField(R.id.volumeIdTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] volumeList, int position) {
                            return ("ID: "+volumeList[0]);
                        }
                    });
                    dictionary.addStringField(R.id.volumeSizeTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] volumeList, int position) {
                            return ("Size: "+volumeList[1] +" GB");
                        }
                    });
                    dictionary.addStringField(R.id.volumeStatusTV, new StringExtractor<String[]>() {
                        @Override
                        public String getStringValue(String[] volumeList, int position) {
                            return volumeList[2];
                        }
                    });

                    FunDapter adapter = new FunDapter(VolumeFragment.this.getActivity(),volumeListArray,R.layout.volume_list_pattern,dictionary);
                    ListView volumeLV = (ListView) myView.findViewById(R.id.listViewVolume);
                    volumeLV.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString("volumeId" , volumeFragmentResultArray.getJSONObject(position).getString("volumeId"));
                                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                                        .beginTransaction();
                                VolumeDetailFragment volumeDetailFragment = new VolumeDetailFragment();
                                volumeDetailFragment.setArguments(bundle);
                                ft.replace(R.id.relativelayout_for_fragment, volumeDetailFragment,volumeDetailFragment.getTag());
                                ft.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    volumeLV.setOnItemClickListener(onListClick);

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
