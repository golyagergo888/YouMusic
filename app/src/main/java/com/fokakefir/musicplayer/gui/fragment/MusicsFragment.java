package com.fokakefir.musicplayer.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.gui.recyclerview.MusicAdapter;
import com.fokakefir.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicsFragment extends Fragment implements MusicAdapter.OnMusicListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private MainActivity activity;

    private int playlistId;

    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Music> musics;

    // endregion

    // region 2. Lifecycle and Constructor

    public MusicsFragment(MainActivity activity, int playlistId) {
        this.activity = activity;
        this.playlistId = playlistId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_musics, container, false);

        this.musics = new ArrayList<>();
        this.musics.add(new Music(0, "valami", "Hardbass zene", "Az eloado", 123));

        this.recyclerView = this.view.findViewById(R.id.recycler_view_musics);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new MusicAdapter(this.musics, this, this.playlistId);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Music listener

    @Override
    public void onMusicClick(Music music) {
        Toast.makeText(this.activity, music.getTitle() + " id: " + music.getId(), Toast.LENGTH_SHORT).show();
    }

    // endregion

}
