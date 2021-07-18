package com.fokakefir.musicplayer.logic.player;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.model.Music;

import java.io.IOException;
import java.util.List;

public class MusicPlayer implements MediaPlayer.OnCompletionListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private MediaPlayer mediaPlayer;

    private Music currentMusic;
    private List<Music> musics;
    private boolean shuffle;

    // endregion

    // region 2. Constructor

    public MusicPlayer(MainActivity activity) {
        this.activity = activity;
        this.shuffle = false;
    }

    // endregion

    // region 3. MediaPlayer

    public void playMusicUri(Uri uri) {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
            this.mediaPlayer.setOnCompletionListener(this);
        } else {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
        }

        try {
            this.mediaPlayer.setDataSource(this.activity, uri);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();

            this.activity.setBtnPlayImage(R.drawable.ic_baseline_pause_music);
        } catch (IOException e) {
            e.printStackTrace();
            this.mediaPlayer = null;
        }
    }

    public void playMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.start();
            this.activity.setBtnPlayImage(R.drawable.ic_baseline_pause_music);
        }
    }

    public void pauseMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
            this.activity.setBtnPlayImage(R.drawable.ic_baseline_play_music);
        }
    }

    public void stopMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;

            this.activity.setBtnPlayImage(R.drawable.ic_baseline_play_music);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // TODO set next music
        stopMediaPlayer();
    }

    // endregion

    // region 4. Getters and Setters

    public boolean isPlayable() {
        if (this.mediaPlayer != null)
            return true;
        else
            return false;
    }

    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    // endregion

}
