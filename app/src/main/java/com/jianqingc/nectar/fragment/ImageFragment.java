package com.jianqingc.nectar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jianqingc.nectar.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {
    View myView;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_images, container, false);
        return myView;
    }

}
