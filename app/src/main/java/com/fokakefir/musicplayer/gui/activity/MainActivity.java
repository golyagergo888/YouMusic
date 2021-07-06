package com.fokakefir.musicplayer.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.fragment.AlbumsFragment;
import com.fokakefir.musicplayer.gui.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener, MediaPlayer.OnCompletionListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private SearchFragment searchFragment;
    private AlbumsFragment albumsFragment;

    private BottomNavigationView bottomNav;
    private SlidingUpPanelLayout layout;

    private MediaPlayer mediaPlayer;

    private ImageButton btnPlay;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.searchFragment = new SearchFragment(this);
        this.albumsFragment = new AlbumsFragment();

        this.bottomNav = findViewById(R.id.bottom_navigation);
        this.layout = findViewById(R.id.sliding_up_panel);
        this.btnPlay = findViewById(R.id.btn_play_music);

        this.bottomNav.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.searchFragment).commit();
        getSupportActionBar().setTitle("YouTube");

        this.btnPlay.setImageResource(R.drawable.ic_baseline_play_music);
        this.btnPlay.setOnClickListener(this);

        this.layout.addPanelSlideListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        stopMediaPlayer();
    }

    // endregion

    // region 3. Fragments

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.nav_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.searchFragment).commit();
                    getSupportActionBar().setTitle("YouTube");
                    return true;
                case R.id.nav_albums:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.albumsFragment).commit();
                    getSupportActionBar().setTitle("Albums");
                    return true;
            }
        }
        return false;
    }

    // endregion

    // region 4. Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_play_music) {
            if (this.mediaPlayer != null) {
                if (this.mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        }
    }

    // endregion

    // region 5. MediaPlayer

    public void playMusicUrl(Uri uri) {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
        }

        try {
            this.mediaPlayer.setDataSource(this, uri);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();

            this.btnPlay.setImageResource(R.drawable.ic_baseline_pause_music);
        } catch (IOException e) {
            e.printStackTrace();
            this.mediaPlayer = null;
        }
    }

    public void playMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.start();
            this.btnPlay.setImageResource(R.drawable.ic_baseline_pause_music);
        }
    }

    public void pauseMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
            this.btnPlay.setImageResource(R.drawable.ic_baseline_play_music);
        }
    }

    public void stopMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();;
            this.mediaPlayer = null;

            this.btnPlay.setImageResource(R.drawable.ic_baseline_play_music);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMediaPlayer();
    }

    // endregion

    // region 6. SlidingUpPanel

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        findViewById(R.id.layout_down).setAlpha(1 - slideOffset);
        findViewById(R.id.layout_up).setAlpha(slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    // endregion

    // region 7. Download music

    public void downloadMusic(String url) {
    }

    // endregion

}