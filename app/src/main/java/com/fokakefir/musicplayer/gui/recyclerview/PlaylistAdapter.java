package com.fokakefir.musicplayer.gui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract;
import com.fokakefir.musicplayer.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    // region 1. Decl and Init

    private Cursor cursor;
    private OnPlaylistListener onPlaylistListener;
    private Context context;

    // endregion

    // region 2. Constructor

    public PlaylistAdapter(Cursor cursor, OnPlaylistListener onPlaylistListener, Context context) {
        this.cursor = cursor;
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
        if (!this.cursor.moveToPosition(position))
            return;

        holder.playlistId = this.cursor.getInt(this.cursor.getColumnIndex(MusicPlayerContract.PlaylistEntry._ID));
        holder.txtName.setText(this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_NAME)));
        holder.txtSongs.setText(this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_MUSICS)) + " music");

        int color = R.color.playlistWhite;
        switch (this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_COLOR))) {
            case Playlist.COLOR_RED:
                color = R.color.playlistRed;
                break;
            case Playlist.COLOR_ORANGE:
                color = R.color.playlistOrange;
                break;
            case Playlist.COLOR_YELLOW:
                color = R.color.playlistYellow;
                break;
            case Playlist.COLOR_GREEN:
                color = R.color.playlistGreen;
                break;
            case Playlist.COLOR_BLUE:
                color = R.color.playlistBlue;
                break;
        }
        holder.imgPlaylist.setBackgroundColor(this.context.getResources().getColor(color));
        //holder.imgPlaylist.setColorFilter(this.context.getResources().getColor(color));
    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
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
