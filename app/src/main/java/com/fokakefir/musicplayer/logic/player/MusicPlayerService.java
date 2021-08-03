package com.fokakefir.musicplayer.logic.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fokakefir.musicplayer.gui.activity.MainActivity;

import static com.fokakefir.musicplayer.gui.activity.MainActivity.INTENT_FILTER_ACTIVITY;

public class MusicPlayerService extends Service implements MusicPlayer.MusicPlayerListener, Runnable {

    // region 0. Constants

    public static final String INTENT_FILTER_SERVICE = "data_service";

    public static final String INTENT_TYPE_PREPARED = "type_prepared";
    public static final String INTENT_TYPE_PLAY = "type_play";
    public static final String INTENT_TYPE_PAUSE = "type_pause";
    public static final String INTENT_TYPE_STOP = "type_stop";
    public static final String INTENT_TYPE_POSITION = "type_position";

    public static final String TYPE = "type";
    public static final String IMAGE_RESOURCE = "img_resource";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String LENGTH = "length";
    public static final String POSITION = "position";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String AUDIO_SESSION_ID = "audio_session_id";

    // endregion

    // region 1. Decl and Init

    private MusicPlayer musicPlayer;

    private Handler handler;

    // endregion

    // region 2. Lifecycle

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.musicPlayer = new MusicPlayer(this, this);

        LocalBroadcastManager.getInstance(this).registerReceiver(this.receiver, new IntentFilter(INTENT_FILTER_ACTIVITY));

        this.handler = new Handler(Looper.getMainLooper());
        this.handler.post(this);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
    }

    // endregion

    // region 3. LocalBroadcastReceiver

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            switch (bundle.getString(MainActivity.TYPE)) {
                case MainActivity.INTENT_TYPE_PLAY_URI:
                    musicPlayer.setCurrentMusic(bundle.getParcelable(MainActivity.CURRENT_MUSIC));
                    musicPlayer.setMusics(bundle.getParcelableArrayList(MainActivity.MUSICS));
                    musicPlayer.setPlaylistId(bundle.getInt(MainActivity.PLAYLIST_ID));

                    Uri uri = Uri.parse(bundle.getString(MainActivity.URI));
                    musicPlayer.playMusicUri(uri);
                    break;

                case MainActivity.INTENT_TYPE_PLAY:
                    musicPlayer.playMusic();
                    break;

                case MainActivity.INTENT_TYPE_PAUSE:
                    musicPlayer.pauseMusic();
                    break;

                case MainActivity.INTENT_TYPE_NEXT:
                    musicPlayer.nextMusic();
                    break;

                case MainActivity.INTENT_TYPE_PREVIOUS:
                    musicPlayer.previousMusic();
                    break;

                case MainActivity.INTENT_TYPE_PROGRESS:
                    musicPlayer.setProgress(bundle.getInt(MainActivity.PROGRESS));
                    break;

                case MainActivity.INTENT_TYPE_INSERT_NEW_MUSIC:
                    musicPlayer.insertNewMusic(bundle.getParcelable(MainActivity.NEW_MUSIC));
                    break;

                case MainActivity.INTENT_TYPE_SHUFFLE:
                    musicPlayer.setShuffle(bundle.getBoolean(MainActivity.SHUFFLE));
                    break;

                case MainActivity.INTENT_TYPE_REPEAT:
                    musicPlayer.setRepeat(bundle.getBoolean(MainActivity.REPEAT));
                    break;
            }
        }
    };

    // endregion

    // region 4. Music listener

    @Override
    public void onPreparedMusic(int imgResource, String title, String artist, int length, int playlistId, int audioSessionId) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PREPARED);
        intent.putExtra(IMAGE_RESOURCE, imgResource);
        intent.putExtra(TITLE, title);
        intent.putExtra(ARTIST, artist);
        intent.putExtra(LENGTH, length);
        intent.putExtra(PLAYLIST_ID, playlistId);
        intent.putExtra(AUDIO_SESSION_ID, audioSessionId);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onPlayMusic(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PLAY);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onPauseMusic(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PAUSE);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onStopMediaPlayer(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_STOP);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // endregion

    // region 5. Runnable

    @Override
    public void run() {
        if (this.musicPlayer.isPlayable()) {
            int position = this.musicPlayer.getTimePosition();

            Intent intent = new Intent(INTENT_FILTER_SERVICE);
            intent.putExtra(TYPE, INTENT_TYPE_POSITION);
            intent.putExtra(POSITION, position);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        this.handler.postDelayed(this, 250);
    }

    // endregion

}