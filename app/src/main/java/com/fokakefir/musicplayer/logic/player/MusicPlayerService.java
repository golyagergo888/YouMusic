package com.fokakefir.musicplayer.logic.player;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fokakefir.musicplayer.gui.activity.MainActivity;

public class MusicPlayerService extends Service {

    private MusicPlayer musicPlayer;

    public MusicPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();

        this.musicPlayer = new MusicPlayer(this);
        this.musicPlayer.setCurrentMusic(bundle.getParcelable(MusicPlayer.CURRENT_MUSIC));
        this.musicPlayer.setMusics(bundle.getParcelableArrayList(MusicPlayer.MUSICS));
        this.musicPlayer.setShuffle(bundle.getBoolean(MusicPlayer.SHUFFLE));
        this.musicPlayer.setRepeat(bundle.getBoolean(MusicPlayer.REPEAT));

        Uri uri = Uri.parse(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/YoutubeMusics/" + this.musicPlayer.getCurrentMusic().getVideoId() + ".m4a"
        );
        this.musicPlayer.playMusicUri(uri);
        this.musicPlayer.setProgress(bundle.getInt(MusicPlayer.PROGRESS));

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("data");
        intent.putExtra(MusicPlayer.CURRENT_MUSIC, this.musicPlayer.getCurrentMusic());
        intent.putExtra(MusicPlayer.PROGRESS, this.musicPlayer.getTimePosition());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        this.musicPlayer.stopMediaPlayer();
    }
}