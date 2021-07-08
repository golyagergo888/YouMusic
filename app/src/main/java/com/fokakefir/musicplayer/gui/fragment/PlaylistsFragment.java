package com.fokakefir.musicplayer.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.fokakefir.musicplayer.R;


public class PlaylistsFragment extends Fragment {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    // endregion

    // region 2. Lifecycle and Constructor

    public PlaylistsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_playlists, container, false);


        return this.view;
    }

    // endregion
}