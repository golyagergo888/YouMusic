package com.fokakefir.musicplayer.logic.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    // region 0. Constants

    public static final String CHANNEL_ID = "notification_channel";

    // endregion

    // region 1. Decl. and Init

    // endregion

    // region 2. Lifecycle

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    // endregion

    // region 3. Notification

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("This is notification channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }

    // endregion

}
