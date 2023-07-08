package com.example.app_music.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Song implements Serializable {
    @SerializedName("title")
    private String name;
    @SerializedName("artist")
    private String artist;

    @SerializedName("image")
    private String avatar;
    @SerializedName("source")
    private String idSong;

    public Song() {
    }

    public Song(String name, String artist, String avatar, String idSong) {
        this.name = name;
        this.artist = artist;
        this.avatar = avatar;
        this.idSong = idSong;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdSong() {
        return idSong;
    }

    public void setIdSong(String idSong) {
        this.idSong = idSong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return idSong == song.idSong;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSong);
    }
}
