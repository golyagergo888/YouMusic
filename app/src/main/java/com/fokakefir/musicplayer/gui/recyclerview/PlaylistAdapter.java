package com.fokakefir.musicplayer.gui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    // region 1. Decl and Init

    private List<Playlist> playlists;
    private OnPlaylistListener onPlaylistListener;
    private Context context;

    // endregion

    // region 2. Constructor

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistListener onPlaylistListener, Context context) {
        this.playlists = playlists;
        this.onPlaylistListener = onPlaylistListener;
        this.context = context;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_playlist, parent, false);
        PlaylistViewHolder viewHolder = new PlaylistViewHolder(v, this.onPlaylistListener);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist currentPlaylist = this.playlists.get(position);

        holder.playlistId = currentPlaylist.getId();
        holder.txtName.setText(currentPlaylist.getName());
        holder.txtSongs.setText(currentPlaylist.getNumberOfMusics() + " songs");
        holder.imgPlaylist.setColorFilter(this.context.getResources().getColor(currentPlaylist.getColor()));
    }

    @Override
    public int getItemCount() {
        return this.playlists.size();
    }

    // endregion

    // region 4. Holder class

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgPlaylist;
        public TextView txtName;
        public TextView txtSongs;

        public int playlistId;

        private View itemView;

        private OnPlaylistListener onPlaylistListener;

        public PlaylistViewHolder(@NonNull View itemView, OnPlaylistListener onPlaylistListener) {
            super(itemView);

            this.playlistId = 0;

            this.imgPlaylist = itemView.findViewById(R.id.img_playlist);
            this.txtName = itemView.findViewById(R.id.txt_playlist_name);
            this.txtSongs = itemView.findViewById(R.id.txt_playlist_songs);

            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            this.onPlaylistListener = onPlaylistListener;
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                this.onPlaylistListener.onPlaylistClick(this.playlistId);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnPlaylistListener {
        void onPlaylistClick(int playlistId);
    }

    // endregion

}
