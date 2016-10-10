package com.jianqingc.nectar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jianqingc.nectar.R;
import com.jianqingc.nectar.controller.HttpRequestController;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    View myView;
    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_overview, container, false);
        // Inflate the layout for this fragment
        HttpRequestController.getInstance(getContext()).listOverview(getActivity());
        return myView;
    }

}
