package com.fokakefir.musicplayer.gui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    // region 1. Decl and Init

    private List<Music> musics;
    private OnMusicListener onMusicListener;
    private int playlistId;

    // endregion

    // region 2. Constructor

    public MusicAdapter(List<Music> musics, OnMusicListener onMusicListener, int playlistId) {
        this.musics = musics;
        this.onMusicListener = onMusicListener;
        this.playlistId = playlistId;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_music, parent, false);
        MusicViewHolder viewHolder = new MusicViewHolder(v, this.onMusicListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {
        Music currentMusic = this.musics.get(position);

        holder.txtTitle.setText(currentMusic.getTitle());
        holder.txtArtist.setText(currentMusic.getArtist());
        holder.music = currentMusic;
    }

    @Override
    public int getItemCount() {
        return this.musics.size();
    }

    // endregion

    // region 4. Holder class

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgMusic;
        public TextView txtTitle;
        public TextView txtArtist;

        public Music music;

        private View itemView;

        private OnMusicListener onMusicListener;

        public MusicViewHolder(@NonNull View itemView, OnMusicListener onMusicListener) {
            super(itemView);

            this.music = null;
            this.imgMusic = itemView.findViewById(R.id.img_music);
            this.txtTitle = itemView.findViewById(R.id.txt_music_title);
            this.txtArtist = itemView.findViewById(R.id.txt_music_artist);

            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            this.onMusicListener = onMusicListener;
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                this.onMusicListener.onMusicClick(this.music);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnMusicListener {
        void onMusicClick(Music music);
    }

    // endregion

}
