package com.jianqingc.nectar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jianqingc.nectar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VolumeDetailFragment extends Fragment {
    View myView;

    public VolumeDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String volumeId = bundle.getString("volumeId");
        Toast.makeText(getActivity().getApplicationContext(), "volumeId: "+volumeId, Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_volume_detail, container, false);
        return myView;
    }

}
