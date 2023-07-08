package com.example.app_music.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app_music.service.model.Song;

public class SongItemViewModel extends ViewModel {
    private final MutableLiveData<Song> selectedSong = new MutableLiveData<>();
    public void setSelectedSong(Song song) {
        selectedSong.setValue(song);
    }

    public LiveData<Song> getSelectedSong(){
        return selectedSong;
    }

}
