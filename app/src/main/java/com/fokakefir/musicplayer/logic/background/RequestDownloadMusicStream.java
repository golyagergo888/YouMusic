package com.fokakefir.musicplayer.logic.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.logic.notification.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ThreadLocalRandom;

public class RequestDownloadMusicStream extends AsyncTask<String, String, String> {

    // region 0. Constants

    public static final int PROGRESS_MAX = 100;
    public static final int NOTIFICATION_ID_MAX = 1000000;

    // endregion

    // region 1. Decl and Init

    private RequestDownloadMusicStreamResponse response;
    private Context context;

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId;

    private String videoId;
    private String title;
    private String artist;

    // endregion

    // region 2. Constructor and Lifecycle

    public RequestDownloadMusicStream(RequestDownloadMusicStreamResponse response, Context context) {
        this.response = response;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.notificationId = ThreadLocalRandom.current().nextInt(2, NOTIFICATION_ID_MAX + 1);

        this.notificationManager = NotificationManagerCompat.from(this.context);

        this.notificationBuilder = new NotificationCompat.Builder(this.context, App.CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_baseline_download_24)
                .setContentTitle("Download")
                .setContentText("Download in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(PROGRESS_MAX, 0, false);

        this.notificationManager.notify(this.notificationId, this.notificationBuilder.build());
    }

    @Override
    protected String doInBackground(String... params) {
        this.videoId = params[1];
        this.title = params[2];
        this.artist = params[3];

        InputStream inputStream = null;
        URL url = null;
        int len1 = 0;
        int progress = 0;
        try {
            url = new URL(params[0]);
            inputStream = url.openStream();
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int size = urlConnection.getContentLength();

            String fileName = params[1] + MainActivity.AUDIO_FORMAT;
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

                    progress = (int) (((double) total / size) * 100);
                    if(progress >= 0) {
                        publishProgress("" + progress);
                    }

                    fileOutputStream.write(buffer, 0, len1);
                }
            }

            publishProgress("" + 100);
            fileOutputStream.close();
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
        int progress = Integer.parseInt(values[0]);
        this.notificationBuilder.setProgress(PROGRESS_MAX, progress, false);
        this.notificationManager.notify(this.notificationId, this.notificationBuilder.build());
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.notificationBuilder.setContentText("Download finished")
                .setProgress(0, 0, false)
                .setOngoing(false);

        this.notificationManager.notify(this.notificationId, this.notificationBuilder.build());

        if (this.response != null)
            this.response.onMusicDownloaded(
                    this.videoId, this.title, this.artist, getDuration(this.videoId)
            );
    }

    // endregion

    // region 3. Getters and Setters

    private int getDuration(String videoId) {
        String fileName = videoId + MainActivity.AUDIO_FORMAT;
        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeMusics";

        String path = storagePath + "/" + fileName;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        String strDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        int duration = Integer.parseInt(strDuration.substring(0, strDuration.length() - 3));
        return duration;
    }

    // endregion

}
