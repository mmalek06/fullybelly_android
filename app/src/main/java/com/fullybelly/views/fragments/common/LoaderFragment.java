package com.fullybelly.views.fragments.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullybelly.R;


public class LoaderFragment extends Fragment {

    //constructor

    public LoaderFragment() { }

    //endregion

    //region public methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loader, container, false);
    }

    //endregion

}
