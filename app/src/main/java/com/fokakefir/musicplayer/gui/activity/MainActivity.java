package com.fokakefir.musicplayer.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.fragment.AlbumsFragment;
import com.fokakefir.musicplayer.gui.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener, MediaPlayer.OnCompletionListener {

    // region 0. Constants

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int YOUTUBE_ITAG_480 = 18;

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

    public void playMusicUri(Uri uri) {
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
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    try {
                        String downloadUrl = ytFiles.get(YOUTUBE_ITAG_480).getUrl();
                        new RequestDownloadMusicStream().execute(downloadUrl, videoTitle);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        youTubeUriExtractor.execute(url);
    }

    private class RequestDownloadMusicStream extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(MainActivity.this);
            this.dialog.setMessage("Downloading file. Please wait...");
            this.dialog.setIndeterminate(false);
            this.dialog.setMax(100);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            URL url = null;
            int len1 = 0;
            int tempProgress = 0;
            int progress = 0;
            try {
                url = new URL(params[0]);
                inputStream = url.openStream();
                URLConnection urlConnection = (URLConnection) url.openConnection();
                urlConnection.connect();
                int size = urlConnection.getContentLength();

                if (urlConnection != null) {
                    String fileName = params[1] + ".mp3";
                    String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeMusics";
                    File f = new File(storagePath);
                    if (!f.exists()) {
                        f.mkdir();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(f+"/"+fileName);
                    byte[] buffer = new byte[1024];
                    int total = 0;
                    if (inputStream != null) {
                        while ((len1 = inputStream.read(buffer)) != -1) {
                            total += len1;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            progress = (int) ((total * 100) / size);
                            if(progress >= 0) {
                                tempProgress = progress;
                                publishProgress("" + progress);
                            }else
                                publishProgress("" + tempProgress+1);

                            fileOutputStream.write(buffer, 0, len1);
                        }
                    }

                    if (fileOutputStream != null) {
                        publishProgress("" + 100);
                        fileOutputStream.close();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            this.dialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (this.dialog.isShowing())
                this.dialog.dismiss();
        }
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

}