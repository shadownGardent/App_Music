package com.example.app_music.controller;

import com.example.app_music.model.Song;

import java.util.List;

public interface SearchController {
    List<Song> searchByName(List<Song> songs, String songName);
    List<Song> searchByArtist(List<Song> songs, String songArtist);

}
