package com.example.app_music.service.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongList {
    @SerializedName("song")
    private List<Song> songs;

    public SongList() {
    }

    public SongList(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
