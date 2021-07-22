package com.fokakefir.musicplayer.logic.player;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.model.Music;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer implements MediaPlayer.OnCompletionListener {

    // region 0. Constants

    public static final String CURRENT_MUSIC = "current music";
    public static final String MUSICS = "musics";
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";
    public static final String PROGRESS = "progress";

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;
    private MusicPlayerService service;

    private MediaPlayer mediaPlayer;

    private Music currentMusic;
    private ArrayList<Music> musics;
    private boolean shuffle;
    private boolean repeat;

    // endregion

    // region 2. Constructor

    public MusicPlayer(MainActivity activity) {
        this.activity = activity;
        this.shuffle = false;
        this.repeat = false;
    }

    public MusicPlayer(MusicPlayerService service) {
        this.service = service;
        this.shuffle = false;
        this.repeat = false;
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
            if (this.activity != null) {
                this.mediaPlayer.setDataSource(this.activity, uri);
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();

                this.activity.setBtnPlayImage(R.drawable.ic_baseline_pause_music);
                this.activity.setMusicTexts(this.currentMusic.getTitle(), this.currentMusic.getArtist());
                this.activity.setMusicSeekBar(this.currentMusic.getLength());
            } else {
                this.mediaPlayer.setDataSource(this.service, uri);
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.mediaPlayer = null;
        }

    }

    public void playMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.start();
            if (this.activity != null)
                this.activity.setBtnPlayImage(R.drawable.ic_baseline_pause_music);
        }
    }

    public void pauseMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
            if (this.activity != null)
                this.activity.setBtnPlayImage(R.drawable.ic_baseline_play_music);
        }
    }

    public void previousMusic() {
        if (this.currentMusic != null) {
            int position = getCurrentMusicPosition() - 1;
            if (position >= 0) {
                this.currentMusic = this.musics.get(position);
                Uri uri = Uri.parse(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                "/YoutubeMusics/" + this.currentMusic.getVideoId() + ".m4a"
                );
                this.playMusicUri(uri);
            }
        }
    }

    public void nextMusic() {
        if (this.currentMusic != null) {
            int position = getCurrentMusicPosition() + 1;
            if (position < this.musics.size()) {
                this.currentMusic = this.musics.get(position);
                Uri uri = Uri.parse(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                "/YoutubeMusics/" + this.currentMusic.getVideoId() + ".m4a"
                );
                this.playMusicUri(uri);
            }
        }
    }

    public void stopMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;

            if (this.activity != null)
                this.activity.setBtnPlayImage(R.drawable.ic_baseline_play_music);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (getCurrentMusicPosition() < this.musics.size() - 1) {
            nextMusic();
        } else {
            if (!this.repeat) {
                stopMediaPlayer();
            } else {
                // TODO first music
            }
        }
    }

    // endregion

    // region 4. Getters and Setters

    private int getCurrentMusicPosition() {
        for (int ind = 0; ind < musics.size(); ind++) {
            if (this.currentMusic.getId() == this.musics.get(ind).getId())
                return ind;
        }
        return -1;
    }

    public int getTimePosition() {
        if (this.mediaPlayer != null) {
            return this.mediaPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public Music getCurrentMusic() {
        return currentMusic;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isPlayable() {
        return (this.mediaPlayer != null);
    }

    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setProgress(int progress) {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.seekTo(progress * 1000, MediaPlayer.SEEK_CLOSEST);
        }
    }

    // endregion

}
