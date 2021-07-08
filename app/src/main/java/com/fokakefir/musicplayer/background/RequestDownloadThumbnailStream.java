package com.fokakefir.musicplayer.background;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestDownloadThumbnailStream extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressDialog progressDialog;

    public RequestDownloadThumbnailStream(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(this.context);
    }

    @Override
    public void onPreExecute() {
        super .onPreExecute();
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoutubeThumbnails");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        DownloadManager manager = (DownloadManager) this.context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(params[0]);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        String fileName = params[1];

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName + ".jpg")
                .setDestinationInExternalFilesDir(this.context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoutubeThumbnails",  fileName + ".jpg");

        manager.enqueue(request);
        return dir.getAbsolutePath() + File.separator + fileName + ".jpg";
    }

    @Override
    public void onPostExecute(String s) {
        super .onPostExecute(s);
        progressDialog.dismiss();
        Toast.makeText(this.context, "Image Saved", Toast.LENGTH_SHORT).show();
    }

}
