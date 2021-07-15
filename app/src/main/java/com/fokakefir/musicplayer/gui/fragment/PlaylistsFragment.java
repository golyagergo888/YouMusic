package com.fokakefir.musicplayer.gui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.gui.recyclerview.PlaylistAdapter;
import com.fokakefir.musicplayer.model.Playlist;

import java.util.ArrayList;
import java.util.List;


public class PlaylistsFragment extends Fragment implements PlaylistAdapter.OnPlaylistListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private MainActivity activity;

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Playlist> playlistList;

    // endregion

    // region 2. Lifecycle and Constructor

    public PlaylistsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_playlists, container, false);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_playlists);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new PlaylistAdapter(this.activity.getAllPlaylists(), this, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Playlist listener

    @Override
    public void onPlaylistClick(int playlistId) {
        this.activity.addMusicsFragment(playlistId);
    }

    // endregion

    // region 4. Database

    public void swapCursor(Cursor cursor) {
        this.adapter.swapCursor(cursor);
    }

    // endregion
}