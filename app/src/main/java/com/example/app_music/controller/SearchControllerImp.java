package com.example.app_music.controller;

import com.example.app_music.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchControllerImp implements SearchController{
    @Override
    public List<Song> searchByName(List<Song> songs, String songName) {
        List<Song> result = new ArrayList<>();
        for(Song song : songs) {
            if(isMatch(song.getName(), songName)) {
                result.add(song);
            }
        }
        return result;
    }

    @Override
    public List<Song> searchByArtist(List<Song> songs, String songArtist) {
        List<Song> result = new ArrayList<>();
        for(Song song : songs) {
            if(isMatch(song.getArtist(), songArtist)) {
                result.add(song);
            }
        }
        return result;
    }

    private boolean isMatch(String firstName, String key) {
        String pattern = ".*" + key + ".*";
        Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(firstName);
        return matcher.matches();
    }
}
