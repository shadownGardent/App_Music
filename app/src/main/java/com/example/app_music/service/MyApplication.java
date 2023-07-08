package com.example.app_music.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.app_music.R;

public class MyApplication extends Application {
    public static final String CHANEL_ID = "CHANEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        createChanelNotification();
    }

    private void createChanelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANEL_ID,
                    getString(R.string.music_service), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
           if(manager != null) {
               manager.createNotificationChannel(channel);
           }
        }
    }
}
