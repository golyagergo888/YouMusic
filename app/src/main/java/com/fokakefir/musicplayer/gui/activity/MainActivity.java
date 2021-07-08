package com.fokakefir.musicplayer.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.background.RequestDownloadMusicStream;
import com.fokakefir.musicplayer.gui.fragment.PlaylistsFragment;
import com.fokakefir.musicplayer.gui.fragment.SearchFragment;
import com.fokakefir.musicplayer.background.RequestDownloadMusicStreamResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener, MediaPlayer.OnCompletionListener, RequestDownloadMusicStreamResponse {

    // region 0. Constants

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int YOUTUBE_ITAG_VIDEO_480P = 18;
    private static final int YOUTUBE_ITAG_AUDIO_50K = 249;
    private static final int YOUTUBE_ITAG_AUDIO_160K = 251;
    private static final int YOUTUBE_ITAG_AUDIO_128K = 140;

    // endregion

    // region 1. Decl and Init

    private SearchFragment searchFragment;
    private PlaylistsFragment playlistsFragment;

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
        this.playlistsFragment = new PlaylistsFragment();

        this.bottomNav = findViewById(R.id.bottom_navigation);
        this.layout = findViewById(R.id.sliding_up_panel);
        this.btnPlay = findViewById(R.id.btn_play_music);

        this.bottomNav.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.searchFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.playlistsFragment).hide(this.playlistsFragment).commit();
        getSupportActionBar().setTitle("YouTube");

        this.btnPlay.setImageResource(R.drawable.ic_baseline_play_music);
        this.btnPlay.setOnClickListener(this);

        this.layout.addPanelSlideListener(this);

        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    getSupportFragmentManager().beginTransaction().hide(this.playlistsFragment).commit();
                    getSupportFragmentManager().beginTransaction().show(this.searchFragment).commit();
                    getSupportActionBar().setTitle("YouTube");
                    return true;
                case R.id.nav_playlists:
                    getSupportFragmentManager().beginTransaction().hide(this.searchFragment).commit();
                    getSupportFragmentManager().beginTransaction().show(this.playlistsFragment).commit();
                    getSupportActionBar().setTitle("Playlists");
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

    public void playMusicUri(Uri uri) {
        //Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeMusics/" + name);
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopMediaPlayer();
                }
            });
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
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    try {
                        String downloadUrl = ytFiles.get(YOUTUBE_ITAG_AUDIO_128K).getUrl();
                        RequestDownloadMusicStream requestDownloadMusicStream = new RequestDownloadMusicStream(MainActivity.this, MainActivity.this);
                        requestDownloadMusicStream.execute(downloadUrl, videoTitle);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        youTubeUriExtractor.execute(url);
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    // endregion

    // region 8. Database

    @Override
    public void onMusicDownloaded() {
        Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();
    }

    // endregion

}