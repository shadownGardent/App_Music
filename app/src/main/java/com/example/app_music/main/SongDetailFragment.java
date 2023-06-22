package com.example.app_music.main;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.app_music.R;
import com.example.app_music.model.Song;
import com.example.app_music.parser.Utils;
import com.example.app_music.viewmodel.ListSongViewModel;
import com.example.app_music.viewmodel.SongItemViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SongDetailFragment extends Fragment implements View.OnClickListener {
    private ImageView imageAvatarDetail;
    private TextView textSongNameDetail;
    private TextView textArtistDetail;
    private Toolbar toolbar;
    private ImageButton btnPlay;
    private ImageButton btnPre;
    private ImageButton btnNext;
    private ImageButton btnMix;
    private ImageButton btnRepeat;
    public static MediaPlayer mediaPlayer;
    private TextView textTimeStart;
    private TextView textTimeEnd;
    private SeekBar seekBar;
    private ListSongViewModel viewModel;
    public static SongItemViewModel model;
    private static boolean isRepeat = false;
    private static boolean isRandom = false;
    private static boolean isPlaying = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ListSongsFragment.class, null)
                        .addToBackStack("ListSongFragment")
                        .commit();
                MainActivity.layoutBottom.setVisibility(View.VISIBLE);
                if(mediaPlayer.isPlaying()) {
                    MainActivity.imgPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_54));
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_details_song, container, false);
        setUpToolBar(view);
        return view;
    }

    private void setUpToolBar(View view) {
        toolbar = view.findViewById(R.id.toolbar_song_detail);
        toolbar.inflateMenu(R.menu.action_menu);
        toolbar.setTitle(getString(R.string.name_song));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_more_vert) {
                Toast.makeText(requireContext(), "Chức năng hiện chưa khả dụng", Toast.LENGTH_SHORT).show();
            }
            return super.onOptionsItemSelected(item);
        });
        setHasOptionsMenu(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initsView(view);
        model = new ViewModelProvider(requireActivity()).get(SongItemViewModel.class);
        model.getSelectedSong().observe(getViewLifecycleOwner(), this::showData);
        // khoi tao mediaplayer
        creatMediaPlayer(model);
        isPlaying = true;
        if (isRandom) {
            btnMix.setImageResource(R.drawable.ic_compare_arrows_purple_32);
        }
        if (isRepeat) {
            btnRepeat.setImageResource(R.drawable.ic_repeat_purple_32);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void creatMediaPlayer(SongItemViewModel model) {
        if (isPlaying) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(Objects.requireNonNull(model.getSelectedSong().getValue()).getIdSong());
            mediaPlayer.prepare(); // Chuẩn bị MediaPlayer cho phát nhạc
        } catch (IOException e) {
        }
        btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_54));
        mediaPlayer.start();
        setTotalTime();
        updateTimeSong();
        startAnimation();
    }

    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageAvatarDetail.animate()
                        .rotationBy(360)
                        .withEndAction(this)
                        .setDuration(17000)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        };
        imageAvatarDetail.animate()
                .rotationBy(360)
                .withEndAction(runnable)
                .setDuration(17000)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    public void stopAnimation() {
        imageAvatarDetail.animate().cancel();
    }

    private void updateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                textTimeStart.setText(format.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                if (seekBar.getProgress() == mediaPlayer.getDuration()) {
                    btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_54));
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewModel = ListSongViewModel.getInstance();
                        List<Song> songs = new ArrayList<>(Objects.requireNonNull(viewModel.getSongs().getValue()));
                        if (isRepeat) {
                            mp.seekTo(0);
                            mp.start();
                        } else if (isRandom) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                            }
                            Random random = new Random();
                            int index = random.nextInt(songs.size());
                            model.setSelectedSong(songs.get(index));

                        } else {
                            int position = 0;
                            for (int i = 0; i < songs.size(); i++) {
                                if (songs.get(i).equals(model.getSelectedSong().getValue())) {
                                    position = i;
                                }
                            }
                            position++;
                            if (position > songs.size() - 1) {
                                position = 0;
                            }
                            model.setSelectedSong(songs.get(position));
                        }
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private void setTotalTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        textTimeEnd.setText(format.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void showData(Song song) {
        if (song == null) {
            Glide.with(this)
                    .load(R.drawable.ic_error_24)
                    .into(imageAvatarDetail);
            textArtistDetail.setText(getString(R.string.text_no_data));
            textSongNameDetail.setText(getString(R.string.text_no_data));
        } else {
            Glide.with(this)
                    .load(song.getAvatar())
                    .circleCrop()
                    .into(imageAvatarDetail);
            textSongNameDetail.setText(song.getName());
            textArtistDetail.setText(song.getArtist());
        }


    }

    private void initsView(View view) {
        imageAvatarDetail = view.findViewById(R.id.image_avatar_details);
        textSongNameDetail = view.findViewById(R.id.text_name_details);
        textArtistDetail = view.findViewById(R.id.text_artist_details);
        btnMix = view.findViewById(R.id.image_btn_mix);
        btnNext = view.findViewById(R.id.image_btn_next);
        btnPlay = view.findViewById(R.id.image_btn_play);
        btnRepeat = view.findViewById(R.id.image_btn_repeat);
        btnPre = view.findViewById(R.id.image_btn_pre);
        textTimeStart = view.findViewById(R.id.text_time_start);
        textTimeEnd = view.findViewById(R.id.text_time_end);
        seekBar = view.findViewById(R.id.seek_bar_time);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnMix.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        viewModel = ListSongViewModel.getInstance();
        List<Song> songs = new ArrayList<>(Objects.requireNonNull(viewModel.getSongs().getValue()));
        if (v.getId() == R.id.image_btn_play) {
            if (mediaPlayer.isPlaying()) {
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_54));
                mediaPlayer.pause();
                stopAnimation();
            } else {
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_54));
                mediaPlayer.start();
                startAnimation();

            }
        } else if (v.getId() == R.id.image_btn_next) {
            int position = 0;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).equals(model.getSelectedSong().getValue())) {
                    position = i;
                }
            }
            position++;
            if (position > songs.size() - 1) {
                position = 0;
            }
            model.setSelectedSong(songs.get(position));
        } else if (v.getId() == R.id.image_btn_pre) {
            int index = 0;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).equals(model.getSelectedSong().getValue())) {
                    index = i;
                }
            }
            index--;
            if (index < 0) {
                index = songs.size() - 1;
            }
            model.setSelectedSong(songs.get(index));
        } else if (v.getId() == R.id.image_btn_repeat) {
            if (!isRepeat) {
                isRepeat = true;
                btnRepeat.setImageResource(R.drawable.ic_repeat_purple_32);
                if (isRandom) {
                    isRandom = false;
                    btnMix.setImageResource(R.drawable.ic_compare_arrows_32);
                }
            } else {
                isRepeat = false;
                btnRepeat.setImageResource(R.drawable.ic_repeat_32);
            }
        } else if (v.getId() == R.id.image_btn_mix) {
            if (!isRandom) {
                isRandom = true;
                btnMix.setImageResource(R.drawable.ic_compare_arrows_purple_32);
                if (isRepeat) {
                    isRepeat = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_32);
                }
            } else {
                isRandom = false;
                btnMix.setImageResource(R.drawable.ic_compare_arrows_32);
            }
        }
    }


}
