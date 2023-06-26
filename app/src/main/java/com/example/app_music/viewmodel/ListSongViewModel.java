package com.example.app_music.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app_music.apiservice.ApiService;
import com.example.app_music.model.Song;
import com.example.app_music.model.SongList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListSongViewModel extends ViewModel {
    private static ListSongViewModel instance;
    private final MutableLiveData<List<Song>> liveDataSong;
    private List<Song> songList;

    private ListSongViewModel() {
        liveDataSong = new MutableLiveData<>();
        songList = new ArrayList<>();
    }

    public static ListSongViewModel getInstance() {
        if (instance == null) {
            instance = new ListSongViewModel();
        }
        return instance;
    }

    public LiveData<List<Song>> getSongs() {
        return liveDataSong;
    }

    public void loadData() {
        songList.clear();
        ApiService.apiService.getListSong().enqueue(new Callback<SongList>() {
            @Override
            public void onResponse(Call<SongList> call, Response<SongList> response) {
                assert response.body() != null;
                songList.addAll(response.body().getSongs());
                if (songList.size() != 0) {
                    liveDataSong.setValue(songList);
                }
            }

            @Override
            public void onFailure(Call<SongList> call, Throwable t) {
            }
        });
    }
}
