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

    // endregion

    // region 2. Constructor

    public VideoAdapter(Context context, List<VideoYT> videos) {
        this.context = context;
        this.videos = videos;
    }


    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_video, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoYT currentVideo = this.videos.get(position);

        holder.txtTitle.setText(currentVideo.getSnippet().getTitle());
        holder.txtDescription.setText(currentVideo.getSnippet().getDescription());

        String imageUrl = currentVideo.getSnippet().getThumbnails().getMedium().getUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imgVideo);

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    // endregion

    // region 4. Holder class

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgVideo;
        public TextView txtTitle;
        public TextView txtDescription;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imgVideo = itemView.findViewById(R.id.img_video);
            this.txtTitle = itemView.findViewById(R.id.txt_video_title);
            this.txtDescription = itemView.findViewById(R.id.txt_video_description);
        }
    }

    // endregion

    // region 5. Listener interface

    // endregion

}
