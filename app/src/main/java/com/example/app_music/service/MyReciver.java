package com.example.app_music.service;

import static com.example.app_music.service.MyService.ACTION_MUSIC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciver extends BroadcastReceiver {
    // remember to declare reciver in AndroidManifest
    public static final String ACTION_MUSIC_RESPOND = "ACTION_MUSIC_RESPOND";
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra(ACTION_MUSIC, 0);
        Intent intent1 = new Intent(context, MyService.class);
        intent1.putExtra(ACTION_MUSIC_RESPOND, actionMusic);
        context.startService(intent1);
    }
}
