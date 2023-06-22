package com.example.app_music.main;

import static com.example.app_music.service.MyReciver.ACTION_MUSIC_RESPOND;
import static com.example.app_music.service.MyService.ACTION_MUSIC;
import static com.example.app_music.service.MyService.ACTION_NEXT;
import static com.example.app_music.service.MyService.ACTION_PAUSE;
import static com.example.app_music.service.MyService.ACTION_PRE;
import static com.example.app_music.service.MyService.ACTION_RESUME;
import static com.example.app_music.service.MyService.ACTION_START;
import static com.example.app_music.service.MyService.OBJETC_SONG;
import static com.example.app_music.service.MyService.SEND_DATA_TO_ACTIVITY;
import static com.example.app_music.service.MyService.STATUS_MEDIA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.app_music.R;
import com.example.app_music.model.Song;
import com.example.app_music.parser.Utils;
import com.example.app_music.service.MyService;
import com.example.app_music.viewmodel.SongItemViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_MEDIA = 1;
    public static final String KEY_SONG = "KEY_SONG";
    private SongItemViewModel itemViewModel;
    private View layout;
    public static RelativeLayout layoutBottom;
    public static ImageView imgAvatar, imgPre, imgPlay, imgNext;
    private TextView txtName, txtArtist;
    private Song mSong;
    private boolean isPlaying;
    private boolean isMain;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }
            mSong = (Song) bundle.get(OBJETC_SONG);
            isPlaying = bundle.getBoolean(STATUS_MEDIA);
            int actionMusic = bundle.getInt(ACTION_MUSIC);
            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        initView();
        itemViewModel = new ViewModelProvider(this)
                .get(SongItemViewModel.class);
        itemViewModel.getSelectedSong().observe(this, this::showDetail);
        if (savedInstanceState == null) {
            addFragment();
        }
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(SEND_DATA_TO_ACTIVITY));
    }

    private void initView() {
        layoutBottom = findViewById(R.id.layout_bottom);
        imgAvatar = findViewById(R.id.img_avatar_notfify);
        imgPre = findViewById(R.id.img_pre_notify);
        imgNext = findViewById(R.id.img_next_notify);
        imgPlay = findViewById(R.id.img_play_notify);
        txtName = findViewById(R.id.txt_name_notify);
        txtArtist = findViewById(R.id.txt_artist_notify);
        imgPlay.setOnClickListener(v-> {
            if(isPlaying) {
                sendActionToService(ACTION_PAUSE);
            }else {
                sendActionToService(ACTION_RESUME);
            }
        });

        imgNext.setOnClickListener(v-> sendActionToService(ACTION_NEXT));
        imgPre.setOnClickListener(v-> sendActionToService(ACTION_PRE));
    }

    private void addFragment() {
        isMain = true;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ListSongsFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void showDetail(Song song) {
        showMediaPreview();
        startMyService();
    }

    private void startMyService() {
        Song song = itemViewModel.getSelectedSong().getValue();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SONG, song);
        Intent intent = new Intent(this, MyService.class);
        intent.putExtras(bundle);
        startService(intent);
    }

    private void showMediaPreview() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startMedia();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(
                            layout, R.string.media_accsess_required, Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                            R.string.oke, view -> requestPermission()
                    ).show();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private void startMedia() {
        isMain = false;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in, // enter
                        R.anim.fade_out, // exit
                        R.anim.fade_in, // popEnter
                        R.anim.slide_out // popExit
                )
                .replace(R.id.fragment_container, SongDetailFragment.class, null)
                .addToBackStack("CountryDetailFragment")
                .commit();
        layoutBottom.setVisibility(View.GONE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_MEDIA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_MEDIA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(layout, R.string.media_pemission_granted, BaseTransientBottomBar.LENGTH_SHORT).show();
                startMedia();
            } else {
                Snackbar.make(layout, R.string.media_pemission_denied, BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private void handleLayoutMusic(int action) {
        switch (action) {
            case ACTION_START:
                if(isMain) {
                    layoutBottom.setVisibility(View.VISIBLE);
                }
                showInfoSong(mSong);
                setStatusBtnPlayOrPause();
                break;
            case ACTION_PAUSE:
            case ACTION_RESUME:
                setStatusBtnPlayOrPause();
                break;
            case ACTION_PRE:
            case ACTION_NEXT:
                showInfoSong2();
                break;
        }
    }

    private void showInfoSong(Song song) {
        if(song == null) {
            return;
        }
//        imgAvatar.setImageResource(Utils.getDrawableId(getApplicationContext(), song.getAvatar()));
        Picasso.get().load(song.getAvatar()).into(imgAvatar);
        txtName.setText(song.getName());
        txtArtist.setText(song.getArtist());
    }

    private void showInfoSong2() {
        Song song = itemViewModel.getSelectedSong().getValue();
        if(song == null) {
            return;
        }
//        imgAvatar.setImageResource(Utils.getDrawableId(getApplicationContext(), song.getAvatar()));
        Picasso.get().load(song.getAvatar()).into(imgAvatar);
        txtName.setText(song.getName());
        txtArtist.setText(song.getArtist());
    }

    private void setStatusBtnPlayOrPause() {
        if(isPlaying) {
            imgPlay.setImageResource(R.drawable.ic_pause_circle_outline_54);
        }else {
            imgPlay.setImageResource(R.drawable.ic_play_circle_outline_54);
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra(ACTION_MUSIC_RESPOND, action);
        startService(intent);
    }
}
