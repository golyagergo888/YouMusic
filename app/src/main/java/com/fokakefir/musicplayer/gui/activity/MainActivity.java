package com.fokakefir.musicplayer.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.fragment.ChoosePlaylistFragment;
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract;
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.*;
import com.fokakefir.musicplayer.logic.background.RequestDownloadMusicStream;
import com.fokakefir.musicplayer.logic.background.RequestDownloadThumbnailStream;
import com.fokakefir.musicplayer.gui.fragment.MusicsFragment;
import com.fokakefir.musicplayer.gui.fragment.PlaylistsFragment;
import com.fokakefir.musicplayer.gui.fragment.SearchFragment;
import com.fokakefir.musicplayer.logic.background.RequestDownloadMusicStreamResponse;
import com.fokakefir.musicplayer.logic.database.MusicPlayerDBHelper;
import com.fokakefir.musicplayer.logic.player.MusicPlayerService;
import com.fokakefir.musicplayer.model.Music;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.fokakefir.musicplayer.logic.network.YoutubeAPI.YOUTUBE_ITAG_AUDIO_128K;
import static com.fokakefir.musicplayer.logic.player.MusicPlayerService.INTENT_FILTER_SERVICE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener, RequestDownloadMusicStreamResponse, SeekBar.OnSeekBarChangeListener {

    // region 0. Constants

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;

    public static final int DEFAULT_PLAYLIST_ID = 1;

    public static final String INTENT_FILTER_ACTIVITY = "data_activity";

    public static final String INTENT_TYPE_PLAY_URI = "play_uri";
    public static final String INTENT_TYPE_PLAY = "play";
    public static final String INTENT_TYPE_PAUSE = "pause";
    public static final String INTENT_TYPE_NEXT = "next";
    public static final String INTENT_TYPE_PREVIOUS = "previous";
    public static final String INTENT_TYPE_PROGRESS = "progress";

    public static final String TYPE = "type";
    public static final String CURRENT_MUSIC = "current_music";
    public static final String MUSICS = "musics";
    public static final String URI = "uri";
    public static final String PROGRESS = "progress";

    // endregion

    // region 1. Decl and Init

    private SQLiteDatabase database;

    private SearchFragment searchFragment;
    private PlaylistsFragment playlistsFragment;
    private MusicsFragment musicsFragment;
    private ChoosePlaylistFragment choosePlaylistFragment;

    private BottomNavigationView bottomNav;
    private SlidingUpPanelLayout layout;

    private TextView txtMusicTitleDown;
    private TextView txtMusicArtistDown;
    private ImageButton btnPlayDown;

    private TextView txtMusicTitleUp;
    private TextView txtMusicArtistUp;
    private TextView txtCurrentTime;
    private TextView txtFinalTime;
    private SeekBar seekBar;
    private CircleImageView btnPlayUp;
    private ImageButton btnPrevious;
    private ImageButton btnNext;

    private boolean slidingSeekBar;
    private boolean isPlaying;
    private boolean isPlayable;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MusicPlayerDBHelper dbHelper = new MusicPlayerDBHelper(this);
        this.database = dbHelper.getWritableDatabase();

        this.searchFragment = new SearchFragment(this);
        this.playlistsFragment = new PlaylistsFragment(this);
        this.musicsFragment = null;
        this.choosePlaylistFragment = null;

        this.bottomNav = findViewById(R.id.bottom_navigation);
        this.layout = findViewById(R.id.sliding_up_panel);

        this.txtMusicTitleDown = findViewById(R.id.txt_music_title_down);
        this.txtMusicArtistDown = findViewById(R.id.txt_music_artist_down);
        this.btnPlayDown = findViewById(R.id.btn_play_music_down);

        this.txtMusicTitleUp = findViewById(R.id.txt_music_title_up);
        this.txtMusicArtistUp = findViewById(R.id.txt_music_artist_up);
        this.txtCurrentTime = findViewById(R.id.txt_current_time);
        this.txtFinalTime = findViewById(R.id.txt_final_time);
        this.seekBar = findViewById(R.id.seek_bar);
        this.btnPlayUp = findViewById(R.id.btn_play_music_up);
        this.btnPrevious = findViewById(R.id.btn_previous_music);
        this.btnNext = findViewById(R.id.btn_next_music);

        this.txtMusicTitleDown.setSelected(true);
        this.txtMusicTitleUp.setSelected(true);

        this.bottomNav.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.searchFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.playlistsFragment).hide(this.playlistsFragment).commit();

        this.btnPlayDown.setImageResource(R.drawable.ic_baseline_play_music);
        this.btnPlayUp.setImageResource(R.drawable.ic_baseline_play_music);
        this.btnPlayDown.setOnClickListener(this);
        this.btnPlayUp.setOnClickListener(this);
        this.btnPrevious.setOnClickListener(this);
        this.btnNext.setOnClickListener(this);

        this.layout.addPanelSlideListener(this);

        this.seekBar.setOnSeekBarChangeListener(this);
        this.slidingSeekBar = false;

        Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(this.receiver, new IntentFilter(INTENT_FILTER_SERVICE));
        this.isPlaying = false;
        this.isPlayable = false;

        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
    }

    @Override
    public void onBackPressed() {
        if (this.bottomNav.getSelectedItemId() == R.id.nav_playlists) {
            if (this.choosePlaylistFragment != null) {
                this.musicsFragment = null;
                super.onBackPressed();
            } else if (this.musicsFragment != null) {
                this.musicsFragment = null;
                super.onBackPressed();
            }
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
                    if (this.musicsFragment != null) {
                        getSupportFragmentManager().beginTransaction().hide(this.musicsFragment).commit();
                    }
                    if (this.choosePlaylistFragment != null) {
                        closeChoosePlaylistFragment();
                    }
                    return true;
                case R.id.nav_playlists:
                    getSupportFragmentManager().beginTransaction().hide(this.searchFragment).commit();
                    getSupportFragmentManager().beginTransaction().show(this.playlistsFragment).commit();
                    if (this.musicsFragment != null) {
                        getSupportFragmentManager().beginTransaction().show(this.musicsFragment).commit();
                    }
                    return true;
            }
        }
        return false;
    }

    public void addMusicsFragment(int playlistId) {
        this.musicsFragment = new MusicsFragment(this, playlistId);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.musicsFragment).addToBackStack(null).commit();
    }

    public void addChoosePlaylistFragment(int musicId) {
        this.choosePlaylistFragment = new ChoosePlaylistFragment(this, musicId);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.choosePlaylistFragment).addToBackStack(null).commit();
    }

    public void closeChoosePlaylistFragment() {
        getSupportFragmentManager().popBackStack();
    }

    // endregion

    // region 4. Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_play_music_down || view.getId() == R.id.btn_play_music_up) {
            if (this.isPlayable) {
                Intent intent = new Intent(INTENT_FILTER_ACTIVITY);
                if (this.isPlaying) {
                    intent.putExtra(TYPE, INTENT_TYPE_PAUSE);
                } else {
                    intent.putExtra(TYPE, INTENT_TYPE_PLAY);
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } else if (view.getId() == R.id.btn_previous_music) {
            if (this.isPlayable) {
                Intent intent = new Intent(INTENT_FILTER_ACTIVITY);
                intent.putExtra(TYPE, INTENT_TYPE_PREVIOUS);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } else if (view.getId() == R.id.btn_next_music) {
            if (this.isPlayable) {
                Intent intent = new Intent(INTENT_FILTER_ACTIVITY);
                intent.putExtra(TYPE, INTENT_TYPE_NEXT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    // endregion

    // region 5. MusicPlayer

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            switch (bundle.getString(MusicPlayerService.TYPE)) {
                case MusicPlayerService.INTENT_TYPE_PREPARED:
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE));
                    setMusicTexts(bundle.getString(MusicPlayerService.TITLE), bundle.getString(MusicPlayerService.ARTIST));
                    setMusicSeekBar(bundle.getInt(MusicPlayerService.LENGTH));
                    isPlaying = true;
                    isPlayable = true;
                    seekBar.setProgress(0);
                    break;

                case MusicPlayerService.INTENT_TYPE_PLAY:
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE));
                    isPlaying = true;
                    break;

                case MusicPlayerService.INTENT_TYPE_PAUSE:
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE));
                    isPlaying = false;
                    break;

                case MusicPlayerService.INTENT_TYPE_STOP:
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE));
                    isPlaying = false;
                    isPlayable = false;
                    break;

                case MusicPlayerService.INTENT_TYPE_POSITION:
                    setMusicSeekBarPosition(bundle.getInt(MusicPlayerService.POSITION));
                    break;
            }
        }
    };

    public void playMusic(Music music, int playlistId) {
        String strUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/YoutubeMusics/" + music.getVideoId() + ".m4a";

        Intent intent = new Intent(INTENT_FILTER_ACTIVITY);
        intent.putExtra(TYPE, INTENT_TYPE_PLAY_URI);
        intent.putExtra(CURRENT_MUSIC, music);
        intent.putExtra(MUSICS, getMusics(playlistId));
        intent.putExtra(URI, strUri);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (this.isPlayable && fromUser) {
            int minute = progress / 60;
            int second = progress % 60;
            String strMinute = String.valueOf(minute);
            String strSecond;
            if (second < 10)
                strSecond = "0" + second;
            else
                strSecond = String.valueOf(second);

            this.txtCurrentTime.setText(strMinute+ ":" + strSecond);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.slidingSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.slidingSeekBar = false;
        if (this.isPlayable) {
            Intent intent = new Intent(INTENT_FILTER_ACTIVITY);
            intent.putExtra(TYPE, INTENT_TYPE_PROGRESS);
            intent.putExtra(PROGRESS, seekBar.getProgress());

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
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

    // region 7. Download and delete music

    public void downloadMusic(String url, String videoArtist) {
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    try {
                        String downloadUrl = ytFiles.get(YOUTUBE_ITAG_AUDIO_128K).getUrl();
                        RequestDownloadMusicStream requestDownloadMusicStream = new RequestDownloadMusicStream(MainActivity.this, MainActivity.this);
                        requestDownloadMusicStream.execute(downloadUrl, videoId, videoTitle, videoArtist);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        youTubeUriExtractor.execute(url);
    }

    @Override
    public void onMusicDownloaded(String videoId, String title, String artist, int length) {
        insertMusic(videoId, title, artist, length);
    }

    public void downloadThumbnail(String url, String name) {
        new RequestDownloadThumbnailStream(this).execute(url, name);
    }

    public void deleteMusicFromStorage(Music music) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeMusics");
        File file = new File(dir, music.getVideoId() + ".m4a");
        boolean deleted = file.delete();

        if (deleted)
            deleteMusic(music);
    }

    // endregion

    // region 8. Database

    public void insertMusic(String videoId, String title, String artist, int length) {
        ContentValues cvMusic = new ContentValues();
        cvMusic.put(MusicEntry.COLUMN_VIDEO_ID, videoId);
        cvMusic.put(MusicEntry.COLUMN_TITLE, title);
        cvMusic.put(MusicEntry.COLUMN_ARTIST, artist);
        cvMusic.put(MusicEntry.COLUMN_LENGTH, length);

        long id = this.database.insert(MusicEntry.TABLE_NAME, null, cvMusic);

        ContentValues cvConnect = new ContentValues();
        cvConnect.put(ConnectEntry.COLUMN_PLAYLIST_ID, DEFAULT_PLAYLIST_ID);
        cvConnect.put(ConnectEntry.COLUMN_MUSIC_ID, id);

        this.database.insert(ConnectEntry.TABLE_NAME, null, cvConnect);

        this.playlistsFragment.swapCursor(getAllPlaylists());
        if (this.musicsFragment != null)
            this.musicsFragment.swapCursor(getAllMusic(this.musicsFragment.getPlaylistId()));

        Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();
    }

    public void insertPlaylist(String name, String color) {
        ContentValues cv = new ContentValues();
        cv.put(PlaylistEntry.COLUMN_NAME, name);
        cv.put(PlaylistEntry.COLUMN_COLOR, color);

        this.database.insert(PlaylistEntry.TABLE_NAME, null, cv);

        this.playlistsFragment.swapCursor(getAllPlaylists());
    }

    public void insertConnection(int playlistId, int musicId) {
        ContentValues cv = new ContentValues();
        cv.put(ConnectEntry.COLUMN_PLAYLIST_ID, playlistId);
        cv.put(ConnectEntry.COLUMN_MUSIC_ID, musicId);

        this.database.insert(ConnectEntry.TABLE_NAME, null, cv);

        this.playlistsFragment.swapCursor(getAllPlaylists());
        if (this.musicsFragment != null)
            this.musicsFragment.swapCursor(getAllMusic(this.musicsFragment.getPlaylistId()));

        closeChoosePlaylistFragment();

        Toast.makeText(this, "Music added to playlist", Toast.LENGTH_SHORT).show();
    }

    public void updatePlaylist(int playlistId, String newName, String newColor) {
        ContentValues cv = new ContentValues();
        cv.put(PlaylistEntry.COLUMN_NAME, newName);
        cv.put(PlaylistEntry.COLUMN_COLOR, newColor);

        this.database.update(PlaylistEntry.TABLE_NAME, cv, PlaylistEntry._ID + "=?", new String[]{String.valueOf(playlistId)});

        this.playlistsFragment.swapCursor(getAllPlaylists());
    }

    public void deleteMusic(Music music) {
        this.database.delete(MusicEntry.TABLE_NAME, MusicEntry._ID + "=?", new String[]{String.valueOf(music.getId())});
        this.database.delete(ConnectEntry.TABLE_NAME, ConnectEntry.COLUMN_MUSIC_ID + "=?", new String[]{String.valueOf(music.getId())});

        this.playlistsFragment.swapCursor(getAllPlaylists());

        if (this.musicsFragment != null)
            this.musicsFragment.swapCursor(getAllMusic(this.musicsFragment.getPlaylistId()));

        Toast.makeText(this, "Music deleted", Toast.LENGTH_SHORT).show();
    }

    public void deletePlaylist(int playlistId) {
        this.database.delete(PlaylistEntry.TABLE_NAME, PlaylistEntry._ID + "=?", new String[]{String.valueOf(playlistId)});
        this.database.delete(ConnectEntry.TABLE_NAME, ConnectEntry.COLUMN_PLAYLIST_ID + "=?", new String[]{String.valueOf(playlistId)});

        this.playlistsFragment.swapCursor(getAllPlaylists());

        Toast.makeText(this, "Playlist deleted", Toast.LENGTH_SHORT).show();
    }

    public void deleteConnection(int playlistId, int musicId) {
        this.database.delete(
                ConnectEntry.TABLE_NAME,
                ConnectEntry.COLUMN_PLAYLIST_ID + "=? AND " + ConnectEntry.COLUMN_MUSIC_ID + "=?",
                new String[]{String.valueOf(playlistId), String.valueOf(musicId)}
        );

        this.playlistsFragment.swapCursor(getAllPlaylists());

        if (this.musicsFragment != null)
            this.musicsFragment.swapCursor(getAllMusic(this.musicsFragment.getPlaylistId()));

        Toast.makeText(this, "Music removed from playlist", Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 9. Getters and Setters

    public Cursor getAllPlaylists() {
        return this.database.rawQuery(
                "SELECT " + PlaylistEntry._ID + ", " + PlaylistEntry.COLUMN_NAME + ", " + PlaylistEntry.COLUMN_COLOR + ", " +
                        "(SELECT COUNT(" + ConnectEntry.COLUMN_MUSIC_ID + ") FROM " + ConnectEntry.TABLE_NAME + ", " + MusicEntry.TABLE_NAME +
                        " WHERE " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                        " AND " + ConnectEntry.COLUMN_MUSIC_ID + "=" + MusicEntry.TABLE_NAME + "." + MusicEntry._ID +
                        ") AS " + PlaylistEntry.COLUMN_MUSICS +
                        " FROM " + PlaylistEntry.TABLE_NAME +
                        " ORDER BY " + PlaylistEntry.COLUMN_TIMESTAMP + " ASC;",
                null
        );
    }

    public Cursor getAllChoosePlaylists(int musicId) {
        final String SQL_SELECT_ALL_CHOOSE_PLAYLISTS = "SELECT DISTINCT " + PlaylistEntry._ID + ", " + PlaylistEntry.COLUMN_NAME + ", " + PlaylistEntry.COLUMN_COLOR + ", " +
                "(SELECT COUNT(" + ConnectEntry.COLUMN_MUSIC_ID + ") FROM " + ConnectEntry.TABLE_NAME + ", " + MusicEntry.TABLE_NAME +
                    " WHERE " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                    " AND " + ConnectEntry.COLUMN_MUSIC_ID + "=" + MusicEntry.TABLE_NAME + "." + MusicEntry._ID +
                ") AS " + PlaylistEntry.COLUMN_MUSICS +
                " FROM " + PlaylistEntry.TABLE_NAME +
                " WHERE " + PlaylistEntry._ID + " NOT IN " +
                "(SELECT " + ConnectEntry.COLUMN_PLAYLIST_ID +
                    " FROM " + ConnectEntry.TABLE_NAME +
                    " WHERE " + ConnectEntry.COLUMN_MUSIC_ID + "=?)" +
                " ORDER BY " + PlaylistEntry.COLUMN_TIMESTAMP + " ASC;";

        return this.database.rawQuery(
                SQL_SELECT_ALL_CHOOSE_PLAYLISTS,
                new String[]{String.valueOf(musicId)}
        );
    }

    public Cursor getAllMusic() {
        return this.database.query(
                MusicEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MusicEntry.COLUMN_TIMESTAMP + " ASC"
        );
    }

    public Cursor getAllMusic(int playlistId) {
        if (playlistId == DEFAULT_PLAYLIST_ID)
            return getAllMusic();

        final String SQL_SELECT_ALL_MUSIC = "SELECT " + MusicEntry.TABLE_NAME + ".*" + " FROM " +
                MusicEntry.TABLE_NAME + ", " + PlaylistEntry.TABLE_NAME + ", " + ConnectEntry.TABLE_NAME +
                " WHERE " + MusicEntry.TABLE_NAME + "." + MusicEntry._ID + "=" + ConnectEntry.COLUMN_MUSIC_ID +
                " AND " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                " AND " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=?" +
                " ORDER BY " + ConnectEntry.COLUMN_TIMESTAMP + " ASC;";

        return this.database.rawQuery(
                SQL_SELECT_ALL_MUSIC,
                new String[]{String.valueOf(playlistId)}
        );
    }

    public ArrayList<Music> getMusics(int playlistId) {
        ArrayList<Music> musics = new ArrayList<>();

        Cursor cursor = getAllMusic(playlistId);
        if (cursor.moveToFirst()) {
            do {
                Music currentMusic = new Music(
                        cursor.getInt(cursor.getColumnIndex(MusicPlayerContract.MusicEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_VIDEO_ID)),
                        cursor.getString(cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_ARTIST)),
                        cursor.getInt(cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_LENGTH))
                );
                musics.add(currentMusic);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return musics;
    }

    public void setBtnPlayImage(int resId) {
        this.btnPlayDown.setImageResource(resId);
        this.btnPlayUp.setImageResource(resId);
    }

    public void setMusicTexts(String title, String artist) {
        this.txtMusicTitleDown.setText(title);
        this.txtMusicArtistDown.setText(artist);
        this.txtMusicTitleUp.setText(title);
        this.txtMusicArtistUp.setText(artist);
    }

    @SuppressLint("SetTextI18n")
    public void setMusicSeekBar(int length) {
        this.seekBar.setMax(length);
        this.txtCurrentTime.setText("0:00");

        int minute = length / 60;
        int second = length % 60;
        String strMinute = String.valueOf(minute);
        String strSecond;
        if (second < 10)
            strSecond = "0" + second;
        else
            strSecond = String.valueOf(second);

        this.txtFinalTime.setText(strMinute+ ":" + strSecond);
    }

    @SuppressLint("SetTextI18n")
    public void setMusicSeekBarPosition(int position) {
        if (this.isPlayable && !this.slidingSeekBar) {
            this.seekBar.setProgress(position);
            int minute = position / 60;
            int second = position % 60;
            String strMinute = String.valueOf(minute);
            String strSecond;
            if (second < 10)
                strSecond = "0" + second;
            else
                strSecond = String.valueOf(second);

            this.txtCurrentTime.setText(strMinute+ ":" + strSecond);
        }
    }

    // endregion

}