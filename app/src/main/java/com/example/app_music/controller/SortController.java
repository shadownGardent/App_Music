package com.example.app_music.controller;

import com.example.app_music.service.model.Song;

import java.util.List;

public interface SortController {
    void sortByNameASC(List<Song> songs);
    void sortByNameDESC(List<Song> songs);
}
