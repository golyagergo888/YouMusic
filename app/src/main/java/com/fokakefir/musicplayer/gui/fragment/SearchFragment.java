package com.fokakefir.musicplayer.gui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fokakefir.musicplayer.R;


public class SearchFragment extends Fragment {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    // endregion

    // region 2. Lifecycle and Constructor

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_search, container, false);


        return this.view;
    }

    // endregion
}