package com.fokakefir.musicplayer.gui.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.model.VideoYT;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    // region 1. Decl and Init

    private Context context;
    private List<VideoYT> videos;
    private OnVideoListener onVideoListener;

    // endregion

    // region 2. Constructor

    public VideoAdapter(Context context, List<VideoYT> videos, OnVideoListener onVideoListener) {
        this.context = context;
        this.videos = videos;
        this.onVideoListener = onVideoListener;
    }


    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_video, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(v, this.onVideoListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoYT currentVideo = this.videos.get(position);

        holder.txtTitle.setText(currentVideo.getSnippet().getTitle());
        holder.txtChannel.setText(currentVideo.getSnippet().getChannelTitle());

        String imageUrl = currentVideo.getSnippet().getThumbnails().getMedium().getUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imgVideo);

        holder.videoId = currentVideo.getId().getVideoId();

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    // endregion

    // region 4. Holder class

    public static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgVideo;
        public TextView txtTitle;
        public TextView txtChannel;

        public String videoId;

        private View itemView;

        private OnVideoListener onVideoListener;

        public VideoViewHolder(@NonNull View itemView, OnVideoListener onVideoListener) {
            super(itemView);

            this.imgVideo = itemView.findViewById(R.id.img_video);
            this.txtTitle = itemView.findViewById(R.id.txt_video_title);
            this.txtChannel = itemView.findViewById(R.id.txt_video_channel);

            this.videoId = null;

            this.itemView = itemView;
            itemView.setOnClickListener(this);

            this.onVideoListener = onVideoListener;
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                if (this.videoId != null)
                    this.onVideoListener.onVideoClick(this.videoId);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnVideoListener {
        void onVideoClick(String videoId);
    }

    // endregion

}
