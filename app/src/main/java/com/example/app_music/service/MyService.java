package com.example.app_music.service;

import static com.example.app_music.main.MainActivity.KEY_SONG;
import static com.example.app_music.service.MyApplication.CHANEL_ID;
import static com.example.app_music.service.MyReciver.ACTION_MUSIC_RESPOND;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.app_music.R;
import com.example.app_music.main.MainActivity;
import com.example.app_music.main.SongDetailFragment;
import com.example.app_music.service.model.Song;
import com.example.app_music.viewmodel.ListSongViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MyService extends Service {
    public static final int ACTION_START = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_PRE = 3;
    public static final int ACTION_NEXT = 4;

    public static final String ACTION_MUSIC = "ACTION_MUSIC";
    public static final String STATUS_MEDIA = "STATUS_MEDIA";
    public static final String OBJETC_SONG = "OBJETC_SONG";
    public static final String SEND_DATA_TO_ACTIVITY = "SEND_DATA_TO_ACTIVITY";
    private boolean isPlaying = false;
    private Song mSong;
    private ListSongViewModel viewModel;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("App_Music", "MyService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get(KEY_SONG);
            if (song != null) {
                mSong = song;
                isPlaying = true;
                sendNotificationMedia(song);
                sendActionToActivity(ACTION_START);
            }
        }
        // lấy action bên reciver để sử lí sự kiện button trên notification được click
        int actionMusic = intent.getIntExtra(ACTION_MUSIC_RESPOND, 0);
        handleActionMusic(actionMusic);
        return START_NOT_STICKY;
    }


//    private void sendNotification(Song song) {
//        Intent intent = new Intent(this, MainActivity.class);
//        @SuppressLint("InlinedApi")
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, intent, PendingIntent.FLAG_MUTABLE);
//
//
//        @SuppressLint("RemoteViewLayout")
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
//        remoteViews.setTextViewText(R.id.txt_name_notify, song.getName());
//        remoteViews.setTextViewText(R.id.txt_artist_notify, song.getArtist());
//        remoteViews.setImageViewResource(R.id.img_avatar_notfify, Utils.getDrawableId(this, song.getAvatar()));
//
//        if (isPlaying) {
//            remoteViews.setOnClickPendingIntent(R.id.img_play_notify, getPedingIntent(this, ACTION_PAUSE));
//            remoteViews.setImageViewResource(R.id.img_play_notify, R.drawable.ic_pause_black_54);
//        } else {
//            remoteViews.setOnClickPendingIntent(R.id.img_play_notify, getPedingIntent(this, ACTION_RESUME));
//            remoteViews.setImageViewResource(R.id.img_play_notify, R.drawable.ic_play_black_54);
//        }
//
//        remoteViews.setOnClickPendingIntent(R.id.img_pre_notify, getPedingIntent(this, ACTION_PRE));
//        remoteViews.setOnClickPendingIntent(R.id.img_next_notify, getPedingIntent(this, ACTION_NEXT));
//
//        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
//                .setSmallIcon(R.mipmap.app_symbol)
//                .setContentIntent(pendingIntent)
//                .setCustomContentView(remoteViews)
//                .build();
//
//        startForeground(1, notification);
//
//    }

    // set action cho các button trên remoteview bằng cách gửi PendingIntent sang broadcastReciver
    private PendingIntent getPedingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReciver.class);
        intent.putExtra(ACTION_MUSIC, action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_MUTABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("App_Music", "MyService onDestroy");
        stopSelf();
    }

    // xử lý sự kiện khi người dùng click button trong notification
    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_PRE:
                preMusic();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
        }
    }

    private void nextMusic() {
        viewModel = ListSongViewModel.getInstance();
        List<Song> songs = new ArrayList<>(Objects.requireNonNull(viewModel.getSongs().getValue()));
        int position = 0;
        if (SongDetailFragment.mediaPlayer.isPlaying()) {
            SongDetailFragment.mediaPlayer.stop();
        }
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).equals(SongDetailFragment.model.getSelectedSong().getValue())) {
                position = i;
            }
        }
        position++;
        if (position > songs.size() - 1) {
            position = 0;
        }
        SongDetailFragment.model.setSelectedSong(songs.get(position));
//        sendNotificationMedia(mSong);
        sendActionToActivity(ACTION_NEXT);
    }

    private void preMusic() {
        viewModel = ListSongViewModel.getInstance();
        List<Song> songs = new ArrayList<>(Objects.requireNonNull(viewModel.getSongs().getValue()));
        int position = 0;
        if (SongDetailFragment.mediaPlayer.isPlaying()) {
            SongDetailFragment.mediaPlayer.stop();
        }
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).equals(SongDetailFragment.model.getSelectedSong().getValue())) {
                position = i;
            }
        }
        position--;
        if (position < 0) {
            position = songs.size() - 1;
        }
        SongDetailFragment.model.setSelectedSong(songs.get(position));
//        sendNotificationMedia(mSong);
        sendActionToActivity(ACTION_PRE);
    }

    private void pauseMusic() {
        if (SongDetailFragment.mediaPlayer.isPlaying() && isPlaying) {
            SongDetailFragment.mediaPlayer.pause();
            isPlaying = false;
            sendNotificationMedia(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }

    private void resumeMusic() {
        if (!isPlaying) {
            SongDetailFragment.mediaPlayer.start();
            isPlaying = true;
            sendNotificationMedia(mSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    // send action to activity
    private void sendActionToActivity(int action) {
        Intent intent = new Intent(SEND_DATA_TO_ACTIVITY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBJETC_SONG, mSong);
        bundle.putBoolean(STATUS_MEDIA, isPlaying);
        bundle.putInt(ACTION_MUSIC, action);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        // gửi intent sang broadcastReciver
    }

    private void sendNotificationMedia(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("InlinedApi")
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_MUTABLE);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Utils.getDrawableId(this,song.getAvatar()));



        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.drawable.app_symbol_2)
                .setSubText("Notification")
                .setContentIntent(pendingIntent)
                .setContentTitle(song.getName())
                .setContentText(song.getArtist())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()));

        Picasso.get().load(song.getAvatar()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                notificationBuilder.setLargeIcon(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        if(isPlaying) {
            notificationBuilder// set button control
                    .addAction(R.drawable.ic_skip_previous_48, "Previous", getPedingIntent(this, ACTION_PRE))
                    .addAction(R.drawable.ic_pause_circle_outline_48, "Pause", getPedingIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.ic_skip_next_48, "Next", getPedingIntent(this, ACTION_NEXT));
        }else {
            notificationBuilder// set button control
                    .addAction(R.drawable.ic_skip_previous_48, "Previous", getPedingIntent(this, ACTION_PRE))
                    .addAction(R.drawable.ic_play_circle_outline_48, "Pause", getPedingIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.ic_skip_next_48, "Next", getPedingIntent(this, ACTION_NEXT));
        }

        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }
}
