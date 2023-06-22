package com.example.app_music.controller;

import android.icu.text.RuleBasedCollator;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.app_music.model.Song;

import java.util.Collections;
import java.util.List;

public class SortControllerImp implements SortController {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sortByNameASC(List<Song> songs) {
        String rules = "&a<á<à<ả<ã<ạ<ă<ắ<ằ<ẳ<ẵ<ặ<â<ấ<ầ<ẩ<ẫ<ậ" +
                "<b<c<d<đ<e<é<è<ẻ<ẽ<ẹ<ê<ế<ề<ể<ễ<ệ<f<g<h" +
                "<i<í<ì<ỉ<ĩ<ị<j<k<l<m<n<o<ó<ò<ỏ<õ<ọ<ô<ố<ồ<ỗ" +
                "<ộ<ơ<ớ<ờ<ở<ỡ<ợ<p<q<r<s<t" +
                "<u<ú<ù<ủ<ũ<ụ<ư<ứ<ừ<ử<ữ" +
                "<v<w<x<y<ý<ỳ<ỷ<ỹ<ỵ<z";
        try {
            RuleBasedCollator collator = new RuleBasedCollator(rules);
            songs.sort((s1, s2) -> {
                return collator.compare(s1.getName().toLowerCase(),
                        s2.getName().toLowerCase());
            });

        } catch (Exception e) {
            Log.e("ParseException", e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sortByNameDESC(List<Song> songs) {
        sortByNameASC(songs);
        Collections.reverse(songs);
    }
}
